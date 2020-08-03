package com.project.ftp.obj;

import com.project.ftp.config.FileDeleteAccess;
import com.project.ftp.config.FileViewer;

public class ResponseFilesInfo {
    private String filepath;
    private boolean viewOption;
    private boolean deleteOption;
    private String subject;
    private String heading;
    public ResponseFilesInfo(FileDetail fileDetail, LoginUserDetails loginUserDetails) {
        if (fileDetail == null || loginUserDetails == null) {
            return;
        }
        if (fileDetail.isDeletedTrue()) {
            return;
        }
        this.filepath = fileDetail.getFilepath();
        this.subject = fileDetail.getSubject();
        this.heading = fileDetail.getHeading();
        this.viewOption = false;
        FileViewer viewer = fileDetail.getViewer();
        if (loginUserDetails.getLoginUserAdmin()) {
            this.viewOption = true;
        } else if (FileViewer.ALL == viewer) {
            this.viewOption = true;
        } else if (FileViewer.SELF == viewer) {
            if (loginUserDetails.getUsername().equals(fileDetail.getUploadedby())) {
                this.viewOption = true;
            }
        }
        this.deleteOption = false;
        FileDeleteAccess deleteAccess = fileDetail.getDeleteAccess();
        if (FileDeleteAccess.ADMIN == deleteAccess) {
            if (loginUserDetails.getLoginUserAdmin()) {
                this.deleteOption = true;
            }
        } else if (FileDeleteAccess.SELF_ADMIN == deleteAccess) {
            if (loginUserDetails.getLoginUserAdmin() ||
                    loginUserDetails.getUsername().equals(fileDetail.getUploadedby())) {
                this.deleteOption = true;
            }
        } else if (FileDeleteAccess.SELF == deleteAccess) {
            if (loginUserDetails.getUsername().equals(fileDetail.getUploadedby())) {
                this.deleteOption = true;
            }
        }
    }
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
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

    @Override
    public String toString() {
        return "ResponseFilesInfo{" +
                "filepath='" + filepath + '\'' +
                ", viewOption=" + viewOption +
                ", deleteOption=" + deleteOption +
                ", subject='" + subject + '\'' +
                ", heading='" + heading + '\'' +
                '}';
    }
}
