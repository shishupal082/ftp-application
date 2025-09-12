package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.mysql.MysqlUser;

public class RelatedUserData {
    private boolean isValid;
    private String username;
    private String mobile;
    private String email;
    private String name;
    private String passcode;
    private String createPasswordOtp;
    private int methodRequestCount;
    private String method;

    public RelatedUserData(String username) {
        this.isValid = false;
        this.username = username;
    }
    public RelatedUserData(MysqlUser mysqlUser) {
        if (mysqlUser == null) {
            return;
        }
        this.isValid = true;
        this.username = mysqlUser.getUsername();
        this.mobile = mysqlUser.getMobile();
        this.email = mysqlUser.getEmail();
        this.name = mysqlUser.getName();
        this.passcode = AppConstant.MaskDataString;//mysqlUser.getPasscode();
        this.createPasswordOtp = mysqlUser.getCreatePasswordOtp();
        this.methodRequestCount = mysqlUser.getChangePasswordCount();
        this.method = mysqlUser.getMethod();
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getCreatePasswordOtp() {
        return createPasswordOtp;
    }

    public void setCreatePasswordOtp(String createPasswordOtp) {
        this.createPasswordOtp = createPasswordOtp;
    }

    public int getMethodRequestCount() {
        return methodRequestCount;
    }

    public void setMethodRequestCount(int methodRequestCount) {
        this.methodRequestCount = methodRequestCount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "RelatedUserData{" +
                "isValid=" + isValid +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", passcode='" + AppConstant.MaskDataString + '\'' +
                ", createPasswordOtp='" + AppConstant.MaskDataString + '\'' +
                ", methodRequestCount=" + methodRequestCount +
                ", method='" + method + '\'' +
                '}';
    }
}
