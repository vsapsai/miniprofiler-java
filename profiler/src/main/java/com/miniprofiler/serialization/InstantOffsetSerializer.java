package com.miniprofiler.serialization;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.miniprofiler.InstantOffset;

/**
 * Serialize {@link InstantOffset} as milliseconds between start and offset.
 */
public class InstantOffsetSerializer extends StdSerializer<InstantOffset> {

    public InstantOffsetSerializer () {
        this(null);
    }

    public InstantOffsetSerializer (Class<InstantOffset> t) {
        super(t);
    }

    public void serialize(InstantOffset value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        Duration offsetDuration = Duration.between(value.getStart(), value.getOffset());
        double durationMillis = offsetDuration.getSeconds() * 1_000 + ((double)offsetDuration.getNano()) / 1_000_000;
        jgen.writeNumber(durationMillis);
    }
}