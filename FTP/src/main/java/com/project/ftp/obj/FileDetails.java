package com.project.ftp.obj;

import com.project.ftp.service.StaticService;

import java.util.ArrayList;
import java.util.HashMap;

public class FileDetails {
    private HashMap<String, FileDetail> fileDetailHashMap; // filepath --> filedetails
    private int count;
    public FileDetails(ArrayList<ArrayList<String>> filedetailsData) {
        fileDetailHashMap = new HashMap<>();
        count = 0;
        if (filedetailsData == null) {
            return;
        }
        FileDetail fileDetail, fileDetail1;
        String filepath;
        for(int i=filedetailsData.size()-1; i>=0; i--) {
            fileDetail = new FileDetail(filedetailsData.get(i));
            filepath = fileDetail.getFilepath();
            if (filepath == null || filepath.isEmpty()) {
                continue;
            }
            fileDetail1 = fileDetailHashMap.get(filepath);
            if (fileDetail1 == null) {
                fileDetailHashMap.put(filepath, fileDetail);
                count++;
            } else {
                fileDetailHashMap.put(filepath, fileDetail1.incrementEntryCount());
            }
        }
    }
    public FileDetail searchFileByFilepath(String filepath) {
        if (fileDetailHashMap == null || filepath == null || filepath.isEmpty()) {
            return null;
        }
        return fileDetailHashMap.get(filepath);
    }

    public HashMap<String, FileDetail> getFileDetailHashMap() {
        return fileDetailHashMap;
    }

    public void setFileDetailHashMap(HashMap<String, FileDetail> fileDetailHashMap) {
        this.fileDetailHashMap = fileDetailHashMap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "FileDetails{" +
                "fileDetailHashMap=" + fileDetailHashMap +
                ", count=" + count +
                '}';
    }
}
