package com.vv.spring_cloudevents_kafka_avro.producer;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.kafka.CloudEventSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Properties;
import java.util.UUID;

@Component
public class ProducerApp implements CommandLineRunner {

    @Override
    public void run( String... args ) {
        VisitEvent payload = new VisitEvent();
        payload.setLocation( "Lille Zoo" );
        payload.setTimestamp( OffsetDateTime.now()
                                            .toString() );

        CloudEvent event = CloudEventBuilder.v1()
                                            .withId( UUID.randomUUID()
                                                         .toString() )
                                            .withSource( URI.create( "https://lille.tour" ) )
                                            .withType( "VisitEvent" )
                                            .withTime( OffsetDateTime.now() )
                                            .withData( "application/avro", AvroUtils.toBytes( payload ) ) // Avro payload
                                            .build();

        Properties props = new Properties();
        props.put( "bootstrap.servers", "localhost:9092" );
        props.put( "key.serializer", StringSerializer.class.getName() );
        props.put( "value.serializer", CloudEventSerializer.class.getName() );

        try ( KafkaProducer<String, CloudEvent> producer = new KafkaProducer<>( props ) ) {
            ProducerRecord<String, CloudEvent> record = new ProducerRecord<>( "city.tours", event );
            producer.send( record );
            System.out.println( "Event sent!" );
        }
    }
}
