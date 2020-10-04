package com.project.ftp.bridge;

import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeResource {
    private final static Logger logger = LoggerFactory.getLogger(BridgeResource.class);
    private final BridgeService bridgeService;
    private final BridgeTracking bridgeTracking;
    public BridgeResource(BridgeConfig bridgeConfig, BridgeToAppInterface bridgeToAppInterface) {
        this.bridgeTracking = new BridgeTracking(bridgeToAppInterface);
        this.bridgeService = new BridgeService(bridgeConfig, bridgeTracking);
    }
    public void sendCreatePasswordOtpEmail(BridgeRequestSendCreatePasswordOtp request) {
        logger.info("Request for sending create password otp email: {}", request);
        request = new BridgeRequestSendCreatePasswordOtp(request);
        try {
            bridgeService.sendCreatePasswordOtpEmail(request);
        } catch (BridgeException e) {
            logger.info("Error in sending create password otp email: {}", e.getMessage());
            bridgeTracking.trackFailureSendEmail(e.getErrorCode(), request);
        }
    }
}