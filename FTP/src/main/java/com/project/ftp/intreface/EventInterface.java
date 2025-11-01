package com.project.ftp.intreface;

public interface EventInterface {
    void addText(String configDataFilePath, String username, String event, String status, String reason, String comment);
    void addTextV2(String configDataFilePath, String username, String event, String status, String reason, String comment);
}
