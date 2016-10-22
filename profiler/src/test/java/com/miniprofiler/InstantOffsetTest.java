package com.miniprofiler;

import static org.junit.Assert.*;

import org.junit.Test;

import java.time.Instant;

public class InstantOffsetTest {
    private Instant now = Instant.now();
    private Instant later = now.plusMillis(1);

    @Test
    public void testInstantOffset() {
        InstantOffset offset = new InstantOffset(now, later);
        assertEquals(now, offset.getStart());
        assertEquals(later, offset.getOffset());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidOffset() {
        new InstantOffset(later, now);
    }

    @Test
    public void testEqualInstants() {
        new InstantOffset(now, now);
        // Expect no exception.
    }
}