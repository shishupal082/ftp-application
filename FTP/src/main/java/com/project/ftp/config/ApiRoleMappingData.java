package com.project.ftp.config;

public class ApiRoleMappingData {
    private String role;
    private String source;

    public ApiRoleMappingData(String roleName, String mappingSource) {
        this.role = roleName;
        this.source = mappingSource;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "ApiRoleMappingData{" +
                "role='" + role + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
