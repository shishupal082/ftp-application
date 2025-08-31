package com.project.ftp.bridge.standalone.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Sequence {
    private String id;
    private Boolean confirmationRequired;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getConfirmationRequired() {
        return confirmationRequired;
    }

    public void setConfirmationRequired(Boolean confirmationRequired) {
        this.confirmationRequired = confirmationRequired;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "id='" + id + '\'' +
                ", confirmationRequired=" + confirmationRequired +
                '}';
    }
}
