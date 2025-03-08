package com.luka.gamesellerrating.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestUtil {

    public String generateDeviceFingerprint() {
        return String.join("-",
                getSessionId(),
                getClientIp(),
                getUserAgent(),
                getAcceptLanguage(),
                getScreenResolution());
    }

    private String getSessionId() {
        return getCurrentRequest().getSession().getId();
    }

    private String getUserAgent() {
        return getCurrentRequest().getHeader("User-Agent");
    }

    private String getAcceptLanguage() {
        return getCurrentRequest().getHeader("Accept-Language");
    }

    private String getScreenResolution() {
        String height = getCurrentRequest().getHeader("Sec-CH-Viewport-Height");
        String width = getCurrentRequest().getHeader("Sec-CH-Viewport-Width");
        return height + "x" + width;
    }

    private String getClientIp() {
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
