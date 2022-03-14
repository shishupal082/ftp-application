package com.project.ftp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SocialLoginType {
    LOGIN_WITH_GOOGLE("LOGIN_WITH_GOOGLE");

    /*Not supported by browser
        - doc, docx, xls, xlsx, ppt, pptx
        - zar, exe, mp3, mp4, mov, avi
    */

    private final static Logger logger = LoggerFactory.getLogger(SocialLoginType.class);

    private final String loginType;
    SocialLoginType(String fileMimeType) {
        this.loginType = fileMimeType;
    }

    public String getType() {
        return loginType;
    }

}
