package com.thinking.backendmall.config.security;

public final class AuthContext {
    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {
    }

    public static void set(AuthUser user) {
        HOLDER.set(user);
    }

    public static Long getUserId() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUserId();
    }

    public static String getRoleKey() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getRoleKey();
    }

    public static String getUsername() {
        AuthUser user = HOLDER.get();
        return user == null ? null : user.getUsername();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static final class AuthUser {
        private final Long userId;
        private final String username;
        private final String roleKey;

        public AuthUser(Long userId, String username, String roleKey) {
            this.userId = userId;
            this.username = username;
            this.roleKey = roleKey;
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getRoleKey() {
            return roleKey;
        }
    }
}
