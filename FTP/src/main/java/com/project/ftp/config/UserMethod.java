package com.project.ftp.config;

public enum UserMethod {
    BLOCKED("blocked"),
    NEW_USER("new_user"),
    REGISTER("register"),
    REGISTER_ERROR("register_error"),
    FORGOT_PASSWORD("forgot_password"),
    CREATE_PASSWORD("create_password"),
    CREATE_PASSWORD_ERROR("create_password_error"),
    CHANGE_PASSWORD("change_password");
    private final String userMethod;
    UserMethod(String method) {
        this.userMethod = method;
    }
    public String getUserMethod() {
        return userMethod;
    }
}
