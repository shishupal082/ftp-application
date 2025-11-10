package com.project.ftp.bridge.resources;

public interface BridgeToAppInterface {
    void trackEvent(String configFilePath, String username, String eventName, String status, String reason, String comment);
}
