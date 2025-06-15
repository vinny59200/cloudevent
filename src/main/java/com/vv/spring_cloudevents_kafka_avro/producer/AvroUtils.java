package com.vv.spring_cloudevents_kafka_avro.producer;

import com.vv.spring_cloudevents_kafka_avro.VisitEvent;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

public class AvroUtils {
    public static byte[] toBytes( VisitEvent event ) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SpecificDatumWriter<VisitEvent> writer = new SpecificDatumWriter<>( VisitEvent.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(event, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Avro event", e);
        }
    }
}
