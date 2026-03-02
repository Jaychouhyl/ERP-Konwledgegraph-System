package com.smartx.scm.context;

// 这是一个极其巧妙的工具类，利用 ThreadLocal 为每个线程（每次请求）绑定一个专属的口袋
public class UserContextHolder {
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_THREAD_LOCAL.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_ID_THREAD_LOCAL.remove();
    }
}
