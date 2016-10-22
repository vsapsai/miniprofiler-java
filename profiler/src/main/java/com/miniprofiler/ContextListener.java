package com.miniprofiler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * MiniProfiler integration point.
 */
public class ContextListener implements ServletContextListener, ServletRequestListener {
    private static final ThreadLocal<ServletRequest> threadRequest = new ThreadLocal<>();

    public static ServletRequest getCurrentRequest() {
        return threadRequest.get();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Settings settings = MiniProfiler.getSettings();
        ServletContext servletContext = event.getServletContext();
        settings.setContextPath(servletContext.getContextPath());
        configureSettings(settings);
        servletContext
            .addServlet("RequestHandlerServlet", RequestHandlerServlet.class)
            .addMapping("/" + settings.getRouteBasePath() + "*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do.
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        threadRequest.set(event.getServletRequest());
        MiniProfiler.start();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        MiniProfiler.stop();
        threadRequest.remove();
    }

    /**
     * Supposed to be overridden in subclasses to configure MiniProfiler.
     */
    public void configureSettings(Settings settings) {
        // Settings can have:
        // * shouldProfileServletRequests
        // * shouldProfileFilters
        // * shouldProfileViewRendering
    }
}
