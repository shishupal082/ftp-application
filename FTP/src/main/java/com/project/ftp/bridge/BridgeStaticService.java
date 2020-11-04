package com.project.ftp.bridge;

import com.project.ftp.service.StaticService;

public class BridgeStaticService {
    public static boolean isInValidString (String str) {
        return StaticService.isInValidString(str);
    }
    public static String[] splitStringOnLimit (String str, String regex, int limit) {
        return StaticService.splitStringOnLimit(str, regex,limit);
    }
}
