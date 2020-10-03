package com.project.ftp.bridge;


/**
 * Created by shishupalkumar on 10/02/17.
 */

public class BridgeException extends RuntimeException {
    private final BridgeErrorCode bridgeErrorCode;

    public BridgeException(final BridgeErrorCode bridgeErrorCode) {
        super(bridgeErrorCode.getErrorString());
        this.bridgeErrorCode = bridgeErrorCode;
    }
    public BridgeErrorCode getErrorCode() {
        return bridgeErrorCode;
    }

    @Override
    public String toString() {
        return "BridgeException{" +
                "bridgeErrorCode=" + bridgeErrorCode +
                '}';
    }
}
