package com.miniprofiler.storage;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.miniprofiler.MiniProfiler;

public class ConcurrentHashMapStorage implements Storage {
    private final Map<UUID, MiniProfiler> map = new ConcurrentHashMap<>();
    private final ProfilesViewingStorage unviewedProfilesStorage =
        new ProfilesViewingStorage(new ConcurrentHashMap<>());

    @Override
    public void save(MiniProfiler profiler) {
        map.put(profiler.getId(), profiler);
    }

    @Override
    public MiniProfiler load(UUID id) {
        return map.get(id);
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
