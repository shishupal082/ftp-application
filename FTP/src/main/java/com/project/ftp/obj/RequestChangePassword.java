package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestChangePassword {
    @JsonProperty("old_password")
    private String old_password;
    @JsonProperty("new_password")
    private String new_password;
    @JsonProperty("confirm_password")
    private String confirm_password;

    public String getOld_password() {
        if (StaticService.isInValidString(old_password)) {
            return null;
        }
        return old_password.trim();
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        if (StaticService.isInValidString(new_password)) {
            return null;
        }
        return new_password.trim();
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getConfirm_password() {
        if (StaticService.isInValidString(confirm_password)) {
            return null;
        }
        return confirm_password.trim();
    }

    public void setConfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

    @Override
    public String toString() {
        return "RequestChangePassword{" +
                "old_password='" + "*****" + '\'' +
                ", new_password='" + "*****" + '\'' +
                ", confirm_password='" + "*****" + '\'' +
                '}';
    }
}
