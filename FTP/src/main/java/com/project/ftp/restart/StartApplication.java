package com.project.ftp.restart;


import com.project.ftp.config.AppConfig;
import com.project.ftp.service.StaticService;

import java.util.concurrent.Executors;

public class StartApplication {
    final AppConfig appConfig;
    public StartApplication(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    public void start() {
        String startCommand = appConfig.getFtpConfiguration().getAppRestartCommand();
        StaticService.printLog("Starting application");
        if (startCommand == null || startCommand.isEmpty()) {
            StaticService.printLog("Invalid start command: " + startCommand);
            return;
        }
        StaticService.printLog("startCommand: " + startCommand);
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        String homeDirectory = System.getProperty("user.home");
        StaticService.printLog("homeDirectory: " + homeDirectory + ", isWindows: "+ isWindows);
        try {
            Process process;
            if (isWindows) {
                process = Runtime.getRuntime()
                        .exec(String.format(startCommand, homeDirectory));
            } else {
                process = Runtime.getRuntime()
                        .exec(String.format(startCommand, homeDirectory));
            }
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
        } catch (Exception e) {
            e.fillInStackTrace();
            StaticService.printLog("Error in starting application");
        }
    }
}
