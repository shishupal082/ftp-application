package com.project.ftp.obj;

public class SingleThread {
    private int index;
    private String name;
    private String url;
    public SingleThread(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SingleThread{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
