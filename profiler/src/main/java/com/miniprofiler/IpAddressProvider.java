package com.miniprofiler;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class IpAddressProvider implements UserProvider {
    @Override
    public String getUser(ServletRequest request) {
        String result = "";
        String remoteAddr = request.getRemoteAddr();
        if (remoteAddr != null) {
            result += remoteAddr;
        }

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String forwardedAddr = httpRequest.getHeader("X-Forwarded-For");
            if (!StringUtils.isBlank(forwardedAddr)) {
                result += " - " + forwardedAddr;
            }
        }
        return result;
    }
}
