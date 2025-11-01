package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestResetCount {
    @JsonProperty("username")
    private String username;
    @JsonProperty("role_id")
    private String roleId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RequestResetCount{" +
                "username='" + username + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
