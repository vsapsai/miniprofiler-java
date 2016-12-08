package com.miniprofiler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.miniprofiler.storage.Storage;

public class RequestHandlerFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        MiniProfiler currentProfiler = ServletRequestProfilerProvider.getProfilerFromRequest(request);

        chain.doFilter(request, response);

        if ((currentProfiler == null) || currentProfiler.isNull()) {
            return;
        }
        ServletRequestProfilerProvider.saveProfiler(currentProfiler);

        // Handle unviewed profiles.
        Storage storage = MiniProfiler.getSettings().getStorage();
        List<UUID> unviewedIds = storage.getUnviewedIds(currentProfiler.getUser());
        int maxUnviewedProfiles = MiniProfiler.getSettings().getMaxUnviewedProfiles();
        if ((unviewedIds != null) && (unviewedIds.size() > maxUnviewedProfiles)) {
            unviewedIds.subList(0, unviewedIds.size() - maxUnviewedProfiles).stream()
                    .forEach(id -> storage.setViewed(currentProfiler.getUser(), id));
        }
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Allow profiling of ajax requests.
            if ((unviewedIds != null) && !unviewedIds.isEmpty()) {
                String unviewedIdsString = unviewedIds.stream()
                        .map(UUID::toString)
                        .collect(Collectors.joining("\",\"", "[\"", "\"]"));
                httpResponse.setHeader("X-MiniProfiler-Ids", unviewedIdsString);
            }
        }
    }

    @Override
    public void destroy() {
        // Do nothing.
    }
}
