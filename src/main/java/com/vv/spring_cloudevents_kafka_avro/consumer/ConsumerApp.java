package com.vv.spring_cloudevents_kafka_avro.consumer;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class ConsumerApp {

    @KafkaListener( topics = "city.tours",
                    groupId = "group1" )
    public void receive( ConsumerRecord<String, VisitEvent> record ) {
        VisitEvent visit = record.value();

        // Optionally reconstruct CloudEvent from headers
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                                                 .withId( getHeader( record, "ce_id" ) )
                                                 .withType( getHeader( record, "ce_type" ) )
                                                 .withSource( URI.create( getHeader( record, "ce_source" ) ) )
                                                 .withSubject( getHeader( record, "ce_subject" ) )
                                                 .withTime( OffsetDateTime.parse( getHeader( record, "ce_time" ) ) )
                                                 .withDataContentType( getHeader( record, "ce_datacontenttype" ) )
                                                 .withData( "application/avro", visit.toString()
                                                                                     .getBytes() ) // optional
                                                 .build();

        System.out.println( "📥 Received visit to: " + visit.getLocation() + " at " + visit.getTimestamp() );
        System.out.println( "🧾 CloudEvent type: " + cloudEvent.getType() + ", id: " + cloudEvent.getId() );
    }

    private String getHeader( ConsumerRecord<?, ?> record, String key ) {
        return new String( record.headers()
                                 .lastHeader( key )
                                 .value() );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VisitEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VisitEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory( consumerFactory() );
        return factory;
    }

    public ConsumerFactory<String, VisitEvent> consumerFactory() {
        Map<String, Object> props = Map.of(
                "bootstrap.servers", "kafka:9092", // Corrected
                "group.id", "group1",
                "key.deserializer", StringDeserializer.class.getName(),
                "value.deserializer", io.confluent.kafka.serializers.KafkaAvroDeserializer.class.getName(),
                "schema.registry.url", "http://schema-registry:8081", // Corrected
                "specific.avro.reader", true
                                          );

        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>( props );
    }
}
