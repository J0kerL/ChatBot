package com.scaffold.common.interceptor;

import com.scaffold.common.util.JwtUtil;
import com.scaffold.common.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 * @author Diamond
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取Token
        String authHeader = request.getHeader(tokenHeader);

        if (authHeader != null && authHeader.startsWith(tokenPrefix + " ")) {
            // 提取Token
            String token = authHeader.substring(tokenPrefix.length() + 1);

            // 验证Token
            if (jwtUtil.validateToken(token)) {
                // 解析用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);

                // 存入ThreadLocal
                UserContext.setUserId(userId);
                UserContext.setUsername(username);

                log.debug("用户认证成功：userId={}, username={}", userId, username);
                return true;
            } else {
                log.warn("Token验证失败");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        log.warn("请求头中未找到Token");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
}
