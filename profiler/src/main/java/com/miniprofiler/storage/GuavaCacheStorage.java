package com.miniprofiler.storage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.miniprofiler.MiniProfiler;

/**
 * {@link Storage} implementation that works with Guava cache.
 *
 * Library doesn't provide transitive Guava dependency.  Code using this
 * storage has to depend on Guava itself.
 */
public class GuavaCacheStorage implements Storage {
    private final Cache<UUID, MiniProfiler> cache;

    public GuavaCacheStorage(Cache<UUID, MiniProfiler> cache) {
        this.cache = cache;
    }

    public GuavaCacheStorage() {
        this(CacheBuilder.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build());
    }

    @Override
    public void save(MiniProfiler profiler) {
        cache.put(profiler.getId(), profiler);
    }

    @Override
    public MiniProfiler load(UUID id) {
        return cache.getIfPresent(id);
    }
}
