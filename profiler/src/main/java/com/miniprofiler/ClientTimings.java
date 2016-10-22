package com.miniprofiler;

import javax.servlet.ServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientTimings {
    private static final String CLIENT_TIMING_PREFIX = "clientPerformance[timing][";
    private static final String CLIENT_PROBES_PREFIX = "clientProbes[";


    private List<ClientTiming> timings;
    private int redirectCount;

    public static ClientTimings fromRequest(ServletRequest request) {
        ClientTimings timing = null;
        long navigationStart = parseLong(request.getParameter(CLIENT_TIMING_PREFIX + "navigationStart]"));
        if (navigationStart > 0) {
            List<ClientTiming> timings = new ArrayList<>();
            timing = new ClientTimings();
            timing.setRedirectCount(parseInt(request.getParameter("clientPerformance[navigation][redirectCount]")));

            Map<String, ClientTiming> clientPerf = new LinkedHashMap<>();
            Map<String, ClientTiming> clientProbes = new LinkedHashMap<>();

            List<String> parameterNames = new ArrayList<>(request.getParameterMap().keySet());
            Collections.sort(parameterNames, (lhs, rhs) -> {
                boolean isLhsStartName = lhs.contains("Start]");
                boolean isRhsStartName = rhs.contains("Start]");
                if (isLhsStartName == isRhsStartName) {
                    return lhs.compareTo(rhs);
                }
                return isLhsStartName ? -1 : 1;
            });
            for (String key : parameterNames) {
                if (key.startsWith(CLIENT_TIMING_PREFIX)) {
                    long val = parseLong(request.getParameter(key));
                    val -= navigationStart;
                    // just ignore stuff that is negative ... not relevant
                    if (val > 0) {
                        String parsedName = key.substring(CLIENT_TIMING_PREFIX.length(), (key.length() - 1));
                        if (parsedName.endsWith("Start")) {
                            String shortName = parsedName.substring(0, parsedName.length() - 5);
                            clientPerf.put(shortName, new ClientTiming(parsedName, val, -1));
                        } else if (parsedName.endsWith("End")) {
                            String shortName = parsedName.substring(0, parsedName.length() - 3);
                            ClientTiming t = clientPerf.get(shortName);
                            if (t != null) {
                                t.setDuration(val - t.getStart());
                                t.setName(shortName);
                            }
                        } else {
                            clientPerf.put(parsedName, new ClientTiming(parsedName, val, -1));
                        }
                    }
                }

                if (key.startsWith(CLIENT_PROBES_PREFIX)) {
                    int probeIdEndLoc = key.indexOf(']');
                    if (probeIdEndLoc > 0) {
                        String probeId = key.substring(CLIENT_PROBES_PREFIX.length(), probeIdEndLoc);
                        ClientTiming t = clientProbes.get(probeId);
                        if (t == null) {
                            t = new ClientTiming();
                            clientProbes.put(probeId, t);
                        }
                        if (key.endsWith("[n]")) {
                            t.setName(request.getParameter(key));
                        }
                        if (key.endsWith("[d]")) {
                            long val = parseLong(request.getParameter(key));
                            if (val > 0) {
                                t.setStart(val - navigationStart);
                            }
                        }
                    }
                }
            }

            Map<String, List<ClientTiming>> clientProbesPerName = clientProbes.values().stream()
                    .collect(Collectors.groupingBy(ClientTiming::getName));
            for (List<ClientTiming> probesGroup : clientProbesPerName.values()) {
                ClientTiming current = null;
                for (ClientTiming item : probesGroup) {
                    if (current == null) {
                        current = item;
                    } else {
                        current.setDuration(item.getStart() - current.getStart());
                        timings.add(current);
                        current = null;
                    }
                }
            }

            for (ClientTiming t : clientPerf.values()) {
                t.setName(sentenceCase(t.getName()));
            }

            timings.addAll(clientPerf.values());
            Collections.sort(timings, (lhs, rhs) -> (int)(lhs.getStart() - rhs.getStart()));
            timing.setTimings(timings);
        }
        return timing;
    }

    private static long parseLong(String value) {
        // NOTE: in current context treating invalid number strings as 0 is fine.
        if (value == null) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int parseInt(String value) {
        // NOTE: in current context treating invalid number strings as 0 is fine.
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String sentenceCase(String value) {
        char[] chars = value.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                sb.append(Character.toUpperCase(chars[i]));
                continue;
            }

            if (Character.isUpperCase(chars[i])) {
                sb.append(' ');
            }

            sb.append(chars[i]);
        }
        return sb.toString();
    }

    @JsonProperty("Timings")
    public List<ClientTiming> getTimings() {
        return timings;
    }

    public void setTimings(List<ClientTiming> timings) {
        this.timings = timings;
    }

    @JsonProperty("RedirectCount")
    public int getRedirectCount() {
        return redirectCount;
    }

    public void setRedirectCount(int redirectCount) {
        this.redirectCount = redirectCount;
    }

    /**
     * A client timing probe.
     */
    public static class ClientTiming {
        private String name;
        private long start;
        private long duration;

        public ClientTiming(String name, long start, long duration) {
            this.name = name;
            this.start = start;
            this.duration = duration;
        }

        public ClientTiming() {
            this("", 0, -1);
        }

        @JsonProperty("Name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("Start")
        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        @JsonProperty("Duration")
        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }
}
