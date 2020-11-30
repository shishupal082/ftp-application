package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.config.FileDeleteAccess;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class FileDetail {
    final static Logger logger = LoggerFactory.getLogger(FileDetail.class);
    private String filepath; //uploadedby/filename
    private String filename;
    private String uploadedby; // username
    private String deletedby; // username
    private FileDeleteAccess deleteAccess; //self or admin or self_admin
    private String subject;
    private String heading;
    private String datetimeStamp;
    private String entryType; //upload or delete or migration
    private String isDeleted;
    private boolean isValid;
    private int entryCount;
    // for reading file_details.csv
    public FileDetail(ArrayList<String> details) {
        if (details == null) {
            logger.info("Invalid fileDetail request: null");
            isValid = false;
            return;
        }
        if (details.size() < 9) {
            isValid = false;
            if (details.size() > 1) {
                logger.info("Invalid fileDetail request: {}", details);
            }
            return;
        }
        isValid = true;
        entryCount = 1;
        datetimeStamp = details.get(0);
        filename = details.get(1);
        uploadedby = details.get(2);
        deletedby = details.get(3);
        deleteAccess = StaticService.getFileDeleteAccess(details.get(4));
        subject = StaticService.decodeComma(details.get(5));
        heading = StaticService.decodeComma(details.get(6));
        entryType = details.get(7);
        isDeleted = details.get(8);
        filepath = uploadedby + "/" +filename;
    }
    //for file upload v1 and for migration
    public FileDetail(String filename, String uploadedby,
                      FileDeleteAccess deleteAccess,
                      String entryType) {
        this.isValid = false;
        if (filename == null || filename.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid filename: {}", filename);
            return;
        }
        if (uploadedby == null || uploadedby.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid uploadedby: {}", uploadedby);
            return;
        }
        this.isValid = true;
        this.datetimeStamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat5);
        this.filename = filename;
        this.uploadedby = uploadedby;
        this.deleteAccess = deleteAccess;
        this.entryType = entryType;
        this.isDeleted = AppConstant.FALSE;
        this.filepath = this.uploadedby + "/" + this.filename;
        // deletedby, subject and heading will be null
    }
    //for file upload v2
    public FileDetail(String filename, String uploadedby,
                      String subject, String heading,
                      FileDeleteAccess deleteAccess) {
        this.isValid = false;
        if (filename == null || filename.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid filename: {}", filename);
            return;
        }
        if (uploadedby == null || uploadedby.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid uploadedby: {}", uploadedby);
            return;
        }
        if (subject == null || subject.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid subject: {}", subject);
            return;
        }
        if (heading == null || heading.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid heading: {}", heading);
            return;
        }
        this.isValid = true;
        this.datetimeStamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat5);
        this.filename = filename;
        this.uploadedby = uploadedby;
        this.deleteAccess = deleteAccess;
        this.subject = subject;
        this.heading = heading;
        this.entryType = "uploadV2";
        this.isDeleted = "false";
        this.filepath = this.uploadedby + "/" + this.filename;
        // deletedby will be null
    }
    //for file delete
    public FileDetail(String filename, String uploadedby, String deletedby) {
        this.isValid = false;
        if (filename == null || filename.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid filename: {}", filename);
            return;
        }
        if (uploadedby == null || uploadedby.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid uploadedby: {}", uploadedby);
            return;
        }
        if (deletedby == null || deletedby.isEmpty()) {
            logger.info("Invalid fileDetail request, invalid deletedby: {}", deletedby);
            return;
        }
        this.isValid = true;
        this.datetimeStamp = StaticService.getDateStrFromPattern(AppConstant.DateTimeFormat5);
        this.filename = filename;
        this.uploadedby = uploadedby;
        this.deletedby = deletedby;
        this.entryType = "delete";
        this.isDeleted = "true";
        this.filepath = this.uploadedby + "/" + this.filename;
        // heading, subject, deleteAccess will be copy paste from old data
    }
    public String generateResponseToSave() {
        String response = "";
        response += this.datetimeStamp;
        response += "," + this.filename;
        response += "," + this.uploadedby;
        response += "," + (this.deletedby != null ? this.deletedby : "");
        response += "," + (this.deleteAccess != null ? this.deleteAccess.getDeleteAccess() : "");
        response += "," + (this.subject != null ? StaticService.encodeComma(this.subject) : "");
        response += "," + (this.heading != null ? StaticService.encodeComma(this.heading) : "");
        response += "," + (this.entryType != null ? this.entryType : "");
        response += "," + this.isDeleted;
        return response;
    }
    public boolean isDeletedTrue() {
        if (isDeleted != null) {
            return isDeleted.equals("true");
        }
        return false;
    }
    public FileDetail incrementEntryCount() {
        entryCount++;
        return this;
    }
    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUploadedby() {
        return uploadedby;
    }

    public void setUploadedby(String uploadedby) {
        this.uploadedby = uploadedby;
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

    public String getDatetimeStamp() {
        return datetimeStamp;
    }

    public void setDatetimeStamp(String datetimeStamp) {
        this.datetimeStamp = datetimeStamp;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getDeletedby() {
        return deletedby;
    }

    public void setDeletedby(String deletedby) {
        this.deletedby = deletedby;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(int entryCount) {
        this.entryCount = entryCount;
    }

    public FileDeleteAccess getDeleteAccess() {
        return deleteAccess;
    }

    public void setDeleteAccess(FileDeleteAccess deleteAccess) {
        this.deleteAccess = deleteAccess;
    }

    @Override
    public String toString() {
        return "FileDetail{" +
                "filepath='" + filepath + '\'' +
                ", filename='" + filename + '\'' +
                ", uploadedby='" + uploadedby + '\'' +
                ", deletedby='" + deletedby + '\'' +
                ", deleteAccess=" + deleteAccess +
                ", subject='" + subject + '\'' +
                ", heading='" + heading + '\'' +
                ", datetimeStamp='" + datetimeStamp + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", isValid=" + isValid +
                ", entryCount=" + entryCount +
                '}';
    }
}
