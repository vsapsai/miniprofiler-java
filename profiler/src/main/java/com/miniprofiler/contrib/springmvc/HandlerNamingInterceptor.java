package com.miniprofiler.contrib.springmvc;

import com.miniprofiler.MiniProfiler;
import com.miniprofiler.ServletRequestProfilerProvider;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor that adds the name of handler as {@link com.miniprofiler.MiniProfiler} name.
 *
 * To use it you add this interceptor to {@link org.springframework.web.servlet.config.annotation.InterceptorRegistry}.
 * One of the simplest ways to do so is in override of
 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter#addInterceptors(InterceptorRegistry)}.
 */
public class HandlerNamingInterceptor implements HandlerInterceptor {
    private final boolean shouldStartStep;

    /**
     * Create an interceptor.
     *
     * @param shouldStartStep flag that tells if should start a profiling step in addition to naming profiler.
     */
    public HandlerNamingInterceptor(boolean shouldStartStep) {
        this.shouldStartStep = shouldStartStep;
    }

    public HandlerNamingInterceptor() {
        this(false);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String className = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            MiniProfiler profiler = ServletRequestProfilerProvider.getProfilerFromRequest(request);
            profiler.setName(className + "/" + methodName);
            if (shouldStartStep) {
                profiler.step("Controller: " + className + "." + methodName);
                // Not closing the step, expecting parent step to close it.
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Do nothing.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Do nothing.
    }
}
