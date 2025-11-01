package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestVerifyPermission {
    @JsonProperty("roleName")
    private String roleName;
    @JsonProperty("role_id")
    private String roleId;


    public String getRoleName() {
        if (StaticService.isInValidString(roleName)) {
            return null;
        }
        return roleName.trim();
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RequestVerifyPermission{" +
                "roleName='" + roleName + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
