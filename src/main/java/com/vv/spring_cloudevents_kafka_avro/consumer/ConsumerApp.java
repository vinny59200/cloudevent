package com.vv.spring_cloudevents_kafka_avro.consumer;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.kafka.CloudEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerApp {

    @KafkaListener(topics = "city.tours", groupId = "group1")
    public void receive(ConsumerRecord<String, CloudEvent> record) {
        CloudEvent event = record.value();
        byte[] data = event.getData().toBytes();

        VisitEvent visit = AvroUtils.fromBytes( data );
        System.out.println("📥 Received visit to: " + visit.getLocation() + " at " + visit.getTimestamp());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CloudEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CloudEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    public ConsumerFactory<String, CloudEvent> consumerFactory() {
        Map<String, Object> props = Map.of(
                "bootstrap.servers", "localhost:9092",
                "group.id", "group1",
                "key.deserializer", StringDeserializer.class.getName(),
                "value.deserializer", CloudEventDeserializer.class.getName()
                                          );
        return new org.springframework.kafka.core.DefaultKafkaConsumerFactory<>(props);
    }
}
