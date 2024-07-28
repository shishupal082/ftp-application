package com.project.ftp.obj;

import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathInfo {
    final static Logger logger = LoggerFactory.getLogger(PathInfo.class);
    private String detectedAt;
    private String path;
    private String type; // file, folder, AppConstant.FTL_VIEW_TYPE, AppConstant.UNAUTHORISED_JSON_DATA
    private String parentFolder; // In case of file only
    private String fileName; // fileName can be ftl.view.id also
    private String filenameWithoutExt;
    private String extension;
    private String mediaType; // Mapping for mimeType
    private String size;
    private double sizeInKb;
    public PathInfo() {}
    public PathInfo(final String path) {
        this.path = path;
    }
    public PathInfo(final String type, final String fileName) {
        this.type = type;
        this.fileName = fileName;
    }

    public String getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(String detectedAt) {
        this.detectedAt = detectedAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilenameWithoutExt() {
        return filenameWithoutExt;
    }

    public void setFilenameWithoutExt(String filenameWithoutExt) {
        this.filenameWithoutExt = filenameWithoutExt;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void findExtension() {
        if (AppConstant.FILE.equals(this.type) && this.fileName != null) {
            String[] fileNameArr = this.fileName.split("\\.");
            String filenameWithoutExt = "";
            if (fileNameArr.length > 0) {
                if (fileNameArr.length == 1) {
                    this.extension = "";
                } else {
                    this.extension = fileNameArr[fileNameArr.length-1];
                }
            }
            for (int i=0; i<fileNameArr.length-1; i++) {
                if (i==0) {
                    filenameWithoutExt = fileNameArr[i];
                } else {
                    filenameWithoutExt += "." + fileNameArr[i];
                }
            }
            if (filenameWithoutExt.isEmpty()) {
                filenameWithoutExt = this.fileName;
            }
            this.filenameWithoutExt = filenameWithoutExt;
        }
    }
    public void findMimeType() {
        if (extension == null) {
            return;
        }
        //        if (mimeType == null) {
            // Can be search in ftpConfiguration, but as of now not required
//            logger.info("directoryConfig:mimeType not configured in env_config");
//        }
        this.mediaType = StaticService.getFileMimeTypeValue(extension);
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
    private void setSizeFromKb(double sizeInKb) {
        String pathSize;
        if (sizeInKb < 1000) {
            pathSize = String.format("%1.3f", sizeInKb) + " kb";
        } else if (sizeInKb < 1000*1000) {
            pathSize = String.format("%1.3f", sizeInKb/1000) + " mb";
        } else {
            pathSize = String.format("%1.3f", sizeInKb/(1000*1000)) + " gb";
        }
        this.setSize(pathSize);
    }

    public double getSizeInKb() {
        return sizeInKb;
    }

    public void setSizeInKb(double sizeInKb) {
        this.sizeInKb = sizeInKb;
        this.setSizeFromKb(sizeInKb);
    }

    @Override
    public String toString() {
        return "PathInfo{" +
                "path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", parentFolder='" + parentFolder + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filenameWithoutExt='" + filenameWithoutExt + '\'' +
                ", extension='" + extension + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", size='" + size + '\'' +
                ", sizeInKb=" + sizeInKb +
                '}';
    }
}
