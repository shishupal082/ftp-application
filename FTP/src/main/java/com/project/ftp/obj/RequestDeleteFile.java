package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestDeleteFile {
    @JsonProperty("filename")
    private String filename;

    public String getFilename() {
        if (StaticService.isInValidString(filename)) {
            return null;
        }
        return filename.trim();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "RequestDeleteFile{" +
                "filename='" + filename + '\'' +
                '}';
    }
}
