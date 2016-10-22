package com.miniprofiler.samples;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.miniprofiler.Closeable;
import com.miniprofiler.MiniProfiler;
import com.miniprofiler.RenderOptions;
import com.miniprofiler.RequestHandlerServlet;

public class TestStepsServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        MiniProfiler profiler = MiniProfiler.getCurrent();

        response.setContentType("text/html");
        String sb = "<!DOCTYPE html>\n" +
                "<html>" +
                "<head>" +
                "<title>Test steps</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                RequestHandlerServlet.renderIncludes(profiler, new RenderOptions()) +
                "</head>" +
                "<body>" +
                "<a href='index.jsp'>Home</a>" +
                "</body>" +
                "</html>";
        response.getWriter().write(sb);

        try {
            try (Closeable step = profiler.step("Doing complex stuff")) {
                try (Closeable stepA = profiler.step("Step A")) {
                    // Simulate fetching a URL.
                    try (Closeable timing = profiler.customTiming("http", "GET http://google.com")) {
                        Thread.sleep(10);
                    }
                }
                try (Closeable stepB = profiler.step("Step B")) {
                    // Simulate fetching a URL.
                    try (Closeable timing = profiler.customTiming("http", "GET http://stackoverflow.com")) {
                        Thread.sleep(20);
                    }

                    try (Closeable timing = profiler.customTiming("redis", "SET \"mykey\" 10")) {
                        Thread.sleep(5);
                    }
                }
            }

            // Now something that loops.
            for (int i = 0; i < 15; i++) {
                try (Closeable timing = profiler.customTiming("redis", "SET \"mykey\" 10")) {
                    Thread.sleep(i);
                }
            }
        } catch (InterruptedException e) {
            // Just ignore it.
        }
    }
}
