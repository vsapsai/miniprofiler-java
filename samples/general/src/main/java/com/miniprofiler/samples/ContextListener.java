package com.miniprofiler.samples;

import com.miniprofiler.Settings;
import com.miniprofiler.storage.GuavaCacheStorage;

public class ContextListener extends com.miniprofiler.ContextListener {
    @Override
    public void configureSettings(Settings settings) {
        settings.setRouteBasePath("profiler/");
        settings.setStorage(new GuavaCacheStorage());
    }
}
