package com.miniprofiler.serialization;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serialize java.time.Duration as milliseconds.
 */
public class DurationSerializer extends StdSerializer<Duration> {

    public static double durationInMillis(Duration value) {
        return value.getSeconds() * 1_000 + ((double)value.getNano()) / 1_000_000;
    }

    public DurationSerializer () {
        this(null);
    }

    public DurationSerializer (Class<Duration> t) {
        super(t);
    }

    public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeNumber(durationInMillis(value));
    }
}
