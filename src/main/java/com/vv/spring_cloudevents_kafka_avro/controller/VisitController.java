package com.vv.spring_cloudevents_kafka_avro.controller;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final KafkaTemplate<String, CloudEvent> kafkaTemplate;
    private final String topic;

    public VisitController(KafkaTemplate<String, CloudEvent> kafkaTemplate,
                           @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping
    public String sendVisit(@RequestParam String location) throws IOException {
        // 1. Build the Avro payload
        VisitEvent visit = VisitEvent.newBuilder()
                                     .setLocation(location)
                                     .setTimestamp(OffsetDateTime.now().toString())
                                     .build();

        // 2. Serialize Avro to byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        DatumWriter<VisitEvent> writer = new SpecificDatumWriter<>(VisitEvent.class);
        writer.write(visit, encoder);
        encoder.flush();
        out.close();
        byte[] avroBytes = out.toByteArray();

        // 3. Wrap in a CloudEvent
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                                                 .withId(UUID.randomUUID().toString())
                                                 .withType("VisitEvent")
                                                 .withSource(URI.create("http://localhost/visits"))
                                                 .withDataContentType("application/avro")
                                                 .withTime(OffsetDateTime.now())
                                                 .withSubject("visit")
                                                 .withData(avroBytes)
                                                 .build();

        // 4. Send to Kafka
        kafkaTemplate.send(topic, cloudEvent);

        return "✅ Visit sent to Kafka: " + location;
    }
}
