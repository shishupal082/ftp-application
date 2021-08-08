package com.project.ftp.obj;

import java.util.HashMap;
import java.util.Map;

public class LoginUserDetailsV2 {
    private String username;
    private String orgUsername;
    private String displayName;
    private boolean isLogin;
    private HashMap<String, Boolean> roles;
    /* Why added displayName?
    * It will be required for api/get_login_user_details (Right now it is not used)
    * */
    public LoginUserDetailsV2(LoginUserDetails loginUserDetails) {
        displayName = "";
        roles = new HashMap<>();
        if (loginUserDetails != null) {
            username = loginUserDetails.getUsername();
            orgUsername = loginUserDetails.getOrgUsername();
            isLogin = loginUserDetails.getLogin();
        } else {
            username = "";
            orgUsername = "";
            isLogin = false;
        }
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrgUsername() {
        return orgUsername;
    }

    public void setOrgUsername(String orgUsername) {
        this.orgUsername = orgUsername;
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
        String jsonUsername = username == null ? "" : username;
        String jsonDisplayName = displayName == null ? "" : displayName;
        String jsonOrgUsername = orgUsername == null ? "" : orgUsername;
        boolean isRolesAdded = false;
        StringBuilder result = new StringBuilder("{" +
                "\"username\":\"" + jsonUsername + "\"" +
                ", \"orgUsername\":\"" + jsonOrgUsername + "\"" +
                ", \"login\":\"" + isLogin + "\"" +
                ", \"displayName\":\"" + jsonDisplayName + "\"" +
                ", \"roles\":{");
        if (roles != null) {
            for (Map.Entry<String, Boolean> entry: roles.entrySet()) {
                if (isRolesAdded) {
                    result.append(",");
                }
                result.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
                isRolesAdded = true;
            }
        }
        result.append("}}");
        return result.toString();
    }

    @Override
    public String toString() {
        return "LoginUserDetailsV2{" +
                "username='" + username + '\'' +
                ", orgUsername='" + orgUsername + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isLogin=" + isLogin +
                ", roles=" + roles +
                '}';
    }
}
