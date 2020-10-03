package com.project.ftp.bridge;

import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;

public class BridgeTracking {
    private final BridgeToAppInterface bridgeToAppInterface;
    private final String eventName = "send_email";
    public BridgeTracking(BridgeToAppInterface bridgeToAppInterface) {
        this.bridgeToAppInterface = bridgeToAppInterface;
    }
    public void trackFailureSendEmail(BridgeErrorCode bridgeErrorCode, BridgeRequestSendCreatePasswordOtp request) {
        String comment = bridgeErrorCode.getErrorString();
        comment += ","+request.createTrackingComment();
        bridgeToAppInterface.trackEvent(request.getUsername(), eventName,
                BridgeConstant.FAILURE, bridgeErrorCode.getErrorCode(), comment);
    }
    public void trackSuccessSendEmail(BridgeRequestSendCreatePasswordOtp request) {
        bridgeToAppInterface.trackEvent(request.getUsername(), eventName,
                BridgeConstant.SUCCESS, null, request.createTrackingComment());
    }
}
