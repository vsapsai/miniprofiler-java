package com.miniprofiler.storage;

import java.util.UUID;

import com.miniprofiler.MiniProfiler;

public interface Storage {
    void save(MiniProfiler profiler);
    MiniProfiler load(UUID id);
}
