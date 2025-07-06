package com.vv.spring_cloudevents_kafka_avro.controller;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping( "/visits" )
public class VisitController {

    private final KafkaTemplate<String, VisitEvent> kafkaTemplate;
    private final String                            topic;

    public VisitController( KafkaTemplate<String, VisitEvent> kafkaTemplate,
                            @Value( "${app.kafka.topic}" ) String topic ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @PostMapping
    public String sendVisit( @RequestParam String location ) {
        // 1. Build the Avro payload (VisitEvent must be a generated Avro class)
        VisitEvent visit = VisitEvent.newBuilder()
                                     .setLocation( location )
                                     .setTimestamp( OffsetDateTime.now()
                                                                  .toString() )
                                     .build();

        // 2. Create CloudEvent metadata as headers
        String id = UUID.randomUUID()
                        .toString();
        OffsetDateTime now = OffsetDateTime.now();

        ProducerRecord<String, VisitEvent> record = new ProducerRecord<>( topic, visit );
        record.headers()
              .add( "ce_id", id.getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_type", "VisitEvent".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_source", "http://localhost/visits".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_specversion", "1.0".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_time", now.toString()
                                  .getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_subject", "visit".getBytes( StandardCharsets.UTF_8 ) );
        record.headers()
              .add( "ce_datacontenttype", "application/avro".getBytes( StandardCharsets.UTF_8 ) );

        // 3. Send the record
        kafkaTemplate.send( record );

        return "✅ Visit sent to Kafka: " + location;
    }

}
