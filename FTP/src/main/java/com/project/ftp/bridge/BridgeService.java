package com.project.ftp.bridge;

import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeService {
    final static Logger logger = LoggerFactory.getLogger(BridgeService.class);
    private final EmailService emailService;
    public BridgeService(BridgeConfig bridgeConfig, BridgeTracking bridgeTracking) {
        this.emailService = new EmailService(bridgeTracking, bridgeConfig.getEmailConfig(),
                bridgeConfig.getCreatePasswordEmailConfig());
    }
    public void sendCreatePasswordOtpEmail(BridgeRequestSendCreatePasswordOtp request) throws BridgeException {
        emailService.sendCreatePasswordOtpEmail(request);
    }
}
