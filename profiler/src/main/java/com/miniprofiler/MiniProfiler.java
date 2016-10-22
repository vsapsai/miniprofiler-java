package com.miniprofiler;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.miniprofiler.serialization.DurationSerializer;
import com.miniprofiler.serialization.InstantSerializer;

// TODO(vsapsai): implement
public class MiniProfiler {

    private final static Settings settings = new Settings();

    private final UUID id;
    private String name;
    private Instant started;
    private Duration duration;
    private String machineName;
    private Map<String, String> customLinks;
    private Timing root;
    private Timing head;
    private ClientTimings clientTimings;
    private String user;
    private boolean hasUserViewed;

    public static Settings getSettings() {
        return settings;
    }

    public static MiniProfiler getCurrent() {
        settings.ensureProfilerProvider();
        return settings.getProfilerProvider().getCurrentProfiler();
    }

    public static MiniProfiler start() {
        return start(null);
    }

    public static MiniProfiler start(String sessionName) {
        settings.ensureProfilerProvider();
        return settings.getProfilerProvider().start(sessionName);
    }

    public static void stop() {
        stop(false);
    }

    public static void stop(boolean discardResults) {
        settings.ensureProfilerProvider();
        settings.getProfilerProvider().stop(discardResults);
    }

    /* package */ boolean stopImpl() {
        assert duration == null : "Trying to stop already stopped profiler";
        duration = Duration.between(started, now());
        root.timingHierarchyStream().forEach(Timing::stop);
        return true;
    }

    public MiniProfiler(String url) {
        id = UUID.randomUUID();
        started = now();
        root = new Timing(this, null, url);
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

    @JsonProperty("Started")
    @JsonSerialize(using = InstantSerializer.class)
    public Instant getStarted() {
        return started;
    }

    @JsonProperty("DurationMilliseconds")
    @JsonSerialize(using = DurationSerializer.class)
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("MachineName")
    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @JsonProperty("CustomLinks")
    public Map<String, String> getCustomLinks() {
        return customLinks;
    }

    public void setCustomLinks(Map<String, String> customLinks) {
        this.customLinks = customLinks;
    }

    @JsonProperty("Root")
    public Timing getRoot() {
        return root;
    }

    @JsonProperty("ClientTimings")
    public ClientTimings getClientTimings() {
        return clientTimings;
    }

    public void setClientTimings(ClientTimings clientTimings) {
        this.clientTimings = clientTimings;
    }

    @JsonProperty("User")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean hasUserViewed() {
        return hasUserViewed;
    }

    public void setUserViewed(boolean hasUserViewed) {
        this.hasUserViewed = hasUserViewed;
    }

    public Closeable step(String name) {
        return new Timing(this, getHead(), name);
    }

    public Closeable stepIf(String name, double minSaveMillis, boolean includeChildren) {
        return new Timing(this, getHead(), name, minSaveMillis, includeChildren);
    }

    public Closeable stepIf(String name, double minSaveMillis) {
        return stepIf(name, minSaveMillis, false);
    }

    public CustomTiming customTiming(String category, String commandString, String executeType) {
        return customTimingIf(category, commandString, 0, executeType);
    }

    public CustomTiming customTiming(String category, String commandString) {
        return customTiming(category, commandString, null);
    }

    public CustomTiming customTimingIf(String category, String commandString, double minSaveMillis, String executeType) {
        Timing head = getHead();
        if (head == null) {
            return null;
        }

        CustomTiming customTiming = new CustomTiming(this, commandString, minSaveMillis);
        customTiming.setExecuteType(executeType);
        customTiming.setCategory(category);
        head.addCustomTiming(category, customTiming);
        return customTiming;
    }

    public CustomTiming customTimingIf(String category, String commandString, double minSaveMillis) {
        return customTimingIf(category, commandString, minSaveMillis, null);
    }

    /* package */ Timing getHead() {
        return head;
    }

    /* package */ void setHead(Timing head) {
        this.head = head;
    }

    @JsonIgnore
    public InstantOffset getStartOffset() {
        return new InstantOffset(getStarted(), now());
    }

    public Duration getOffsetDuration(InstantOffset startOffset) {
        return Duration.between(startOffset.getOffset(), now());
    }

    private Instant now() {
        return Instant.now(settings.getClock());
    }
}
