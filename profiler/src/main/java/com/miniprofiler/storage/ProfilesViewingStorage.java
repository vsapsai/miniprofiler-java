package com.miniprofiler.storage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * Helper class that allows to store which profiling results are viewed and which are not.
 *
 * The purpose of creating this class is that same implementation can be shared between
 * {@link ConcurrentMap} and {@link com.google.common.cache.Cache}.
 *
 * Not thread safe.
 */
/* package */ class ProfilesViewingStorage {
    private final ConcurrentMap<String, Set<UUID>> unviewedProfilesMap;

    private static Set<UUID> concatenateSets(Set<UUID> set1, Set<UUID> set2) {
        Set<UUID> result = new LinkedHashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    public ProfilesViewingStorage(ConcurrentMap<String, Set<UUID>> unviewedProfilesMap) {
        this.unviewedProfilesMap = unviewedProfilesMap;
    }

    public void setUnviewed(String user, UUID id) {
        Set<UUID> newUnviewedValue = new LinkedHashSet<>();
        newUnviewedValue.add(id);
        unviewedProfilesMap.merge(user, newUnviewedValue, ProfilesViewingStorage::concatenateSets);
    }

    public void setViewed(String user, final UUID id) {
        unviewedProfilesMap.computeIfPresent(user, (key, unviewedIds) -> {
            unviewedIds.remove(id);
            return unviewedIds;
        });
    }

    public List<UUID> getUnviewedIds(String user) {
        Set<UUID> unviewedIds = unviewedProfilesMap.get(user);
        return (unviewedIds != null) ? new ArrayList<>(unviewedIds) : new ArrayList<>();
    }
}
