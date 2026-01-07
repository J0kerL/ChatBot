package com.chatbot.common.interceptor;

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

        log.info("====== 请求开始 ====== ");
        log.info("请求方法: {}", method);
        log.info("请求URI: {}", uri);

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

}
