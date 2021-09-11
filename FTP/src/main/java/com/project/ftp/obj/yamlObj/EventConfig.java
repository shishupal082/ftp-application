package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class EventConfig {
    private boolean enabled;
    private boolean staticDir;
    private String eventDataFilenamePattern;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStaticDir() {
        return staticDir;
    }

    public void setStaticDir(boolean staticDir) {
        this.staticDir = staticDir;
    }

    public String getEventDataFilenamePattern() {
        return eventDataFilenamePattern;
    }

    public void setEventDataFilenamePattern(String eventDataFilenamePattern) {
        this.eventDataFilenamePattern = eventDataFilenamePattern;
    }

    @Override
    public String toString() {
        return "EventConfig{" +
                "enabled=" + enabled +
                ", staticDir=" + staticDir +
                ", eventDataFilenamePattern='" + eventDataFilenamePattern + '\'' +
                '}';
    }
}
