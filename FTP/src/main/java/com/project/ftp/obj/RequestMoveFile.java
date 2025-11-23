package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestMoveFile {
    @JsonProperty("filepath")
    private String filepath;
    @JsonProperty("move_dir")
    private String moveDir;
    @JsonProperty("create_move_dir")
    private String createMoveDir;
    @JsonProperty("role_id")
    private String roleId;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getMoveDir() {
        return moveDir;
    }

    public void setMoveDir(String moveDir) {
        this.moveDir = moveDir;
    }

    public String getCreateMoveDir() {
        return createMoveDir;
    }

    public void setCreateMoveDir(String createMoveDir) {
        this.createMoveDir = createMoveDir;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getTrackingData() {
        return filepath + "," + moveDir;
    }

    @Override
    public String toString() {
        return "RequestMoveFile{" +
                "filepath='" + filepath + '\'' +
                ", moveDir='" + moveDir + '\'' +
                ", createMoveDir='" + createMoveDir + '\'' +
                ", roleId='" + roleId + '\'' +
                '}';
    }
}
