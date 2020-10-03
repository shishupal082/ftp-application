package com.project.ftp.intreface;

import com.project.ftp.bridge.BridgeToAppInterface;
import com.project.ftp.event.EventTracking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeToApp implements BridgeToAppInterface {
    private final static Logger logger = LoggerFactory.getLogger(BridgeToApp.class);
    private final EventTracking eventTracking;
    public BridgeToApp(EventTracking eventTracking) {
        this.eventTracking = eventTracking;
    }
    @Override
    public void trackEvent(String username, String eventName, String status, String reason, String comment) {
        eventTracking.trackEventV2(username, eventName, status, reason, comment);
    }
}
