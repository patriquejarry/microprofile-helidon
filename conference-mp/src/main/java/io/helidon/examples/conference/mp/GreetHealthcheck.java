package io.helidon.examples.conference.mp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped
public class GreetHealthcheck implements HealthCheck {
	private final GreetingProvider provider;

	@Inject
	public GreetHealthcheck(final GreetingProvider provider) {
		this.provider = provider;
	}

	@Override
	public HealthCheckResponse call() {
		final String message = provider.getMessage();
		return HealthCheckResponse.named("greeting")
			.state("Hello".equals(message))
			.withData("greeting", message)
			.build();
	}
}