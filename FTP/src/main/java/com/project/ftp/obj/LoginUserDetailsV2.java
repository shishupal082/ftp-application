package com.project.ftp.obj;

import java.util.HashMap;
import java.util.Map;

public class LoginUserDetailsV2 {
    private String username;
    private String displayName;
    private boolean isLogin;
    private HashMap<String, Boolean> roles;
    /* Why added displayName?
    * It will be required for api/get_login_user_details (Right now it is not used)
    * */
    public LoginUserDetailsV2(LoginUserDetails loginUserDetails) {
        displayName = "";
        if (loginUserDetails != null) {
            username = loginUserDetails.getUsername();
            isLogin = loginUserDetails.getLogin();
        } else {
            username = "";
            isLogin = false;
        }
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public HashMap<String, Boolean> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, Boolean> roles) {
        this.roles = roles;
    }
    public String toJsonString() {
        String jsonDisplayName = displayName == null ? "" : displayName;
        String result = "{"+
                "\"username\":\""+username+"\""+
                ", \"isLogin\":\""+isLogin+"\""+
                ", \"displayName\":\""+jsonDisplayName+"\"";
        if (roles != null) {
            for (Map.Entry<String, Boolean> entry: roles.entrySet()) {
                result += ",\"" + entry.getKey() + "\":\""+entry.getValue()+"\"";
            }
        }
        result += "}";
        return result;
    }
    @Override
    public String toString() {
        return "LoginUserDetailsV2{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isLogin=" + isLogin +
                ", roles=" + roles +
                '}';
    }
}
