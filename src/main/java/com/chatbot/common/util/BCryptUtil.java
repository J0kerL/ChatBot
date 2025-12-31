package com.chatbot.common.util;

import cn.hutool.crypto.digest.BCrypt;

/**
 * BCrypt密码加密工具类
 * @author Diamond
 */
public class BCryptUtil {

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        String password = "admin123";
        String encoded = encode(password);
        System.out.println("原始密码：" + password);
        System.out.println("加密密码：" + encoded);
        System.out.println("验证结果：" + matches(password, encoded));
    }
}
