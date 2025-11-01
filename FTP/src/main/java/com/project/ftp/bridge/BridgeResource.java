package com.project.ftp.bridge;

import com.project.ftp.bridge.config.BridgeConfig;
import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;
import com.project.ftp.config.AppConfig;
import com.project.ftp.event.EventTracking;
import com.project.ftp.intreface.BridgeToApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeResource {
    private final static Logger logger = LoggerFactory.getLogger(BridgeResource.class);
    private final AppConfig appConfig;
    private final EventTracking eventTracking;
    public BridgeResource(final AppConfig appConfig, final EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
    }
    private BridgeTracking getBridgeTracking() {
        BridgeToAppInterface bridgeToAppInterface = new BridgeToApp(eventTracking);
        return new BridgeTracking(bridgeToAppInterface);
    }
    private BridgeService getBridgeService() {
        BridgeConfig bridgeConfig = new BridgeConfig(appConfig.getFtpConfiguration().getEmailConfig(), appConfig.getFtpConfiguration().getCreatePasswordEmailConfig());
        return new BridgeService(bridgeConfig, this.getBridgeTracking());
    }
    public void sendCreatePasswordOtpEmail(BridgeRequestSendCreatePasswordOtp request, String configDataFilePath) {
        logger.info("Request for sending create password otp email: {}", request);
        request = new BridgeRequestSendCreatePasswordOtp(request);
        BridgeTracking bridgeTracking = this.getBridgeTracking();
        BridgeService bridgeService = this.getBridgeService();
        try {
            bridgeService.sendCreatePasswordOtpEmail(request, configDataFilePath);
        } catch (BridgeException e) {
            logger.info("Error in sending create password otp email: {}", e.getMessage());
            bridgeTracking.trackFailureSendEmail(e.getErrorCode(), request, configDataFilePath);
        }
    }
}
