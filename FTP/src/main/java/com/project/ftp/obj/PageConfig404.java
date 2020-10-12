package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
@JsonIgnoreProperties(ignoreUnknown = true)

public class PageConfig404 {
    private HashMap<String, String> pageMapping404;

    public HashMap<String, String> getPageMapping404() {
        return pageMapping404;
    }

    public void setPageMapping404(HashMap<String, String> pageMapping404) {
        this.pageMapping404 = pageMapping404;
    }

    @Override
    public String toString() {
        return "PageConfig404{" +
                "pageMapping404=" + pageMapping404 +
                '}';
    }
}
