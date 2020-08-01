package com.project.ftp.restart;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.service.StaticService;

public class RestartApplication {
    final AppConfig appConfig;
    public RestartApplication(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    private void restart() {
//        ShutdownTask shutdownTask = appConfig.getShutdownTask();
        ShutdownTask shutdownTask = new ShutdownTask(appConfig);
        try {
            shutdownTask.execute(null, null);
            StaticService.printLog("Application shutting down...");
        } catch (Exception e) {
            e.printStackTrace();
            StaticService.printLog("Error in system shutting down");
        }
        StartApplication startApplication = new StartApplication(appConfig);
        startApplication.start();
    }
    public void checkForRestart() {
        String currentDate = StaticService.getDateStrFromPattern(AppConstant.DATE_FORMAT);
        String startDate = appConfig.getConfigDate();
        if (!currentDate.equals(startDate)) {
            StaticService.printLog("Date changed, restarting application: " + startDate + currentDate);
            this.restart();
        }
    }
}
