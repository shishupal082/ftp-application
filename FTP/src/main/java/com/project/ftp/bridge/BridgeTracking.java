package com.project.ftp.bridge;

import com.project.ftp.bridge.obj.BridgeRequestSendCreatePasswordOtp;

import java.util.ArrayList;
import java.util.HashMap;

public class BridgeTracking {
    private final BridgeToAppInterface bridgeToAppInterface;

    public BridgeTracking(BridgeToAppInterface bridgeToAppInterface) {
        this.bridgeToAppInterface = bridgeToAppInterface;
    }
    public void trackFailureSendEmail(BridgeErrorCode bridgeErrorCode, BridgeRequestSendCreatePasswordOtp request) {
        String comment = bridgeErrorCode.getErrorString();
        comment += ","+request.createTrackingComment();
        bridgeToAppInterface.trackEvent(request.getUsername(), BridgeConstant.SEND_EMAIL,
                BridgeConstant.FAILURE, bridgeErrorCode.getErrorCode(), comment);
    }
    public void trackSuccessSendEmail(BridgeRequestSendCreatePasswordOtp request) {
        bridgeToAppInterface.trackEvent(request.getUsername(), BridgeConstant.SEND_EMAIL,
                BridgeConstant.SUCCESS, null, request.createTrackingComment());
    }
    public void trackAllRelatedUsers(HashMap<String, ArrayList<String>> allRelatedUsers) {
        String finalResult = allRelatedUsers.toString();
        if (finalResult == null) {
            bridgeToAppInterface.trackEvent(null, BridgeConstant.RELATED_USERS,
                    BridgeConstant.SUCCESS, null, null);
            return;
        }
        int size = 500; // Max length of comment field is 511 char
        String[] tokens = finalResult.split("(?<=\\G.{" + size + "})");
        int count = 1;
        for (String token : tokens) {
            bridgeToAppInterface.trackEvent(null, BridgeConstant.RELATED_USERS,
                    BridgeConstant.SUCCESS, Integer.toString(count++), token);
        }
    }
}
