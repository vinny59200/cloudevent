package com.vv.spring_cloudevents_kafka_avro.consumer;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayInputStream;

public class AvroUtils {
    public static VisitEvent fromBytes( byte[] bytes ) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            SpecificDatumReader<VisitEvent> reader = new SpecificDatumReader<>(VisitEvent.class);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(in, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Avro event", e);
        }
    }
}

