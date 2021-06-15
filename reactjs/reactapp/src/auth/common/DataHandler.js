import $S from "../../interface/stack.js";
import Config from "./Config";
import Template from "./Template";
import TemplateHandler from "./TemplateHandler";
import FormHandler from "./FormHandler";
import AppHandler from "../../common/app/common/AppHandler";
// import GATracking from "./GATracking";

var DataHandler;

(function($S){

var CurrentFormData = $S.getDataObj();
var keys = ["platform"];

keys.push("pageName");
keys.push("login.username");
keys.push("login.password");
keys.push("guest-login-status");

keys.push("change_password.old_password");
keys.push("change_password.new_password");
keys.push("change_password.confirm_password");

keys.push("forgot_password.username");
keys.push("forgot_password.mobile");
keys.push("forgot_password.email");

keys.push("create_password.username");
keys.push("create_password.create_password_otp");
keys.push("create_password.new_password");
keys.push("create_password.confirm_password");

keys.push("register.username");
keys.push("register.passcode");
keys.push("register.password");
keys.push("register.displayName");
keys.push("register.mobile");
keys.push("register.email");

keys.push("formSubmitStatus"); // in_progress, completed

CurrentFormData.setKeys(keys);
CurrentFormData.setData("formSubmitStatus", "not_started");

DataHandler = function(arg) {
    return new DataHandler.fn.init(arg);
};

DataHandler.fn = DataHandler.prototype = {
    constructor: DataHandler,
    init: function(arg) {
        this.arg = arg;
        return this;
    }
};
$S.extendObject(DataHandler);

DataHandler.extend({
    setData: function(key, value) {
        return CurrentFormData.setData(key, value);
    },
    getData: function(key, defaultValue) {
        return CurrentFormData.getData(key, defaultValue);
    },
    trackUIEvent: function(event, status, reason, comment) {
        var postData = {"event": event, "status": status, "reason": reason};
        postData["comment"] = comment;
        var url = Config.apiMapping["track_event"]+"?u=" + AppHandler.GetUserData("username", "");
        $S.sendPostRequest(Config.JQ, url, postData);
    },
    isAndroid: function(username) {
        if (AppHandler.GetStaticData("android_check_enable") !== "true") {
            return false;
        }
        var event = "android_check";
        var platform = $S.getNavigatorData(Config.navigator, "platform");
        var appVersion = $S.getNavigatorData(Config.navigator, "appVersion");
        var isLinuxArmv = platform.search(/linux armv/i) >= 0;
        var isLinuxAarch = platform.search(/linux aarch/i) >= 0;
        var isLinuxAndroid = appVersion.search(/linux; android/i) >= 0;
        if ((isLinuxArmv || isLinuxAarch) && isLinuxAndroid) {
            // DataHandler.setData("platform", "Android");
            platform = "Android";
            AppHandler.Track(username, event, platform);
            return true;
        }
        var status = "FAILURE";
        var reason = "";
        var comment = "";
        if (isLinuxArmv || isLinuxAarch) {
            comment = $S.getUserAgentTrackingData(Config.navigator);
            reason = "LINUX_ARMV_OR_AARCH_NOT_ANDROID";
        } else if (isLinuxAndroid) {
            comment = $S.getUserAgentTrackingData(Config.navigator);
            reason = "ANDROID_NOT_LINUX_ARMV";
        } else {
            AppHandler.Track(username, event, "Not Android");
            return false;
        }
        AppHandler.Track(username, event, "Not Android:"+reason);
        DataHandler.trackUIEvent(event, status, reason, comment);
        return false;
    }
});
DataHandler.extend({
    checkForRedirect: function() {
        var pageName = DataHandler.getData("pageName", "");
        var isLogin = AppHandler.GetUserData("login", false);
        var redirectStatus = false;
        if ([Config.change_password, Config.logout].indexOf(pageName) >= 0) {
            if (!isLogin) {
                AppHandler.LazyRedirect("/login", 250);
                redirectStatus = true;
            }
        } else if ([Config.forgot_password, Config.login, Config.register, Config.create_password].indexOf(pageName) >= 0) {
            if (isLogin) {
                AppHandler.LazyRedirect(Config.loginRedirectUrl, 250);
                redirectStatus = true;
            }
        }
        return redirectStatus;
    },
    handlePageLoad: function() {
        AppHandler.setGtag(Config.gtag);
        var userData = Config.UserData;
        var pageData = Config.PageData;
        var userDetails = {"username": "", "displayName": "", "login": false, "roles": {}};
        var key;
        if ($S.isObject(userData)) {
            for (key in userData) {
                if (["username", "displayName"].indexOf(key) >= 0) {
                    userDetails[key] = userData[key];
                } else if (["isLogin"].indexOf(key) >= 0) {
                    userDetails["login"] = userData[key] === "true";
                } else {
                    userDetails.roles[key] = userData[key] === "true";
                }
            }
        }
        if ($S.isObject(pageData)) {
            AppHandler.SetStaticData(pageData);
        }
        AppHandler.SetUserDetails(userDetails);
    }
});
DataHandler.extend({
    AppDidMount: function(appStateCallback, appDataCallback) {
        AppHandler.TrackPageView(DataHandler.getData("pageName", ""));
        var redirectStatus = this.checkForRedirect();
        if (!redirectStatus) {
            this.reRenderApp(appStateCallback, appDataCallback);
        }
    },
    PageComponentDidMount: function(appStateCallback, appDataCallback, pageName) {
        DataHandler.setData("pageName", pageName);
    },
    OnInputChange: function(appStateCallback, appDataCallback, name, value) {
        DataHandler.setData(name, value);
    },
    OnButtonClick: function(appStateCallback, appDataCallback, name, value) {
        if (name === "login.submit-guest") {
            DataHandler.setData("guest-login-status", true);
        }
    },
    OnFormSubmit: function(appStateCallback, appDataCallback, name, value) {
        var callback = function() {
            DataHandler.reRenderApp(appStateCallback, appDataCallback);
        };
        var pageName = DataHandler.getData("pageName", "");
        switch(value) {
            case "login_form":
                FormHandler.handleLoginForm(pageName, callback);
            break;
            case "register_form":
                FormHandler.handleRegisterForm(pageName, callback);
            break;
            case "change_password_form":
                FormHandler.handleChangePasswordForm(pageName, callback);
            break;
            case "forgot_password_form":
                FormHandler.handleForgotPasswordForm(pageName, callback);
            break;
            case "create_password_form":
                FormHandler.handleCreatePasswordForm(pageName, callback);
            break;
            default:
            break;
        }
    }
});
DataHandler.extend({
    getRenderData: function(pageName) {
        var renderData = {"guest-login-status": false, "is_guest_enable": false,
                "fieldsValue": {},
                "submitBtnName": "", "formSubmitStatus": ""};
        var fieldsName = [];
        var submitBtnName = "";
        switch(pageName) {
            case "login":
                submitBtnName = pageName + ".submit";
                fieldsName = ["login.username", "login.password"];
                renderData["guest-login-status"] = DataHandler.getData("guest-login-status", false);
                renderData["isGuestLoginEnable"] = AppHandler.GetStaticData("is_guest_enable", "false") === "true";
            break;
            case "register":
                submitBtnName = pageName + ".submit";
                fieldsName = ["register.username", "register.passcode", "register.password", "register.displayName", "register.mobile", "register.email"];
            break;
            case "forgot_password":
                submitBtnName = pageName + ".submit";
                fieldsName = ["forgot_password.username", "forgot_password.mobile", "forgot_password.email"];
            break;
            case "change_password":
                submitBtnName = pageName + ".submit";
                fieldsName = ["change_password.old_password", "change_password.new_password", "change_password.confirm_password"];
            break;
            case "create_password":
                submitBtnName = pageName + ".submit";
                fieldsName = ["create_password.username", "create_password.username", "create_password.create_password_otp", "create_password.new_password", "create_password.confirm_password"];
            break;
            case "logout":
            case "noMatch":
            default:
            break;
        }
        renderData.submitBtnName = submitBtnName;
        renderData.formSubmitStatus = DataHandler.getData("formSubmitStatus", "");
        for (var i=0; i<fieldsName.length; i++) {
            renderData.fieldsValue[fieldsName[i]] = DataHandler.getData(fieldsName[i], "");
        }
        return renderData;
    }
});
DataHandler.extend({
    reRenderApp: function(appStateCallback, appDataCallback) {
        var appHeading = AppHandler.getTemplate(Template, "heading", "App Heading");
        var pageName = DataHandler.getData("pageName", "");
        var renderData = this.getRenderData(pageName);
        var renderFieldRow = TemplateHandler.getRenderField(pageName, renderData);
        appDataCallback("renderFieldRow", renderFieldRow);
        appDataCallback("appHeading", appHeading);
        appStateCallback();
    }
});
})($S);

export default DataHandler;