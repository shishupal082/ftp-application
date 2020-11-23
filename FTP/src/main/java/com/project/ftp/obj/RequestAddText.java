package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.service.StaticService;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestAddText {
    @JsonProperty("text")
    private String[] text;
    @JsonProperty("filename")
    private String filename;

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "RequestAddText{" +
                "text=" + Arrays.toString(text) +
                ", filename='" + filename + '\'' +
                '}';
    }
}
