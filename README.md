# 🌍 Spring Boot Kafka Avro + CloudEvents Example

This project demonstrates how to use **Spring Boot**, **Kafka**, **Avro**, and **CloudEvents SDK** to produce and consume events.

---

## 🚀 1. Build the Spring Boot App

First, build your Spring Boot application into a Docker image. 
Navigate to the root directory of your project (where `pom.xml` and `Dockerfile` are located, assuming you have one, or the `docker-compose.yml` points to a build context) 
and run:

```bash
./mvnw clean install
```
This command compiles your Java code, runs tests, and packages your application (e.g., into a JAR file).

Next, build the Docker image for your Spring Boot application:

```bash
docker build -t my-spring-boot-app:latest .
```

This command creates a Docker image tagged `my-spring-boot-app:latest`, which will be used by your `docker-compose.yml`.

--- 

## 🐳 2. Start Kafka and Services

Make sure Docker is running, then start the entire Kafka stack and your Spring Boot application:

```bash
docker compose up -d
```

This command starts all services defined in your `docker-compose.yml`, including:

 - Zookeeper: Coordinates Kafka brokers.
 - Kafka Broker: The messaging system.
 - Schema Registry: Manages Avro schemas.
 - Kafbat UI: A web-based interface for managing Kafka (accessible at http://localhost:8080).
 - create-topics: A service that attempts to create necessary Kafka topics upon startup.
 - spring-app: Your Spring Boot application.

---

## 🧪 3. Create Kafka Topics

Although the `create-topics` service is configured to attempt topic creation, it's good practice to verify or manually create them if needed, especially during initial setup or if `KAFKA_AUTO_CREATE_TOPICS_ENABLE` is set to `false` (as it is in your `docker-compose.yml`).

1.  Open your browser and go to the Kafbat UI: [http://localhost:8080](http://localhost:8080)
2.  Navigate to the topic creation page. You can usually find this under **Topics** or by directly accessing: [http://localhost:8080/ui/clusters/local/all-topics/create-new-topic](http://localhost:8080/ui/clusters/local/all-topics/create-new-topic)
3.  Create the following topics:
  * `visits`
  * `city.tours`

    Ensure these topic names exactly match what your Spring application is configured to use.

---

## 🚀 4. Send a Message to the Spring Boot App

Your Spring Boot application exposes a REST API endpoint to produce messages. Once the `spring-app` container is up and running (it might take a minute or two to fully start), you can send a test message.

Use a tool like **cURL**, **Bruno**, or **Postman** to send a `POST` request to your application. For example, using cURL:

```bash
curl --request POST --url 'http://localhost:9090/visits?location=VieilleBourse'
```

This will:

-   Trigger your Spring Boot application to produce a CloudEvent (e.g., a sightseeing visit to "VieilleBourse").
-   Send this event to the configured Kafka topic (`city.tours`) as Avro.
-   Your consumer within the same Spring application will then consume, deserialize, and process the event.
-   The message will be logged to the console output of the `spring-app` Docker container.

---

### 📥 5. Verify the Event Was Consumed
To see the consumer output, you can view the logs of your `spring-app` Docker container:

```bash
docker logs spring-app
```

At the end of the logs, you should see output similar to:

```Plaintext
📥 Received visit to: VieilleBourse at 2025-07-06T02:41:29.912132600+02:00
🧾 CloudEvent type: com.vv.visit.created, id: <some-uuid>
```

(Note: The timestamp and CloudEvent ID will vary).

This confirms that the message was successfully:

-   Sent as a CloudEvent using Avro.
-   Consumed from Kafka by your `spring-app` consumer.
-   Decoded, and logged correctly 🎉

---

## 📂 Project Structure

```plaintext
.
├── docker-compose.yml       # Kafka broker, Schema Registry, UI, and Spring app
├── src/main/java            # Spring Boot application source code
├── src/main/avro            # Avro schema (.avsc files)
├── pom.xml                  # Maven dependencies & Avro plugin
└── README.md                # This file
```

## 📚 Tech Stack

-   Spring Boot
-   Apache Kafka
-   CloudEvents SDK (Java)
-   Apache Avro
-   Docker Compose
-   Kafbat UI (browser-based Kafka management)

---

## 🤝 Contributing

Feel free to fork, modify, and contribute!

---

## 🛠 Troubleshooting

-   **"Connection to node -1 (localhost/127.0.0.1:9092) could not be established."**
  * This typically means your Spring app within its Docker container is trying to connect to `localhost`, which refers to itself, not the Kafka service. Ensure your `application.properties` and any hardcoded Kafka/Schema Registry URLs in your consumer code use the Docker service names: `kafka:9092` for Kafka and `http://schema-registry:8081` for Schema Registry.
-   **Topics Not Found:**
  * Double-check that the topic names (`visits`, `city.tours`) in Kafbat UI exactly match what your Spring application expects.
  * Verify that `kafka:9092` is correctly configured as the `KAFKA_BROKER_ID` and `KAFKA_ADVERTISED_LISTENERS` within your `docker-compose.yml` for the `kafka` service.
-   **No Logs from Consumer:**
  * Ensure all Docker containers (`zookeeper`, `kafka`, `schema-registry`, `kafbat-ui`, `spring-app`) are running (`docker ps`).
  * Confirm you've sent a POST request to `http://localhost:9090/visits?location=...`.
  * Check `docker logs spring-app` for any errors or the consumer output.
-   **Schema Registry Issues:**
  * Verify `SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: 'kafka:9092'` in your `docker-compose.yml` for the `schema-registry` service is correct.
  * Ensure your Spring app's `schema.registry.url` points to `http://schema-registry:8081`.

---

Happy streaming! 🚀