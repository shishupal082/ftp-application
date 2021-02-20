package com.project.ftp.obj;

import com.project.ftp.mysql.MysqlUser;

public class RelatedUserDataV2 {
    private String username;
    private boolean isValid;
//    private String name;
//    private String email;
//    private String mobile;

    public RelatedUserDataV2(String username, boolean isValid) {
        this.username = username;
        this.isValid = isValid;
    }
    public RelatedUserDataV2(MysqlUser mysqlUser) {
        if (mysqlUser == null) {
            return;
        }
        this.username = mysqlUser.getUsername();
        this.isValid = true;
//        this.name = mysqlUser.getName();
//        this.email = mysqlUser.getEmail();
//        this.mobile = mysqlUser.getMobile();
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

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }

    @Override
    public String toString() {
        return "RelatedUserData{" +
                "username='" + username + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
