package com.scaffold.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 日志拦截器 - 记录请求信息和耗时
 * @author Diamond
 */
@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录开始时间
        START_TIME.set(System.currentTimeMillis());

        // 获取请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = getClientIP(request);
        String queryString = request.getQueryString();

        log.info("====== 请求开始 ====== ");
        log.info("请求方法: {}", method);
        log.info("请求URI: {}", uri);
        log.info("请求参数: {}", queryString);
        log.info("客户端IP: {}", ip);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 计算耗时
        long startTime = START_TIME.get();
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        log.info("响应状态: {}", response.getStatus());
        log.info("请求耗时: {} ms", executeTime);
        log.info("====== 请求结束 ======\n");

        // 清除ThreadLocal
        START_TIME.remove();
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
