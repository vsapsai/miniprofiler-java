package com.miniprofiler.samples;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.miniprofiler.Closeable;
import com.miniprofiler.CustomTiming;
import com.miniprofiler.MiniProfiler;
import com.miniprofiler.RenderOptions;
import com.miniprofiler.RequestHandlerServlet;

public class TestMinSaveMillisServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        MiniProfiler profiler = MiniProfiler.getCurrent();

        response.setContentType("text/html");
        String sb = "<!DOCTYPE html>\n" +
                "<html>" +
                "<head>" +
                "<title>Test min save milliseconds</title>" +
                "<link rel='stylesheet' href='style.css'>" +
                RequestHandlerServlet.renderIncludes(profiler, new RenderOptions()) +
                "</head>" +
                "<body>" +
                "<a href='index.jsp'>Home</a>" +
                "</body>" +
                "</html>";
        response.getWriter().write(sb);

        try {
            try (Closeable step = profiler.stepIf("Should show up", 50)) {
                Thread.sleep(60);
            }
            try (Closeable step = profiler.stepIf("Should not show up", 50)) {
                Thread.sleep(10);
            }

            try (Closeable step = profiler.stepIf("Show show up with children", 10, true)) {
                Thread.sleep(5);
                try (Closeable childStep = profiler.step("Step A")) {
                    Thread.sleep(10);
                }
                try (Closeable childStep = profiler.step("Step B")) {
                    Thread.sleep(10);
                }
                try (Closeable childStep = profiler.stepIf("Should not show up", 15)) {
                    Thread.sleep(10);
                }
            }

            try (Closeable step = profiler.stepIf("Show Not show up with children", 10)) {
                Thread.sleep(5);
                try (Closeable childStep = profiler.step("Step A")) {
                    Thread.sleep(10);
                }
                try (Closeable childStep = profiler.step("Step B")) {
                    Thread.sleep(10);
                }
            }

            try (CustomTiming timing = profiler.customTimingIf("redis", "should show up", 5)) {
                Thread.sleep(10);
            }

            try (CustomTiming timing = profiler.customTimingIf("redis", "should not show up", 15)) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            // Just ignore it.
        }
    }
}
