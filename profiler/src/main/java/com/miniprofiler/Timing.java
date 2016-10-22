package com.miniprofiler;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.miniprofiler.serialization.DurationSerializer;
import com.miniprofiler.serialization.InstantOffsetSerializer;

public class Timing implements Closeable {
    private final UUID id;
    private final MiniProfiler profiler;
    private final Optional<Double> minSaveMillis;
    private final boolean includeChildrenWithMinSave;
    private final InstantOffset startOffset;
    private String name;
    private Duration duration;
    private Timing parentTiming;
    private List<Timing> children;
    private Map<String, List<CustomTiming>> customTimings = new ConcurrentHashMap<>();

    public Timing(MiniProfiler profiler, Timing parent, String name, Double minSaveMillis, boolean includeChildrenWithMinSave) {
        this.id = UUID.randomUUID();
        this.profiler = profiler;
        profiler.setHead(this);
        if (parent != null) {
            parent.addChild(this);
        }

        this.minSaveMillis = Optional.ofNullable(minSaveMillis);
        this.includeChildrenWithMinSave = includeChildrenWithMinSave;
        this.startOffset = profiler.getStartOffset();
        this.name = name;
    }

    public Timing(MiniProfiler profiler, Timing parent, String name, Double minSaveMillis) {
        this(profiler, parent, name, minSaveMillis, false);
    }

    public Timing(MiniProfiler profiler, Timing parent, String name) {
        this(profiler, parent, name, null);
    }

    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("StartMilliseconds")
    @JsonSerialize(using = InstantOffsetSerializer.class)
    public InstantOffset getStartOffset() {
        return startOffset;
    }

    @JsonProperty("DurationMilliseconds")
    @JsonSerialize(using = DurationSerializer.class)
    public Duration getDuration() {
        return duration;
    }

    @JsonIgnore
    private Duration getDurationWithoutChildren() {
        Duration result = getDuration();
        List<Timing> children = getChildren();
        if ((result != null) && (children != null)) {
            for (Timing childTiming : children) {
                Duration childDuration = childTiming.getDuration();
                if (childDuration != null) {
                    result = result.minus(childDuration);
                }
            }
        }
        return result;
    }

    @JsonProperty("Children")
    public List<Timing> getChildren() {
        return (children != null) ? Collections.unmodifiableList(children) : null;
    }

    @JsonProperty("CustomTimings")
    public Map<String, List<CustomTiming>> getCustomTimings() {
        return Collections.unmodifiableMap(customTimings);
    }

    public void addCustomTiming(String category, CustomTiming customTiming) {
        getCustomTimingList(category).add(customTiming);
    }

    public void removeCustomTiming(String category, CustomTiming customTiming) {
        getCustomTimingList(category).remove(customTiming);
    }

    private List<CustomTiming> getCustomTimingList(String category) {
        List<CustomTiming> initialList = new ArrayList<>();
        customTimings.putIfAbsent(category, initialList);
        return customTimings.get(category);
    }

    @Override
    public void close() {
        stop();
    }

    public void stop() {
        if (duration != null) {
            return;
        }
        duration = profiler.getOffsetDuration(getStartOffset());
        profiler.setHead(getParentTiming());

        if (minSaveMillis.isPresent() && (minSaveMillis.get() > 0) && (getParentTiming() != null)) {
            Duration compareDuration = includeChildrenWithMinSave ? getDuration() : getDurationWithoutChildren();
            if (compareDuration.toMillis() < minSaveMillis.get()) {
                getParentTiming().removeChild(this);
            }
        }
    }

    public Stream<Timing> timingHierarchyStream() {
        Stream<Timing> result = Stream.of(this);
        if (children != null) {
            result = children.stream()
                    .map(Timing::timingHierarchyStream)
                    .reduce(result, Stream::concat);
        }
        return result;
    }

    private Timing getParentTiming() {
        return parentTiming;
    }

    private void setParentTiming(Timing parentTiming) {
        this.parentTiming = parentTiming;
    }

    private void addChild(Timing timing) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(timing);
        timing.setParentTiming(this);
        // TODO(vsapsai): figure out why do we need parent timing id and set it. The same for profiler id.
    }

    private void removeChild(Timing timing) {
        if (children != null) {
            children.remove(timing);
        }
    }
}
