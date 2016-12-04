package com.miniprofiler.storage;

import java.util.List;
import java.util.Set;
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
    private final static long DEFAULT_CACHE_SIZE = 10_000;
    private final static long DEFAULT_CACHE_EXPIRATION_IN_MINUTES = 10;

    private final Cache<UUID, MiniProfiler> cache;
    private final ProfilesViewingStorage unviewedProfilesStorage;

    public GuavaCacheStorage(Cache<UUID, MiniProfiler> cache, Cache<String, Set<UUID>> unviewedProfilesCache) {
        this.cache = cache;
        this.unviewedProfilesStorage = new ProfilesViewingStorage(unviewedProfilesCache.asMap());
    }

    public GuavaCacheStorage() {
        this(CacheBuilder.newBuilder()
                .maximumSize(DEFAULT_CACHE_SIZE)
                .expireAfterAccess(DEFAULT_CACHE_EXPIRATION_IN_MINUTES, TimeUnit.MINUTES)
                .build(),
            CacheBuilder.newBuilder()
                .maximumSize(DEFAULT_CACHE_SIZE)
                .expireAfterAccess(DEFAULT_CACHE_EXPIRATION_IN_MINUTES, TimeUnit.MINUTES)
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

    @Override
    public void setUnviewed(String user, UUID id) {
        unviewedProfilesStorage.setUnviewed(user, id);
    }

    @Override
    public void setViewed(String user, final UUID id) {
        unviewedProfilesStorage.setViewed(user, id);
    }

    @Override
    public List<UUID> getUnviewedIds(String user) {
        return unviewedProfilesStorage.getUnviewedIds(user);
    }
}
