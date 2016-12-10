package com.miniprofiler;

import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprofiler.serialization.DurationSerializer;

public class RequestHandlerServlet extends HttpServlet {
    private final static char VARIABLE_PREFIX = '{';
    private final static char VARIABLE_SUFFIX = '}';
    private final ObjectMapper mapper = new ObjectMapper();

    public static String renderIncludes(MiniProfiler currentProfiler, RenderOptions renderOptions) {
        if (currentProfiler.isNull()) {
            return "";
        }
        String source;
        try {
            source = getUiFileContent("/include.partial.html");
        } catch (IOException e) {
            return "<!-- Could not find 'include.partial.html' -->";
        }
        Settings settings = currentProfiler.getSettings();
        final Map<String, String> valueMap = new HashMap<>();
        valueMap.put("path", settings.getAbsoluteRouteBasePath());
        valueMap.put("version", settings.getVersion());
        valueMap.put("currentId", currentProfiler.getId().toString());
        valueMap.put("ids", currentProfiler.getId().toString());
        valueMap.put("position",
            notNull(renderOptions.getPosition(), settings.getPopupRenderPosition()).getJsRepresentation());
        valueMap.put("showTrivial",
            notNull(renderOptions.getShowTrivial(), settings.getPopupShowTrivial()).toString());
        valueMap.put("showChildren",
            notNull(renderOptions.getShowTimeWithChildren(), settings.getPopupShowTimeWithChildren()).toString());
        valueMap.put("maxTracesToShow",
            notNull(renderOptions.getMaxTracesToShow(), settings.getPopupMaxTracesToShow()).toString());
        valueMap.put("showControls",
            notNull(renderOptions.getShowControls(), settings.getShowControls()).toString());
        valueMap.put("authorized", "true");
        valueMap.put("toggleShortcut", settings.getPopupToggleKeyboardShortcut());
        valueMap.put("startHidden",
            notNull(renderOptions.getStartHidden(), settings.getPopupStartHidden()).toString());
        valueMap.put("trivialMilliseconds", String.format("%.1f", settings.getTrivialDurationThresholdMilliseconds()));
        return substitutePlaceholders(source, valueMap);
    }

    private static <T> T notNull(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    private static String getUiFileContent(String pathInfo) throws IOException {
        try (InputStream inputStream = RequestHandlerServlet.class.getResourceAsStream("/ui" + pathInfo)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static String getUiFileContentType(String pathInfo) {
        if (pathInfo.endsWith(".js")) {
            return "application/javascript";
        }
        if (pathInfo.endsWith(".css")) {
            return "text/css";
        }
        if (pathInfo.endsWith(".tmpl")) {
            return "text/x-jquery-tmpl";
        }
        return "text/html";
    }

    private static String substitutePlaceholders(
            String source, final Map<String, String> substitutions) {
        StrSubstitutor substitutor = new StrSubstitutor();
        substitutor.setVariablePrefixMatcher(new AlphanumericVariablePrefixMatcher());
        substitutor.setVariableSuffix(VARIABLE_SUFFIX);
        substitutor.setVariableResolver(new StrLookup<String>() {
            public String lookup(String key) {
                String value = substitutions.get(key);
                if (value == null) {
                    throw new IllegalArgumentException("Missing value for key '" + key + "'.");
                }
                return value;
            }
        });
        return substitutor.replace(source);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handleRequest(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        handleRequest(request, response);
    }

    /**
     * Common method to handle GET and POST requests.
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String pathInfo = request.getPathInfo();
        String baseName = FilenameUtils.getBaseName(pathInfo);
        switch (baseName.toLowerCase()) {
            case "includes":
                respondWithUiFileContent(response, pathInfo);
                break;
            case "results":
                respondWithSingleProfilerResult(response, request);
                break;
            default:
                respondNotFound(response);
                break;
        }
    }

    private void respondNotFound(HttpServletResponse response, String contentType, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.setContentType(contentType);
        if (message != null) {
            response.getWriter().write(message);
        }
    }

    private void respondNotFound(HttpServletResponse response, String contentType)
            throws IOException {
        respondNotFound(response, contentType, null);
    }

    private void respondNotFound(HttpServletResponse response) throws IOException {
        respondNotFound(response, "text/plain");
    }

    private void respondWithUiFileContent(HttpServletResponse response, String pathInfo)
            throws IOException {
        response.setContentType(getUiFileContentType(pathInfo));
        response.getWriter().write(getUiFileContent(pathInfo));
    }

    private void respondWithSingleProfilerResult(HttpServletResponse response, HttpServletRequest request)
            throws IOException {
        /*
         Difference between .NET version:
           - not showing last profiling results when there is no id;
           - not setting viewed for profiling result;
           - not checking if request is authorized.
        */
        boolean isPopup = (request.getParameter("popup") != null);
        UUID id;
        try {
            id = UUID.fromString(request.getParameter("id"));
        } catch (IllegalArgumentException e) {
            if (isPopup) {
                respondNotFound(response);
            } else {
                respondNotFound(response, "text/plain", "No UUID id specified on the query string");
            }
            return;
        }

        MiniProfiler.getSettings().ensureStorageStrategy();
        MiniProfiler profiler = MiniProfiler.getSettings().getStorage().load(id);

        UserProvider userProvider = ServletRequestProfilerProvider.getUserProvider();
        String user = null;
        if (userProvider != null) {
            user = userProvider.getUser(request);
        }
        MiniProfiler.getSettings().getStorage().setViewed(user, id);

        if (profiler == null) {
            if (isPopup) {
                respondNotFound(response);
            } else {
                respondNotFound(response, "text/plain", "No MiniProfiler results found with Id=" + id.toString());
            }
            return;
        }

        boolean needsSave = false;
        if (profiler.getClientTimings() == null) {
            profiler.setClientTimings(ClientTimings.fromRequest(request));
            if (profiler.getClientTimings() != null) {
                needsSave = true;
            }
        }

        if (!profiler.hasUserViewed()) {
            profiler.setUserViewed(true);
            needsSave = true;
        }

        if (needsSave) {
            MiniProfiler.getSettings().getStorage().save(profiler);
        }

        if (isPopup) {
            mapper.writeValue(response.getWriter(), profiler);
        } else {
            respondWithProfilerFullPage(response, profiler);
        }
    }

    private void respondWithProfilerFullPage(HttpServletResponse response, MiniProfiler profiler)
            throws IOException {
        response.setContentType("text/html");
        String template;
        try {
            template = getUiFileContent("/share.html");
        } catch (IOException e) {
            respondNotFound(response);
            return;
        }
        Settings settings = profiler.getSettings();
        final Map<String, String> valueMap = new HashMap<>();
        valueMap.put("name", profiler.getName());
        valueMap.put("duration",
                String.format("%.1f", DurationSerializer.durationInMillis(profiler.getDuration())));
        valueMap.put("path", settings.getAbsoluteRouteBasePath());
        StringWriter profilerJsonWriter = new StringWriter();
        mapper.writeValue(profilerJsonWriter, profiler);
        valueMap.put("json", profilerJsonWriter.toString());
        valueMap.put("includes", renderIncludes(profiler, new RenderOptions()));
        valueMap.put("version", settings.getVersion());
        response.getWriter().write(substitutePlaceholders(template, valueMap));
    }

    private static class AlphanumericVariablePrefixMatcher extends StrMatcher {
        @Override
        public int isMatch(char[] buffer, int pos, int bufferStart, int bufferEnd) {
            if (buffer[pos] != VARIABLE_PREFIX) {
                return 0;
            }
            int  loc = pos + 1;
            while (loc < bufferEnd) {
                if (buffer[loc] == VARIABLE_SUFFIX) {
                    // 0-length is not a variable.
                    return (loc - pos) > 1 ? 1 : 0;
                }
                if (!Character.isLetterOrDigit(buffer[loc])) {
                    return 0;
                }
                loc++;
            }
            return 0;
        }
    }
}
