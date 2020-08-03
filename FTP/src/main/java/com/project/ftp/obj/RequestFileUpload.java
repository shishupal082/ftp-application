package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestFileUpload {
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("heading")
    private String heading;

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
        return "RequestFileUpload{" +
                "subject='" + subject + '\'' +
                ", heading='" + heading + '\'' +
                '}';
    }
}
