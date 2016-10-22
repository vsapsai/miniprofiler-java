package com.miniprofiler.serialization;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class DurationSerializerTest {
    @Test
    public void testDurationInMillis() {
        Duration duration = Duration.ofSeconds(1_234, 100_000);
        assertEquals(1_234_000.1, DurationSerializer.durationInMillis(duration), 0.01);
    }

    @Test
    public void testSerialize() throws IOException {
        Duration duration = Duration.ofSeconds(1_234L, 100_000L);
        DurationSerializer serializer = new DurationSerializer();
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
        serializer.serialize(duration, jsonGenerator, null);
        jsonGenerator.flush();
        assertEquals("1234000.1", stringWriter.toString());
    }
}