package com.project.ftp.session;

import com.project.ftp.common.SysUtils;
import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.filters.LogFilter;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionService {
    final static Logger logger = LoggerFactory.getLogger(SessionService.class);
    final AppConfig appConfig;
    final SysUtils sysUtils = new SysUtils();
    public SessionService(final AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    private SessionData getCurrentSessionData(HttpServletRequest request) throws AppException {
        String sessionId = getSessionId(request);
        HashMap<String, SessionData> sessionData = appConfig.getSessionData();
        SessionData currentSessionData = sessionData.get(sessionId);
        if (currentSessionData == null) {
            logger.info("CurrentSessionId: {}, not found in: {}", sessionId, sessionData);
            throw new AppException(ErrorCodes.INVALID_SESSION);
        }
        return currentSessionData;
    }
    public String getCurrentSessionDataV2(HttpServletRequest request) {
        String sessionDataStr = "";
        try {
            SessionData sessionData = this.getCurrentSessionData(request);
            sessionDataStr = sessionData.toString();
        } catch (AppException ignored) {}
        return sessionDataStr;
    }
    private String getSessionId(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        return (String) httpSession.getAttribute(AppConstant.SESSION_COOKIE_DATA);
    }
    public void setSessionId(HttpServletRequest request, String newSessionId) {
        String oldSessionId = this.getSessionId(request);
        if (oldSessionId != null && !oldSessionId.equals(newSessionId)) {
            logger.info("sessionId change from: {} to {}", oldSessionId, newSessionId);
            LogFilter.addSessionIdInLog(newSessionId);
        } else if (oldSessionId == null) {
            logger.info("sessionId change from: null to {}", newSessionId);
            LogFilter.addSessionIdInLog(newSessionId);
        }
        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(AppConstant.SESSION_COOKIE_DATA, newSessionId);
    }

    private SessionData getNewSession(String sessionId) {
        Long currentTime = sysUtils.getTimeInMsLong();
        return new SessionData(sessionId, currentTime);
    }

    private SessionData getNewSessionV2(String sessionId, String username, String orgUsername) {
        SessionData sessionData = this.getNewSession(sessionId);
        sessionData.setUsername(username);
        sessionData.setOrgUsername(orgUsername);
        return sessionData;
    }

    public String updateSessionId(HttpServletRequest request, String currentSessionId, EventTracking eventTracking) {
        String newSessionId = StaticService.createUUIDNumber();
        if (currentSessionId.length() > 40 || currentSessionId.length() < 30) {
            logger.info("Invalid currentSessionId length(30 to 40): {}, created new: {}", currentSessionId, newSessionId);
            currentSessionId = newSessionId;
        }
        HashMap<String, SessionData> sessionDataHashMap = appConfig.getSessionData();
        String sessionId;
        SessionData sessionData;
        ArrayList<String> deletedSessionIds = new ArrayList<>();
        Long currentTime = sysUtils.getTimeInMsLong();
        if (sessionDataHashMap != null) {
            for (Map.Entry<String, SessionData> sessionDataMap: sessionDataHashMap.entrySet()) {
                sessionId = sessionDataMap.getKey();
                sessionData = sessionDataMap.getValue();
                if (currentTime - sessionData.getUpdatedTime() >= AppConstant.SESSION_TTL) {
                    eventTracking.trackExpiredUserSession(sessionData);
                    deletedSessionIds.add(sessionId);
                }
                if (currentSessionId.equals(sessionId)) {
                    sessionData.setUpdatedTime(sysUtils.getTimeInMsLong());
                }
            }
            if (sessionDataHashMap.get(currentSessionId) == null) {
                sessionDataHashMap.put(currentSessionId, this.getNewSession(currentSessionId));
            }
        } else {
            logger.info("sessionDataHashMap is null, create new.");
            sessionDataHashMap = new HashMap<>();
            sessionDataHashMap.put(currentSessionId, this.getNewSession(currentSessionId));
        }
        for (String str: deletedSessionIds) {
            logger.info("Deleted expired session data, at: {}, is: {}", currentTime, sessionDataHashMap.get(str));
            sessionDataHashMap.remove(str);
            if (str.equals(currentSessionId)) {
                logger.info("currentSessionId: {}, is expired, create new: {}", currentSessionId, newSessionId);
                sessionDataHashMap.put(newSessionId, this.getNewSession(newSessionId));
                currentSessionId = newSessionId;
            }
        }
        appConfig.setSessionData(sessionDataHashMap);
        this.setSessionId(request, currentSessionId);
        return currentSessionId;
    }
    public void loginUser(HttpServletRequest request, String username, String orgUsername) throws AppException {
        if (username == null || username.isEmpty()) {
            logger.info("userLogin request username is incorrect: {}", username);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (StaticService.isInValidString((orgUsername))) {
            orgUsername = username;
        }
        HashMap<String, SessionData> sessionData = appConfig.getSessionData();
        String oldSessionId = this.getSessionId(request);
        String newSessionId = StaticService.createUUIDNumber();
        sessionData.remove(oldSessionId);
        sessionData.put(newSessionId, this.getNewSessionV2(newSessionId, username, orgUsername));
        this.setSessionId(request, newSessionId);
    }
    public void loginOtherUser(HttpServletRequest request, String username) throws AppException {
        if (username == null || username.isEmpty()) {
            logger.info("userLogin request username is incorrect: {}", username);
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        HashMap<String, SessionData> sessionData = appConfig.getSessionData();
        String sessionId = this.getSessionId(request);
        SessionData currentSessionData = sessionData.get(sessionId);
        if (currentSessionData != null) {
            currentSessionData.setUsername(username);
        }
    }
    public void logoutUser(HttpServletRequest request) {
        HashMap<String, SessionData> sessionData = appConfig.getSessionData();
        String sessionId = this.getSessionId(request);
        SessionData currentSessionData = sessionData.get(sessionId);
        String loginUsername, orgLoginUsername;
        if (currentSessionData != null) {
            loginUsername = currentSessionData.getUsername();
            orgLoginUsername = currentSessionData.getOrgUsername();
            if (loginUsername != null && orgLoginUsername != null && !loginUsername.equals(orgLoginUsername)) {
                currentSessionData.setUsername(orgLoginUsername);
            } else {
                sessionData.remove(sessionId);
                this.setSessionId(request, StaticService.createUUIDNumber());
            }
        }
    }
    public String getSessionParam(final HttpServletRequest request, String param) {
        String result = null;
        try {
            SessionData sessionData = this.getCurrentSessionData(request);
            if (AppConstant.USERNAME.equals(param)) {
                result = sessionData.getUsername();
            } else if (AppConstant.ORG_USERNAME.equals(param)) {
                result = sessionData.getOrgUsername();
            }
            if (result == null || result.isEmpty()) {
                result = null;
            }
        } catch (Exception e) {
            logger.info("Error in getting getSessionParam: {}, {}", param, e.getMessage());
        }
        return result;
    }
}
