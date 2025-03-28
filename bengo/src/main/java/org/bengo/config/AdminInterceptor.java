package org.bengo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bengo.holder.UserHolder;
import org.bengo.util.JwtUtils;
import org.bengo.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AdminInterceptor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String token = request.getHeader("Authorization");
        log.info("Intercepting request: {}, Authorization: {}", uri, token);

        if ("OPTIONS".equals(request.getMethod())) {
            log.info("Handling OPTIONS request: {}", uri);
            return true;
        }

        if (uri.startsWith("/bengo/chat")) {
            log.info("Skipping JWT check for /bengo/chat: {}", uri);
            return true;
        }

        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("Invalid or missing token for: {}", uri);
            response(R.error().message("请登录后再操作"), response);
            return false;
        }

        Long userId = JwtUtils.getUserId(request);
        if (userId == null) {
            log.warn("Failed to get userId from token for: {}", uri);
            response(R.error().message("无效的 token"), response);
            return false;
        }

        UserHolder.set(userId);
        log.info("UserHolder set successfully, userId: {}", userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserHolder.clear();
    }

    private boolean response(R r, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(r));
        response.getWriter().flush();
        return false;
    }
}