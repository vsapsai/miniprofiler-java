package com.miniprofiler;

/**
 * Class to use when we don't have {@link MiniProfiler}.
 *
 * Helps to avoid NullPointerException.
 */
public class NullMiniProfiler extends MiniProfiler {
    public NullMiniProfiler() {
        super(null);
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
