package com.miniprofiler.samples;

import java.util.ArrayList;
import java.util.List;

import com.miniprofiler.Settings;
import com.miniprofiler.storage.GuavaCacheStorage;

public class ContextListener extends com.miniprofiler.ContextListener {
    @Override
    public void configureSettings(Settings settings) {
        List<String> ignoredPaths = new ArrayList<>();
        ignoredPaths.addAll(settings.getIgnoredPaths());
        ignoredPaths.add("/style.css");
        settings.setIgnoredPaths(ignoredPaths);
        settings.setRouteBasePath("profiler/");
        settings.setStorage(new GuavaCacheStorage());
    }
}
