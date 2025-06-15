# 🌍 Spring Boot Kafka Avro + CloudEvents Example

This project demonstrates how to use **Spring Boot**, **Kafka**, **Avro**, and **CloudEvents SDK** to produce and consume events.

---

## 🐳 1. Start Kafka and UI

Make sure Docker is running, then start the Kafka stack:

```bash
docker compose up -d
```

This starts:

- Kafka broker
- Kafka UI at [http://localhost:8080](http://localhost:8080)

---

## 🧪 2. Create a Kafka Topic

1. Go to [http://localhost:8080](http://localhost:8080)
2. Click on **Topics** → **Add topic**
3. Create a topic named (for example): `visits`

You can also use your own topic name — just ensure it's the one your app is configured to use.

---

## 🚀 3. Run the Spring Boot App

Run the app using Maven:
```bash
./mvnw spring-boot:run
```

Or using the compiled JAR:
```bash
java -jar target/your-app-name.jar
```

Launch a production message through the REST API:
```bash
curl --request POST --url 'http://localhost:9090/visits?location=LilleZoo'
```


This will:

- Produce a CloudEvent (e.g., a sightseeing visit to Lille Zoo)
- Send it to the Kafka topic as Avro
- Consume and deserialize it
- Log the message to the console

---

## 📥 4. Verify the Event Was Consumed

At the end of the log, you should see output similar to:

```text
📥 Received visit to: Lille Zoo at 2025-06-15T02:41:29.912132600+02:00
```
This confirms that the message was successfully:

- Sent as a CloudEvent using Avro
- Consumed from Kafka
- Decoded and logged correctly 🎉

---

## 📂 Project Structure

```text
.
├── docker-compose.yml       # Kafka broker & UI
├── src/main/java            # Spring Boot application
├── src/main/avro            # Avro schema (.avsc)
├── pom.xml                  # Maven dependencies & Avro plugin
└── README.md                # This file
```

## 📚 Tech Stack

- Spring Boot
- Kafka
- CloudEvents SDK (Java)
- Avro
- Docker Compose
- Kafka UI (browser-based)

---

## 🤝 Contributing

Feel free to fork, modify, and contribute!

---

## 🛠 Troubleshooting

- Make sure the topic name in your app matches the one you created.
- If you don’t see logs, check:
    - Docker is running
    - Topic exists
    - Ports aren't blocked

---

Happy streaming! 🚀


