package com.miniprofiler.serialization;

import java.io.IOException;
import java.time.Instant;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Serialize java.time.Instant like .NET DateTime.
 */
public class InstantSerializer extends StdSerializer<Instant> {

    public InstantSerializer() {
        this(null);
    }

    public InstantSerializer(Class<Instant> t) {
        super(t);
    }

    public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        jgen.writeString(String.format("/Date(%d)/", value.toEpochMilli()));
    }
}
