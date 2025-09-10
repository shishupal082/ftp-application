package com.project.ftp.config;

import java.util.HashMap;

public enum ApiRoleAccess {
    IS_LOGIN("isLogin"),
    IS_DEV_USER("isDevUser"),
    IS_ADMIN_USER("isAdminUser"),
    IS_USERS_CONTROL_ENABLE("isUsersControlEnable"),
    IS_LOGIN_OTHER_USER_ENABLE("isLoginOtherUserEnable"),
    IS_ADD_TEXT_ENABLE("isAddTextEnable"),
    IS_INFINITE_TTL_LOGIN_USER("isInfiniteTTLLoginUser"),
    IS_DELETE_TEXT_ENABLE("isDeleteTextEnable"),
    IS_UPLOAD_FILE_ENABLE("isUploadFileEnable"),
    IS_DELETE_FILE_ENABLE("isDeleteFileEnable"),
    IS_GET_ALL_USERS_ENABLE("getAllUsersEnable");

    private final String roleAccessName;
    ApiRoleAccess(String name) {
        this.roleAccessName = name;
    }
    public String getRoleAccessName() {
        return roleAccessName;
    }


    private static final HashMap<String, ApiRoleAccess> lookup = new HashMap<>();

    static {
        for (ApiRoleAccess apiRoleAccess : ApiRoleAccess.values()) {
            lookup.put(apiRoleAccess.getRoleAccessName(), apiRoleAccess);
        }
    }
    public static ApiRoleAccess get(String apiRoleAccess) {
        if (apiRoleAccess == null) {
            return null;
        }
        return lookup.get(apiRoleAccess);
    }

}
