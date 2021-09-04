package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

public class ResponseFilesInfo {
    private String filepath; // uploadedBy + / + filename.ext or uploadedBy + /database/+ + filename.ext
    private String fileUsername;
    private String filename;
    private String subject;
    private String heading;
    private boolean viewOption;
    private boolean deleteOption;
    public ResponseFilesInfo(String fileUsername, String fileNamStr,
                             LoginUserDetails loginUserDetails, boolean addDatabasePath) {
        if (fileUsername == null || fileNamStr == null || loginUserDetails == null) {
            return;
        }
        String loginUsername = loginUserDetails.getUsername();
        if (StaticService.isInValidString(loginUsername)) {
            return;
        }
        if (addDatabasePath) {
            this.filepath = fileUsername + "/" + AppConstant.DATABASE + "/" + fileNamStr;
        } else {
            this.filepath = fileUsername + "/" + fileNamStr;
        }
        this.fileUsername = fileUsername;
        this.filename = fileNamStr;
        this.viewOption = true;
        this.deleteOption = loginUsername.equals(fileUsername);
    }
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFileUsername() {
        return fileUsername;
    }

    public void setFileUsername(String fileUsername) {
        this.fileUsername = fileUsername;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public boolean isViewOption() {
        return viewOption;
    }

    public void setViewOption(boolean viewOption) {
        this.viewOption = viewOption;
    }

    public boolean isDeleteOption() {
        return deleteOption;
    }

    public void setDeleteOption(boolean deleteOption) {
        this.deleteOption = deleteOption;
    }

    @Override
    public String toString() {
        return "ResponseFilesInfo{" +
                "filepath='" + filepath + '\'' +
                ", fileUsername='" + fileUsername + '\'' +
                ", filename='" + filename + '\'' +
                ", subject='" + subject + '\'' +
                ", heading='" + heading + '\'' +
                ", viewOption=" + viewOption +
                ", deleteOption=" + deleteOption +
                '}';
    }
}
