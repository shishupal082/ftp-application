package com.project.ftp.bridge.obj.standalone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StandAloneConfigObj {
    private StandAloneConfig standAloneConfig;

    public StandAloneConfig getStandAloneConfig() {
        return standAloneConfig;
    }

    public void setStandAloneConfig(StandAloneConfig standAloneConfig) {
        this.standAloneConfig = standAloneConfig;
    }

    @Override
    public String toString() {
        return "StandAloneConfigObj{" +
                "standAloneConfig=" + standAloneConfig +
                '}';
    }
}
