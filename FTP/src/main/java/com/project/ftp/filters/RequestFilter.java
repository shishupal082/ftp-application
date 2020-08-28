package com.project.ftp.filters;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.exceptions.ErrorCodes;
import com.project.ftp.service.StaticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.util.ArrayList;

/**
 * Created by shishupalkumar on 11/02/17.
 */
@Priority(501)
public class RequestFilter implements ContainerRequestFilter {
    final static private Logger logger = LoggerFactory.getLogger(RequestFilter.class);
    @Context
    private HttpServletRequest httpServletRequest;
    private final AppConfig appConfig;
    private final EventTracking eventTracking;
    public RequestFilter(final AppConfig appConfig, EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.eventTracking = eventTracking;
    }
    public void filter(final ContainerRequestContext requestContext) throws AppException {
        String cookieData = StaticService.getCookieData(httpServletRequest);
        String newCookieData = null;
        String origin = requestContext.getHeaderString(AppConstant.ORIGIN);
        if (origin != null) {
            ArrayList<String> allowedOrigin = appConfig.getFtpConfiguration().getAllowedOrigin();
            if (allowedOrigin != null) {
                if (!allowedOrigin.contains(origin)) {
                    logger.info("UnAuthorized Origin: {}, AllowedOrigin: {}", origin, allowedOrigin);
                    throw new AppException(ErrorCodes.UNAUTHORIZED_ORIGIN);
                }
            } else {
                logger.info("allowedOrigin not defined in FtpConfiguration: {}", origin);
//                throw new AppException(ErrorCodes.CONFIG_ERROR);
            }
        }
        if (cookieData == null || cookieData.equals("")) {
            newCookieData = StaticService.createUUIDNumber();
            logger.info("Invalid session cookieData : {}, Created new : {}", cookieData, newCookieData);
            cookieData = newCookieData;
        }
        cookieData = StaticService.updateSessionId(httpServletRequest, appConfig, cookieData, eventTracking);
        String requestedPath = StaticService.getPathUrlV2(requestContext);
        if (!AppConstant.FAVICON_ICO_PATH.equals(requestedPath)) {
            logger.info("RequestFilter executed, cookieData : {}", cookieData);
        }
        StaticService.checkForDateChange(appConfig, eventTracking);
    }
}
