package com.miniprofiler;

import java.time.Instant;

public class InstantOffset {
    private final Instant start;
    private final Instant offset;

    public InstantOffset(Instant start, Instant offset) {
        if (offset.isBefore(start)) {
            throw new IllegalArgumentException("Offset must be after start");
        }
        this.start = start;
        this.offset = offset;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getOffset() {
        return offset;
    }
}
