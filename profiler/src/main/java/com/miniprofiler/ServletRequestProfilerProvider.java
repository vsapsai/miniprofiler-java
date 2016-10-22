package com.miniprofiler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ServletRequestProfilerProvider implements ProfilerProvider {
    private static final String ATTRIBUTE_NAME = ":mini-profiler:";

    @Override
    public MiniProfiler start(String sessionName) {
        ServletRequest currentRequest = ContextListener.getCurrentRequest();
        if (currentRequest == null) {
            return null;
        }
        MiniProfiler profiler = new MiniProfiler(getFullRequestUrl(currentRequest));
        currentRequest.setAttribute(ATTRIBUTE_NAME, profiler);
        return profiler;
    }

    @Override
    public void stop(boolean discardResults) {
        MiniProfiler currentProfiler = getCurrentProfiler();
        // Stop profiler.
        if (!currentProfiler.stopImpl()) {
            return;
        }
        if (discardResults) {
            return;
        }

        if (currentProfiler.getName() == null) {
            String name = getFullRequestUrl(ContextListener.getCurrentRequest());
            if (name.length() > 50) {
                name = name.substring(0, 50);
            }
            currentProfiler.setName(name);
        }

        // Save profiler.
        MiniProfiler.getSettings().ensureStorageStrategy();
        MiniProfiler.getSettings().getStorage().save(currentProfiler);
    }

    @Override
    public MiniProfiler getCurrentProfiler() {
        ServletRequest currentRequest = ContextListener.getCurrentRequest();
        if (currentRequest == null) {
            return null;
        }
        return (MiniProfiler)currentRequest.getAttribute(ATTRIBUTE_NAME);
    }

    private String getFullRequestUrl(ServletRequest request) {
        String result = "root";
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            StringBuilder sb = new StringBuilder();
            sb.append(httpRequest.getScheme()).append("://").append(httpRequest.getServerName());
            int serverPort = httpRequest.getServerPort();
            if ((serverPort != 80) && (serverPort != 443)) {
                sb.append(":").append(serverPort);
            }
            sb.append(httpRequest.getRequestURI());
            result = sb.toString();
        }
        return result;
    }
}
