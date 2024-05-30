package org.disk.context;

import org.disk.entity.UserInfo;

public class BaseContext {

    public static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();


    public static Long getCurrentId() {
        return threadLocal.get().getId();
    }

    public static void setCurrentUser(UserInfo user) {
        threadLocal.set(user);
    }

    public static UserInfo getCurrentUser() {
        return threadLocal.get();
    }

    public static void removeCurrentUser() {
        threadLocal.remove();
    }

}
