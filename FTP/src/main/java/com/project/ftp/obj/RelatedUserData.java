package com.project.ftp.obj;

import com.project.ftp.mysql.MysqlUser;

public class RelatedUserData {
    private String username;
    private boolean isValid;
    private String name;
    private String email;
    private String mobile;
    private String method;
    private String createPasswordOtp;
    private int methodRequestCount;

    public RelatedUserData(String username, boolean isValid) {
        this.username = username;
        this.isValid = isValid;
    }
    public RelatedUserData(MysqlUser mysqlUser) {
        if (mysqlUser == null) {
            return;
        }
        this.username = mysqlUser.getUsername();
        this.isValid = true;
        this.name = mysqlUser.getName();
        this.email = mysqlUser.getEmail();
        this.mobile = mysqlUser.getMobile();
        this.method = mysqlUser.getMethod();
        this.createPasswordOtp = mysqlUser.getCreatePasswordOtp();
        this.methodRequestCount = mysqlUser.getChangePasswordCount();
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    @Override
    public String toString() {
        return "RelatedUserData{" +
                "username='" + username + '\'' +
                ", isValid=" + isValid +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", method='" + method + '\'' +
                ", createPasswordOtp='" + createPasswordOtp + '\'' +
                ", methodRequestCount=" + methodRequestCount +
                '}';
    }
}
