package com.miniprofiler;

/**
 * A provider used to create MiniProfiler instances and maintain the current instance.
 */
public interface ProfilerProvider {
    MiniProfiler start(String sessionName);
    void stop(boolean discardResults);
    MiniProfiler getCurrentProfiler();
}
