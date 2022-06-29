package com.github.nazzrrg.wherecoffeeapplication.enumerations;

public enum ERole {
    ROLE_USER("user"),
    ROLE_MODERATOR("mod"),
    ROLE_ADMIN("admin");

    private final String role;

    private ERole(String role) {
        this.role = role;
    }

    public static ERole valueOfRole(String role) {
        for (ERole e : values()) {
            if (e.role.equals(role)) {
                return e;
            }
        }
        return null;
    }
}