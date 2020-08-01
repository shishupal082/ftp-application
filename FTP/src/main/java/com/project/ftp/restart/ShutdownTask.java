package com.project.ftp.restart;

import com.project.ftp.config.AppConfig;
import com.project.ftp.service.StaticService;
import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ShutdownTask extends Task {
    final AppConfig appConfig;
    public ShutdownTask(AppConfig appConfig) {
        super("shutdown"); // the task name, used in the endpoint to execute it
        this.appConfig = appConfig;
    }
    public void execute(
            Map<String, List<String>> immutableMultimap,
            PrintWriter printWriter
    ) throws Exception {
        // kill the process asynchronously with some nominal delay
        // to allow the task http response to be sent
        new Timer().schedule(new TimerTask() {
            public void run() {
                // any custom logging / logic here prior to shutdown
                StaticService.printLog("System shutdown.");
                System.exit(0);
            }
        }, 1);
    }
}
