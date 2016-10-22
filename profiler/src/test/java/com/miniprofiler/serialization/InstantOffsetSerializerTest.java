package com.miniprofiler.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import com.miniprofiler.InstantOffset;

public class InstantOffsetSerializerTest {
    private Instant now = Instant.now();

    @Test
    public void testMillisPrecision() throws IOException {
        InstantOffset offset = new InstantOffset(now, now.plusMillis(42));
        assertEquals("42.0", serializeValue(offset));
    }

    @Test
    public void testNanoPrecision() throws IOException {
        InstantOffset offset = new InstantOffset(now, now.plusNanos(100_000));
        assertEquals("0.1", serializeValue(offset));
    }

    @Test
    public void testNoOffset() throws IOException {
        InstantOffset offset = new InstantOffset(now, now);
        assertEquals("0.0", serializeValue(offset));
    }

    private String serializeValue(InstantOffset value) throws IOException {
        StringWriter stringWriter = new StringWriter();
        InstantOffsetSerializer serializer = new InstantOffsetSerializer();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
        serializer.serialize(value, jsonGenerator, null);
        jsonGenerator.flush();
        return stringWriter.toString();
    }
}