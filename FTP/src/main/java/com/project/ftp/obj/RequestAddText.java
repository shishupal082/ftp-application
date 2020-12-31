package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestAddText {
    @JsonProperty("text")
    private String[] text;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("heading")
    private String heading;

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
        return "RequestAddText{" +
                "text=" + Arrays.toString(text) +
                ", filename='" + filename + '\'' +
                ", subject='" + subject + '\'' +
                ", heading='" + heading + '\'' +
                '}';
    }
}
