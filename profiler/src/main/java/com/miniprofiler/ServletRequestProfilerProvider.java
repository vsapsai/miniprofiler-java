package com.miniprofiler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.miniprofiler.storage.Storage;

public class ServletRequestProfilerProvider implements ProfilerProvider {
    private static final String ATTRIBUTE_NAME = ":mini-profiler:";

    private static UserProvider userProvider = new IpAddressProvider();

    public static MiniProfiler getProfilerFromRequest(ServletRequest request) {
        return (MiniProfiler)request.getAttribute(ATTRIBUTE_NAME);
    }

    static void saveProfiler(MiniProfiler profiler) {
        MiniProfiler.getSettings().ensureStorageStrategy();
        Storage storage = MiniProfiler.getSettings().getStorage();
        storage.save(profiler);
        if (!profiler.hasUserViewed()) {
            storage.setUnviewed(profiler.getUser(), profiler.getId());
        }
    }

    @Override
    public MiniProfiler start(String sessionName) {
        ServletRequest currentRequest = ContextListener.getCurrentRequest();
        if (currentRequest == null) {
            return MiniProfiler.NULL;
        }
        if (currentRequest instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) currentRequest;
            String path = httpRequest.getServletPath();
            if (isPathMatchingProfilerRouteBasePath(path)) {
                return MiniProfiler.NULL;
            }
        }
        MiniProfiler profiler = new MiniProfiler(getFullRequestUrl(currentRequest));
        profiler.setUser(userProvider.getUser(currentRequest));
        currentRequest.setAttribute(ATTRIBUTE_NAME, profiler);
        return profiler;
    }

    @Override
    public void stop(boolean discardResults) {
        MiniProfiler currentProfiler = getCurrentProfiler();
        if (currentProfiler.isNull()) {
            return;
        }
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

        saveProfiler(currentProfiler);
    }

    @Override
    public MiniProfiler getCurrentProfiler() {
        MiniProfiler currentProfiler = null;
        ServletRequest currentRequest = ContextListener.getCurrentRequest();
        if (currentRequest != null) {
            currentProfiler = getProfilerFromRequest(currentRequest);
        }
        return (currentProfiler != null) ? currentProfiler : MiniProfiler.NULL;
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

    private boolean isPathMatchingProfilerRouteBasePath(String path) {
        String routeBasePath = MiniProfiler.getSettings().getRouteBasePath();
        String slashLessRouteBasePath = routeBasePath.substring(0, routeBasePath.length() - 1);
        String slashLessPath = path.substring(1);
        return slashLessPath.startsWith(slashLessRouteBasePath);
    }

    public static UserProvider getUserProvider() {
        return userProvider;
    }

    public static void setUserProvider(UserProvider userProvider) {
        ServletRequestProfilerProvider.userProvider = userProvider;
    }
}
