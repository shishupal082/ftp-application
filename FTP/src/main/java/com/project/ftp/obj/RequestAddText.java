package com.project.ftp.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

import java.util.ArrayList;
import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)

public class RequestAddText {
    @JsonProperty("text")
    private String[] text;
    @JsonProperty("filename")
    private String filename;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("uiEntryTime")
    private String uiEntryTime;

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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getUiEntryTime() {
        return uiEntryTime;
    }

    public void setUiEntryTime(String uiEntryTime) {
        this.uiEntryTime = uiEntryTime;
    }

    public ArrayList<String> generateAddTextResponse(String orgUsername, String loginUsername, String currentTimeStamp) {
        ArrayList<String> response = new ArrayList<>();
        String result;
        if (text != null) {
            for (int i=0; i<text.length; i++) {
                if (StaticService.isInValidString(text[i])) {
                    continue;
                }
                if (orgUsername == null) {
                    result = "0,";
                } else {
                    result = orgUsername + ",";
                }
                if (currentTimeStamp == null) {
                    result += ",";
                } else {
                    result += currentTimeStamp+",";
                }
                if (loginUsername == null) {
                    result += ",";
                } else {
                    result += loginUsername+",";
                }
                if (StaticService.isInValidString(tableName)) {
                    result += AppConstant.DEFAULT_TABLE_NAME + ",";
                } else {
                    result += tableName+",";
                }
                result += StaticService.createUUIDNumber() +",";
                if (StaticService.isInValidString(uiEntryTime)) {
                    result += currentTimeStamp + ",";
                } else {
                    result += uiEntryTime+",";
                }
                result += text[i];
                response.add(result);
            }
        }
        return response;
    }

    @Override
    public String toString() {
        return "RequestAddText{" +
                "text=" + Arrays.toString(text) +
                ", filename='" + filename + '\'' +
                ", tableName='" + tableName + '\'' +
                ", uiEntryTime='" + uiEntryTime + '\'' +
                '}';
    }
}
