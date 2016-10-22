package com.miniprofiler.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class InstantSerializerTest {
    @Test
    public void testSerialize() throws IOException {
        Instant instant = Instant.ofEpochMilli(1474518998123L);
        InstantSerializer serializer = new InstantSerializer();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
        serializer.serialize(instant, jsonGenerator, null);
        jsonGenerator.flush();
        assertEquals("\"/Date(1474518998123)/\"", stringWriter.toString());
    }
}
