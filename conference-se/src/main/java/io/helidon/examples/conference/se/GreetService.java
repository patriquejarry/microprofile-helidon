/*
 * Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.examples.conference.se;

import java.util.Collections;
import java.util.function.Supplier;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricRegistry;

import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.metrics.RegistryFactory;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

/**
 * A simple service to greet you. Examples:
 *
 * Get default greeting message: curl -X GET http://localhost:8080/greet
 *
 * Get greeting message for Joe: curl -X GET http://localhost:8080/greet/Joe
 *
 * Change greeting curl -X PUT -H "Content-Type: application/json" -d
 * '{"greeting" : "Howdy"}' http://localhost:8080/greet/greeting
 *
 * The message is returned as a JSON object
 */

public class GreetService implements Service {

	/**
	 * The config value for the key {@code greeting}.
	 */
	private String greeting;

	private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

	private final Supplier<String> greetingSupplier;

	private final Counter defaultMessageCounter;

	GreetService(final Config config) {
		this.greeting = config.get("app.greeting").asString().orElse("Ciao");
		this.greetingSupplier = config.get("app.greeting").asString().supplier("Ciao");

		final RegistryFactory metricsRegistry = RegistryFactory.getRegistryFactory().get();
		final MetricRegistry appRegistry = metricsRegistry.getRegistry(MetricRegistry.Type.APPLICATION);
		this.defaultMessageCounter = appRegistry.counter("greet.default.counter");
	}

	/**
	 * A service registers itself by updating the routine rules.
	 *
	 * @param rules the routing rules.
	 */
	@Override
	public void update(final Routing.Rules rules) {
		rules.get("/", this::getDefaultMessageHandler)
			.get("/{name}", this::getMessageHandler)
			.put("/greeting", this::updateGreetingHandler);
	}

	/**
	 * Return a wordly greeting message.
	 *
	 * @param request  the server request
	 * @param response the server response
	 */
	private void getDefaultMessageHandler(final ServerRequest request, final ServerResponse response) {
		defaultMessageCounter.inc();
		sendResponse(response, "World");
	}

	/**
	 * Return a greeting message using the name that was provided.
	 *
	 * @param request  the server request
	 * @param response the server response
	 */
	private void getMessageHandler(final ServerRequest request, final ServerResponse response) {
		final String name = request.path().param("name");
		sendResponse(response, name);
	}

	private void sendResponse(final ServerResponse response, final String name) {
		String msg = String.format("%s %s!", greeting, name);
		System.out.println("Greeting from Config : " + msg);
		msg = String.format("%s %s!", greetingSupplier.get(), name);
		System.out.println("Greeting from Config.Supplier : " + msg);

		final JsonObject returnObject = JSON.createObjectBuilder().add("message", msg).build();
		response.send(returnObject);
	}

	private void updateGreetingFromJson(final JsonObject jo, final ServerResponse response) {

		if (!jo.containsKey("greeting")) {
			final JsonObject jsonErrorObject = JSON.createObjectBuilder().add("error", "No greeting provided").build();
			response.status(Http.Status.BAD_REQUEST_400).send(jsonErrorObject);
			return;
		}

		greeting = jo.getString("greeting");
		response.status(Http.Status.NO_CONTENT_204).send();
	}

	/**
	 * Set the greeting to use in future messages.
	 *
	 * @param request  the server request
	 * @param response the server response
	 */
	private void updateGreetingHandler(final ServerRequest request, final ServerResponse response) {
		request.content()
			.as(JsonObject.class)
			.thenAccept(jo -> updateGreetingFromJson(jo, response));
	}

}
