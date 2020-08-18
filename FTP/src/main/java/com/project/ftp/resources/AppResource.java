package com.project.ftp.resources;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.FileServiceV2;
import com.project.ftp.service.UserService;
import com.project.ftp.view.AppView;
import com.project.ftp.view.CommonView;
import com.project.ftp.view.IndexView;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_JSON)
public class AppResource {
    final static Logger logger = LoggerFactory.getLogger(AppResource.class);
    final AppConfig appConfig;
    final FileServiceV2 fileServiceV2;
    final UserService userService;
    final String appViewFtlFileName;
    final EventTracking eventTracking;
    public AppResource(final AppConfig appConfig, final UserService userService, final EventTracking eventTracking) {
        this.appConfig = appConfig;
        this.fileServiceV2 = new FileServiceV2(appConfig, userService);
        this.userService = userService;
        this.appViewFtlFileName = AppConstant.APP_VIEW_FTL_FILENAME;
        this.eventTracking = eventTracking;
    }
    @GET
    public Response indexPage(@Context HttpServletRequest request) throws URISyntaxException {
        logger.info("Loading indexPage");
        /*
         * It will load resource path from app Config
         * */
        String reRoutePath = appConfig.getFtpConfiguration().getIndexPageReRoute();
        logger.info("indexPage : redirect from / to: {}", reRoutePath);
        return Response.seeOther(new URI(reRoutePath)).build();
    }
    @GET
    @Path("/index")
    public Response getIndex() throws URISyntaxException {
        logger.info("getIndex : redirect from /index to /");
        return Response.seeOther(new URI("/")).build();
    }
    @GET
    @Path("/view/resource")
    public IndexView getViewResource(@Context HttpServletRequest request) {
        logger.info("Loading indexPage: {}", userService.getUserDataForLogging(request));
        return new IndexView(request, null);
    }
    @GET
    @Path("/view/file/{username}/{filename2}")
    @UnitOfWork
    public Response viewFile(@Context HttpServletRequest request,
                           @PathParam("username") String username,
                           @PathParam("filename2") String filename2,
                           @QueryParam("iframe") String isIframe) {
        String filename = username+"/"+filename2;
        logger.info("Loading viewFile: {}, isIframe: {}", filename, isIframe);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        Response.ResponseBuilder r;
        ApiResponse apiResponse = new ApiResponse();
        try {
            pathInfo = fileServiceV2.searchRequestedFileV2(request, filename);
            eventTracking.addSuccessViewFile(request, filename, isIframe);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackViewFileFailure(request, filename, ae.getErrorCode(), isIframe);
            apiResponse = new ApiResponse(ae.getErrorCode());
        }

        if (pathInfo != null) {
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
        if (AppConstant.TRUE.equals(isIframe)) {
            return Response.status(Response.Status.OK).entity(
                    apiResponse.toJsonString()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(new CommonView(request, "page_not_found_404.ftl")).build();
    }
    @GET
    @Path("/download/file/{username}/{filename2}")
    @UnitOfWork
    public Object downloadFile(@Context HttpServletRequest request,
                               @PathParam("username") String username,
                               @PathParam("filename2") String filename2) {
        String filename = username+"/"+filename2;
        logger.info("Loading downloadFile: {}, user: {}",
                filename, userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        Response.ResponseBuilder r;
        try {
            pathInfo = fileServiceV2.searchRequestedFileV2(request, filename);
            eventTracking.addSuccessDownloadFile(request, filename);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackDownloadFileFailure(request, filename, ae.getErrorCode());
        }
        if (pathInfo != null) {
            File file = new File(pathInfo.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                r = Response.ok(inputStream);
                String responseHeader = "attachment; filename=" + pathInfo.getFileName();
                r.header(HttpHeaders.CONTENT_DISPOSITION, responseHeader);
                return r.build();
            } catch (Exception e) {
                logger.info("Error in loading file: {}", pathInfo);
            }
        }
        return new CommonView(request, "page_not_found_404.ftl");
    }
    @GET
    @Path("/dashboard")
    public AppView dashboard(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName,"dashboard", userService, appConfig);
    }
    @GET
    @Path("/login")
    public AppView login(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "login", userService, appConfig);
    }
    @GET
    @Path("/logout")
    @UnitOfWork
    public AppView logout(@Context HttpServletRequest request) {
        eventTracking.trackLogout(request);
        userService.logoutUser(request);
        return new AppView(request, appViewFtlFileName, "logout", userService, appConfig);
    }
    @GET
    @Path("/register")
    public AppView register(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "register", userService, appConfig);
    }
    @GET
    @Path("/upload_file")
    public AppView uploadFile(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "upload_file", userService, appConfig);
    }
    @GET
    @Path("/change_password")
    public AppView changePassword(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "change_password", userService, appConfig);
    }
    @GET
    @Path("/forgot_password")
    @UnitOfWork
    public AppView forgotPassword(@Context HttpServletRequest request) {
        eventTracking.addForgotPassword(request);
        return new AppView(request, appViewFtlFileName, "forgot_password", userService, appConfig);
    }
    @Path("{default: .*}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    public Object defaultMethod(@Context HttpServletRequest request) {
        return fileServiceV2.handleDefaultUrl(request);
    }
}
