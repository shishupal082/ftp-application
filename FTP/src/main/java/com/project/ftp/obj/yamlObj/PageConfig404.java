package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

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
    public void update(final PageConfig404 pageConfig404) {
        if (pageConfig404 == null) {
            return;
        }
        if (pageConfig404.getPageMapping404() == null) {
            return;
        }
        if (this.pageMapping404 == null) {
            this.pageMapping404 = new HashMap<>();
        }
        for(Map.Entry<String, Page404Entry> entry: pageConfig404.getPageMapping404().entrySet()) {
            this.pageMapping404.put(entry.getKey(), entry.getValue());
        }
    }
    @Override
    public String toString() {
        return "PageConfig404{" +
                "pageMapping404=" + pageMapping404 +
                '}';
    }
}
