package com.thinking.backendmall.config.security;

public final class AuthContext {
    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    // 功能：处理authcontext
    private AuthContext() {
    }

    // 功能：处理set
    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    // 功能：获取用户id
    public static Long getUserId() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    // 功能：获取角色key
    public static String getRoleKey() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getRoleKey();
    }

    // 功能：获取用户名
    public static String getUsername() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    // 功能：处理clear
    public static void clear() {
        HOLDER.remove();
    }

    public static final class AuthUser {
        private final Long userId;
        private final String username;
        private final String roleKey;

        // 功能：处理auth用户
        public AuthUser(Long userId, String username, String roleKey) {
            this.userId = userId;
            this.username = username;
            this.roleKey = roleKey;
        }

        // 功能：获取用户id
        public Long getUserId() {
            return userId;
        }

        // 功能：获取用户名
        public String getUsername() {
            return username;
        }

        // 功能：获取角色key
        public String getRoleKey() {
            return roleKey;
        }
    }
}
