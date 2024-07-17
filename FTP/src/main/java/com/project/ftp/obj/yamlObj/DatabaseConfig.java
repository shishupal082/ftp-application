package com.project.ftp.obj.yamlObj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class DatabaseConfig {
    @JsonProperty("database")
    private DatabaseParams databaseParams;
    public DatabaseConfig() {}

    public DatabaseParams getDatabase() {
        return databaseParams;
    }

    public void setDatabase(DatabaseParams databaseParams) {
        this.databaseParams = databaseParams;
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "database=" + databaseParams +
                '}';
    }
}
