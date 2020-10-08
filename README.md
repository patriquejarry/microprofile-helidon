# Web Development with Helidon

## Subjects
- Using Config
- Metrics
- Health checks
- Connect the services
- Tracing
- Fault Tolerance
- Static content
- Build Native Image
- Deploy to Kubernetes (OKE)

## Generate the Conference SE project

`mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=io.helidon.archetypes -DarchetypeArtifactId=helidon-quickstart-se -DarchetypeVersion=1.0.0 -DgroupId=io.helidon.examples -DartifactId=conference-se -Dpackage=io.helidon.examples.conference.se`
		
## Generate the Conference MP project

`mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=io.helidon.archetypes -DarchetypeArtifactId=helidon-quickstart-mp -DarchetypeVersion=1.0.0 -DgroupId=io.helidon.examples -DartifactId=conference-mp -Dpackage=io.helidon.examples.conference.mp`

## Sanity check

- Check conference-se :
	- `cd conference-se`
	- `mvn clean package`
	- `java -jar target/conference-se.jar`
	- `curl http://localhost:8080/greet`
		- {"message":"Hello World!"}
	- Stop the Java process

- Check conference-mp :
	- `cd ../conference-mp`
	- `mvn clean package`
	- `java -jar target/conference-mp.jar`
	- `curl http://localhost:8080/greet`
		- {"message":"Hello World!"}
	- Stop the Java process

## Docker
- **Zipkin** : `docker run -d -p 9411:9411 openzipkin/zipkin`

## URLs
- Helidon SE : 8080
- Helidon MP : 8081
- Greet
	- http://localhost:808X/greet -> {"message":"Hello World!"}
	- http://localhost:808X/greet/{name} -> {"message":"Hello Name!"}
	- http://localhost:808X/greet/greeting -> (PUT) {"greeting":"Hi"} -> {"message":"Hi World!"}
- Metrics
	- http://localhost:808X/metrics
	- http://localhost:808X/metrics/base
	- http://localhost:808X/metrics/vendor
	- http://localhost:808X/metrics/application
	- http://localhost:808X/metrics/application/greet.default.counter
	- http://localhost:8081/metrics/application/io.helidon.examples.conference.mp.GreetResource.getDefaultMessage
- Health
	- http://localhost:808X/health
- Connect the services
	- http://localhost:8081/greet/outbound/jack
- Static content
	- http://localhost:808X/index.html
- Tracing
	- http://localhost:9411 (zipkin)

## Useful
- `curl -s http://localhost:8080/health | json_pp`
- `curl -i -X PUT -H "Content-Type: application/json" -d '{"greeting":"Hello Helidon"}' http://localhost:8081/greet/greeting`
- Package java
	- `mvn package`
	- `java -jar target/conference-mp.jar`
	- `java -jar target/conference-se.jar`
- Native image
	- `mvn package -P native-image`
	- `target/conference-mp`
	- `target/conference-se`

## SPECIAL THANKS TO 
* Tutorial : [Helidon tutorial](https://github.com/nagypeter/helidon-tutorial)
* Instructor : [Peter Nagy](https://github.com/nagypeter)
