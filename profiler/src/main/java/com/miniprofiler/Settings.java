package com.miniprofiler;

import java.time.Clock;

import com.miniprofiler.storage.ConcurrentHashMapStorage;
import com.miniprofiler.storage.Storage;

public class Settings {
    private final String version;
    private Storage storage;
    private ProfilerProvider profilerProvider;
    private Clock clock = Clock.systemUTC();
    private String contextPath = "";
    private String routeBasePath = "mini-profiler-resources/";
    private int maxUnviewedProfiles = 20;
    private double trivialDurationThresholdMilliseconds = 2.0;
    private RenderPosition popupRenderPosition = RenderPosition.LEFT;
    private Boolean popupShowTrivial = false;
    private Boolean popupShowTimeWithChildren = false;
    private Integer popupMaxTracesToShow = 15;
    private String popupToggleKeyboardShortcut = "Alt+P";
    private Boolean popupStartHidden = false;
    private Boolean showControls = false;

    public Settings() {
        // TODO(vsapsai): set some useful version
        version = "0.1";
    }

    public String getVersion() {
        return version;
    }

    public void ensureStorageStrategy() {
        if (storage == null) {
            storage = new ConcurrentHashMapStorage();
        }
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void ensureProfilerProvider() {
        if (profilerProvider == null) {
            profilerProvider = new ServletRequestProfilerProvider();
        }
    }

    public ProfilerProvider getProfilerProvider() {
        return profilerProvider;
    }

    public void setProfilerProvider(ProfilerProvider profilerProvider) {
        this.profilerProvider = profilerProvider;
    }

    public Clock getClock() {
        return clock;
    }

    // For unit testing.
    /* package */ void setClock(Clock clock) {
        this.clock = clock;
    }

    /* package */ String getContextPath() {
        return contextPath;
    }

    /* package */ void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getRouteBasePath() {
        return routeBasePath;
    }

    public void setRouteBasePath(String routeBasePath) {
        if (routeBasePath.startsWith("/") || !routeBasePath.endsWith("/")) {
            throw new IllegalArgumentException("routeBasePath should end with slash and shouldn't start with slash");
        }
        this.routeBasePath = routeBasePath;
    }

    public String getAbsoluteRouteBasePath() {
        return getContextPath() + "/" + getRouteBasePath();
    }

    public int getMaxUnviewedProfiles() {
        return maxUnviewedProfiles;
    }

    public void setMaxUnviewedProfiles(int maxUnviewedProfiles) {
        this.maxUnviewedProfiles = maxUnviewedProfiles;
    }

    public double getTrivialDurationThresholdMilliseconds() {
        return trivialDurationThresholdMilliseconds;
    }

    public void setTrivialDurationThresholdMilliseconds(double trivialDurationThresholdMilliseconds) {
        this.trivialDurationThresholdMilliseconds = trivialDurationThresholdMilliseconds;
    }

    public RenderPosition getPopupRenderPosition() {
        return popupRenderPosition;
    }

    public void setPopupRenderPosition(RenderPosition popupRenderPosition) {
        this.popupRenderPosition = popupRenderPosition;
    }

    public Boolean getPopupShowTrivial() {
        return popupShowTrivial;
    }

    public void setPopupShowTrivial(Boolean popupShowTrivial) {
        this.popupShowTrivial = popupShowTrivial;
    }

    public Boolean getPopupShowTimeWithChildren() {
        return popupShowTimeWithChildren;
    }

    public void setPopupShowTimeWithChildren(Boolean popupShowTimeWithChildren) {
        this.popupShowTimeWithChildren = popupShowTimeWithChildren;
    }

    public Integer getPopupMaxTracesToShow() {
        return popupMaxTracesToShow;
    }

    public void setPopupMaxTracesToShow(Integer popupMaxTracesToShow) {
        this.popupMaxTracesToShow = popupMaxTracesToShow;
    }

    public String getPopupToggleKeyboardShortcut() {
        return popupToggleKeyboardShortcut;
    }

    public void setPopupToggleKeyboardShortcut(String popupToggleKeyboardShortcut) {
        this.popupToggleKeyboardShortcut = popupToggleKeyboardShortcut;
    }

    public Boolean getPopupStartHidden() {
        return popupStartHidden;
    }

    public void setPopupStartHidden(Boolean popupStartHidden) {
        this.popupStartHidden = popupStartHidden;
    }

    public Boolean getShowControls() {
        return showControls;
    }

    public void setShowControls(Boolean showControls) {
        this.showControls = showControls;
    }
}
