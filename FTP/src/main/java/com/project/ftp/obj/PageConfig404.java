package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
@JsonIgnoreProperties(ignoreUnknown = true)

/*
- if requested path is file then only we have to process
- if directory or not file, no processing of 404 config file

if file
    - if roll access is valid string
        - authorised
            - return file
        - not authorised
            - 404
     - else
        - return file
if not file
    - process as it is

* */

public class PageConfig404 {
    private HashMap<String, Page404Entry> pageMapping404;

    public HashMap<String, Page404Entry> getPageMapping404() {
        return pageMapping404;
    }

    public void setPageMapping404(HashMap<String, Page404Entry> pageMapping404) {
        this.pageMapping404 = pageMapping404;
    }

    @Override
    public String toString() {
        return "PageConfig404{" +
                "pageMapping404=" + pageMapping404 +
                '}';
    }
}
