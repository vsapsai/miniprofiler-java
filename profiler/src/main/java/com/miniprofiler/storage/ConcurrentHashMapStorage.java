package com.miniprofiler.storage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.miniprofiler.MiniProfiler;

public class ConcurrentHashMapStorage implements Storage {
    private final Map<UUID, MiniProfiler> map = new ConcurrentHashMap<>();

    @Override
    public void save(MiniProfiler profiler) {
        map.put(profiler.getId(), profiler);
    }

    @Override
    public MiniProfiler load(UUID id) {
        return map.get(id);
    }
}
