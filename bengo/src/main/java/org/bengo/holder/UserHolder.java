package org.bengo.holder;

public class UserHolder {

    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();
    // 添加
    public static void set(Object id){
        if (id == null) {
            throw new IllegalArgumentException("用户 ID 不能为 null");
        }
        userThreadLocal.set(Long.valueOf(id.toString()));
    }
    // 获取
    public static Long get() {
        Long userId = userThreadLocal.get();
        if (userId == null) {
            throw new IllegalStateException("没有获取到用户id");
        }
        return userId;
    }
    // 删除
    public static void clear(){
        userThreadLocal.remove();
    }
}