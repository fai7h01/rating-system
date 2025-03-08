package com.luka.gamesellerrating.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestUtil {

    public String getSessionId() {
        return getCurrentRequest().getSession().getId();
    }

    public String getClientIp() {
        HttpServletRequest request = getCurrentRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else if (ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}
