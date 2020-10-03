package com.project.ftp.intreface;

import com.project.ftp.mysql.MysqlUser;

public interface AppToBridgeInterface {
    void sendCreatePasswordOtpEmail(MysqlUser user);
}
