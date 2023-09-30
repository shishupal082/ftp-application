package com.project.ftp.resources;

import com.project.ftp.config.AppConfig;
import com.project.ftp.config.AppConstant;
import com.project.ftp.event.EventName;
import com.project.ftp.event.EventTracking;
import com.project.ftp.exceptions.AppException;
import com.project.ftp.obj.ApiResponse;
import com.project.ftp.obj.LoginUserDetails;
import com.project.ftp.obj.PathInfo;
import com.project.ftp.service.AuthService;
import com.project.ftp.service.FileServiceV2;
import com.project.ftp.service.RequestService;
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
    private final static Logger logger = LoggerFactory.getLogger(AppResource.class);
    private final AppConfig appConfig;
    private final FileServiceV2 fileServiceV2;
    private final UserService userService;
    private final AuthService authService;
    private final String appViewFtlFileName;
    private final EventTracking eventTracking;
    private final RequestService requestService;
    public AppResource(final AppConfig appConfig,
                       final UserService userService,
                       final EventTracking eventTracking,
                       final AuthService authService) {
        this.appConfig = appConfig;
        this.fileServiceV2 = new FileServiceV2(appConfig, userService);
        this.userService = userService;
        this.appViewFtlFileName = AppConstant.APP_VIEW_FTL_FILENAME;
        this.eventTracking = eventTracking;
        this.authService = authService;
        this.requestService = new RequestService(appConfig, userService, fileServiceV2);
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
        return new IndexView(null, appConfig);
    }
    @GET
    @Path("/view/file/{username}/{filename2}")
    @UnitOfWork
    public Response viewFile(@Context HttpServletRequest request,
                           @PathParam("username") String username,
                           @PathParam("filename2") String filename2,
                           @QueryParam("container") String container,
                           @QueryParam("u") String uiUsername) {
        String filename = username+"/"+filename2;
        logger.info("Loading viewFile: {}, container: {}", filename, container);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        Response.ResponseBuilder r;
        ApiResponse apiResponse = new ApiResponse();
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.searchRequestedFileV2(loginUserDetails, filename);
            eventTracking.addSuccessViewFile(request, EventName.VIEW_FILE, filename, container, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackViewFileFailure(request, EventName.VIEW_FILE, filename, ae.getErrorCode(), container, uiUsername);
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
        if (AppConstant.IFRAME.equals(container)) {
            return Response.status(Response.Status.OK).entity(
                    apiResponse.toJsonString()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(new CommonView("page_not_found_404.ftl", appConfig)).build();
    }
    @GET
    @Path("/view/any-file")
    @UnitOfWork
    public Response viewAnyFile(@Context HttpServletRequest request,
                             @QueryParam("filepath") String filepath,
                             @QueryParam("container") String container,
                             @QueryParam("u") String uiUsername) {
        logger.info("Loading viewAnyFile, filepath: {}, container: {}, u: {}", filepath, container, uiUsername);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        Response.ResponseBuilder r;
        ApiResponse apiResponse = new ApiResponse();
        try {
            authService.isLogin(request);
            pathInfo = fileServiceV2.searchRequestedFileV3(filepath);
            eventTracking.addSuccessViewFile(request, EventName.VIEW_ANY_FILE, filepath, container, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackViewFileFailure(request, EventName.VIEW_ANY_FILE, filepath, ae.getErrorCode(),
                    container, uiUsername);
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
        if (AppConstant.IFRAME.equals(container)) {
            return Response.status(Response.Status.OK).entity(
                    apiResponse.toJsonString()).type(MediaType.APPLICATION_JSON).build();
        }
        return Response.ok(new CommonView("page_not_found_404.ftl", appConfig)).build();
    }
    @GET
    @Path("/view/redirect")
    @UnitOfWork
    public Response viewRedirect(@Context HttpServletRequest request,
                                @QueryParam("url") String url,
                                @QueryParam("container") String container,
                                @QueryParam("u") String uiUsername) throws URISyntaxException {
        logger.info("Loading viewRedirect, url: {}, container: {}, u: {}", url, container, uiUsername);
        logger.info("user: {}", userService.getUserDataForLogging(request));
        logger.info("viewRedirect : redirect from: /view/redirect to: " + url);
        if (url != null && !"/view/redirect".equals(url)) {
            return Response.seeOther(new URI(url)).build();
        }
        return Response.ok(new CommonView("page_not_found_404.ftl", appConfig)).build();
    }
    @GET
    @Path("/download/file/{username}/{filename2}")
    @UnitOfWork
    public Object downloadFile(@Context HttpServletRequest request,
                               @PathParam("username") String username,
                               @PathParam("filename2") String filename2,
                               @QueryParam("u") String uiUsername) {
        String filename = username+"/"+filename2;
        logger.info("Loading downloadFile: {}, user: {}",
                filename, userService.getUserDataForLogging(request));
        PathInfo pathInfo = null;
        Response.ResponseBuilder r;
        try {
            authService.isLogin(request);
            LoginUserDetails loginUserDetails = userService.getLoginUserDetails(request);
            pathInfo = fileServiceV2.searchRequestedFileV2(loginUserDetails, filename);
            eventTracking.addSuccessDownloadFile(request, filename, uiUsername);
        } catch (AppException ae) {
            logger.info("Error in searching requested file: {}", ae.getErrorCode().getErrorCode());
            eventTracking.trackDownloadFileFailure(request, filename, ae.getErrorCode(), uiUsername);
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
        return new CommonView("page_not_found_404.ftl", appConfig);
    }
    @GET
    @Path("/users_control")
    public AppView usersControl(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "users_control", userService, appConfig);
    }
    @GET
    @Path("/permission_control")
    public AppView permissionControl(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "permission_control", userService, appConfig);
    }
    @GET
    @Path("/compare_control")
    public AppView compareControl(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "compare_control", userService, appConfig);
    }
    @GET
    @Path("/login_other_user")
    public AppView loginOtherUser(@Context HttpServletRequest request) {
        return new AppView(request, appViewFtlFileName, "login_other_user", userService, appConfig);
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
    @UnitOfWork
    public AppView register(@Context HttpServletRequest request) {
        eventTracking.trackLandingPage(request, EventName.REGISTER);
        return new AppView(request, appViewFtlFileName, "register", userService, appConfig);
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
        eventTracking.trackLandingPage(request, EventName.FORGOT_PASSWORD);
        return new AppView(request, appViewFtlFileName, "forgot_password", userService, appConfig);
    }
    @GET
    @Path("/create_password")
    @UnitOfWork
    public AppView createPassword(@Context HttpServletRequest request) {
        eventTracking.trackLandingPage(request, EventName.CREATE_PASSWORD);
        return new AppView(request, appViewFtlFileName, "create_password", userService, appConfig);
    }
    @GET
    @Path("/database_files")
    @UnitOfWork
    public AppView databaseFiles(@Context HttpServletRequest request) {
        eventTracking.trackLandingPage(request, EventName.APP_DATA);
        return new AppView(request, appViewFtlFileName, "database_files", userService, appConfig);
    }
    /**
     * Used for loading static files like css, js, img, ...etc
     */
    @Path("/assets-dir/{default: .*}")
    @GET
    public Object assetsDir(@Context HttpServletRequest request) {
        return requestService.getAssets(request);
    }
    /**
     * Used when accessing from browser
     */
    @Path("{default: .*}")
    @GET
    public Object defaultMethod(@Context HttpServletRequest request) {
        return requestService.handleDefaultUrl(request);
    }
    /**
     * Used while accessing from api and response is text_html
     */
    @Path("{default: .*}")
    @POST
    public Object defaultMethodPostV2(@Context HttpServletRequest request) {
        logger.info("Post Request received with: Consume APPLICATION_JSON and Produce APPLICATION_JSON");
        return requestService.handleDefaultUrl(request);
    }
    /**
     * Used while accessing from api and response is json
     */
    @Path("{default: .*}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Object defaultMethodPostV3(@Context HttpServletRequest request) {
        logger.info("Post Request received with: Consume APPLICATION_JSON and Produce APPLICATION_JSON");
        return requestService.handleDefaultUrl(request);
    }
}
