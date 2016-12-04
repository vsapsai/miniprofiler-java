package com.miniprofiler.storage;

import java.util.List;
import java.util.UUID;

import com.miniprofiler.MiniProfiler;

public interface Storage {
    void save(MiniProfiler profiler);
    MiniProfiler load(UUID id);

    void setUnviewed(String user, UUID id);
    void setViewed(String user, UUID id);
    List<UUID> getUnviewedIds(String user);
}
