package com.miniprofiler;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.miniprofiler.serialization.DurationSerializer;
import com.miniprofiler.serialization.InstantOffsetSerializer;

/**
 *  A custom timing that usually represents a Remote Procedure Call, allowing
 *  better visibility into these longer-running calls.
 */
public class CustomTiming implements Closeable {
    private final UUID id;
    private final MiniProfiler profiler;
    private final String commandString;
    private final Optional<Double> minSaveMillis;
    private final InstantOffset startOffset;
    private String executeType;
    private Duration duration;
    private String category;

    // TODO(vsapsai): add also stack trace snippet.
    // TODO(vsapsai): implement measuring time to the first byte.

    public CustomTiming(MiniProfiler profiler, String commandString, Double minSaveMillis) {
        this.id = UUID.randomUUID();
        this.profiler = profiler;
        this.commandString = commandString;
        this.minSaveMillis = Optional.ofNullable(minSaveMillis);
        this.startOffset = profiler.getStartOffset();
    }

    public CustomTiming(MiniProfiler profiler, String commandString) {
        this(profiler, commandString, null);
    }

    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    @JsonProperty("CommandString")
    public String getCommandString() {
        return commandString;
    }

    @JsonProperty("ExecuteType")
    public String getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = executeType;
    }

    /* package */ String getCategory() {
        return category;
    }

    /* package */ void setCategory(String category) {
        this.category = category;
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

    public void stop() {
        if (duration != null) {
            return;
        }
        duration = profiler.getOffsetDuration(getStartOffset());

        double minMillis = minSaveMillis.orElse(0.0);
        if ((minMillis > 0) && (duration.toMillis() < minMillis)) {
            profiler.getHead().removeCustomTiming(category, this);
        }
    }

    @Override
    public void close() {
        stop();
    }
}
