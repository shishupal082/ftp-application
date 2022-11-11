package com.project.ftp.service;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.obj.RequestTcp;
import com.project.ftp.session.SessionService;
import com.project.ftp.view.CommonView;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class RequestService {
    private final static Logger logger = LoggerFactory.getLogger(RequestService.class);
    private final AppConfig appConfig;
    private final UserService userService;
    private final FileServiceV2 fileServiceV2;
    public RequestService(AppConfig appConfig, UserService userService, FileServiceV2 fileServiceV2) {
        this.appConfig = appConfig;
        this.userService = userService;
        this.fileServiceV2 = fileServiceV2;
    }
    private String getPathUrl(final HttpServletRequest request) {
        String path = request.getPathInfo();
        String[] pathArr = path.split("\\?");
        if (pathArr.length > 0) {
            path = pathArr[0];
        }
        String requestedPath = StaticService.getProperDirString(path);
        requestedPath = StaticService.removeRelativePath(requestedPath);
        return requestedPath;
    }
    public static String getPathUrlV3(final ContainerRequestContext requestContext) {
//        String url = requestContext.getUriInfo().getAbsolutePath().toString();
        return requestContext.getUriInfo().getAbsolutePath().toString();
    }
    public static String getPathUrlV2(final ContainerRequestContext requestContext) {
        String path = ((ContainerRequest) requestContext).getPath(true);
        String[] pathArr = path.split("\\?");
        if (pathArr.length > 0) {
            path = pathArr[0];
        }
        return path;
    }
    public Object getAssets(HttpServletRequest request) {
        String requestedPath = this.getPathUrl(request);
        logger.info("Loading getAssets: {}, user: {}",
                requestedPath, userService.getUserDataForLogging(request));
        PathInfo pathInfo = fileServiceV2.getFileResponseV2(requestedPath);
        Response.ResponseBuilder r;
        if (pathInfo!= null && AppConstant.FILE.equals(pathInfo.getType())) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                r = Response.ok(inputStream);
                if (pathInfo.getMediaType() == null) {
                    logger.info("MediaType is not found (download now): {}", pathInfo);
                    String responseHeader = "attachment; filename=" + pathInfo.getFileName();
                    r.header(HttpHeaders.CONTENT_DISPOSITION, responseHeader);
                } else {
                    r.header(HttpHeaders.CONTENT_TYPE, pathInfo.getMediaType());
                }
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        return new CommonView("page_not_found_404.ftl", appConfig);
    }
    public Object handleDefaultUrl(HttpServletRequest request) {
        String requestedPath = this.getPathUrl(request);
        logger.info("Loading defaultMethod: {}, user: {}",
                requestedPath, userService.getUserDataForLogging(request));
        LoginUserDetails userDetails = userService.getLoginUserDetails(request);
        PathInfo pathInfo = fileServiceV2.getFileResponse(requestedPath, userDetails);
        Response.ResponseBuilder r;
        if (pathInfo!= null && AppConstant.FILE.equals(pathInfo.getType())) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                r = Response.ok(inputStream);
                if (pathInfo.getMediaType() == null) {
                    logger.info("MediaType is not found (download now): {}", pathInfo);
                    String responseHeader = "attachment; filename=" + pathInfo.getFileName();
                    r.header(HttpHeaders.CONTENT_DISPOSITION, responseHeader);
                } else {
                    r.header(HttpHeaders.CONTENT_TYPE, pathInfo.getMediaType());
                }
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        return new CommonView("page_not_found_404.ftl", appConfig);
    }
    public static String updateSessionId(HttpServletRequest request, AppConfig appConfig,
                                         UserService userService,
                                         String cookieData, EventTracking eventTracking) {
        SessionService sessionService = new SessionService(userService, appConfig);
        return sessionService.updateSessionId(request, cookieData, eventTracking);
    }
    public static String getCookieData(AppConfig appConfig, HttpServletRequest request) {
        String cookieName = appConfig.getCookieName();
        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            return null;
        }
        String cookieData = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(cookieName)) {
                cookieData = cookie.getValue();
                break;
            }
        }
        return cookieData;
    }
    public static String getRequestUserAgent(HttpServletRequest request) {
        String userAgent = null;
        if (request != null) {
            userAgent = request.getHeader(AppConstant.REQUEST_USER_AGENT);
        }
        return userAgent;
    }
    public static ApiResponse callTcp(AppConfig appConfig, RequestTcp requestTcp) throws AppException {
        String tcpId, data;
        if (requestTcp == null) {
            logger.info("requestTcp should not be null.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (requestTcp.getTcpId() == null || requestTcp.getTcpId().isEmpty()) {
            logger.info("remoteHost required.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        if (requestTcp.getData() == null || requestTcp.getData().isEmpty()) {
            logger.info("data required.");
            throw new AppException(ErrorCodes.BAD_REQUEST_ERROR);
        }
        data = requestTcp.getData();
        tcpId = requestTcp.getTcpId();
        String response = appConfig.getAppToBridge().getTcpResponse(tcpId, data);
        if (response == null) {
            throw new AppException(ErrorCodes.CONFIG_ERROR);
        }
        return new ApiResponse(response);
    }
}
