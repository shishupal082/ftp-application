import $$$ from '../../interface/global';
import $S from "../../interface/stack.js";
import Config from "./Config";
import FTPHelper from "./AuthHelper";
import GATracking from "./GATracking";

var PageData;

(function($S){

var CurrentFormData = $S.getDataObj();
var keys = ["platform"];

keys.push("ui.username");


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


PageData = function(arg) {
    return new PageData.fn.init(arg);
};

PageData.fn = PageData.prototype = {
    constructor: PageData,
    init: function(arg) {
        this.arg = arg;
        return this;
    }
};
$S.extendObject(PageData);

PageData.extend({
    setData: function(key, value) {
        return CurrentFormData.setData(key, value);
    },
    getData: function(key, defaultValue) {
        return CurrentFormData.getData(key, defaultValue);
    },
    getNavigatorData: function(key) {
        var result = key;
        try {
            var uiNavigator = $$$.navigator;
            if ($S.isString(uiNavigator[key])) {
                result = uiNavigator[key];
            }
        } catch(err) {
            result = "error in " + key;
        }
        return result;
    },
    getUserAgentTrackingData: function() {
        var trackingData = [];
        var trackingKey = ["platform","appVersion","appCodeName","appName"];
        for(var i=0; i<trackingKey.length; i++) {
            trackingData.push(PageData.getNavigatorData(trackingKey[i]));
        }
        return trackingData.join(",");
    },
    isAndroid: function() {
        var platform = PageData.getNavigatorData("platform");
        var appVersion = PageData.getNavigatorData("appVersion");
        var isLinuxArmv = platform.search(/linux armv/i) >= 0;
        var isLinuxAndroid = appVersion.search(/linux; android/i) >= 0;
        if (isLinuxArmv && isLinuxAndroid) {
            return true;
        }
        var event = "android_check";
        var status = "FAILURE";
        var reason = "";
        var comment = "";
        if (isLinuxArmv) {
            comment = PageData.getUserAgentTrackingData();
            reason = "LINUX_ARMV_NOT_ANDROID";
        } else if (isLinuxAndroid) {
            comment = PageData.getUserAgentTrackingData();
            reason = "ANDROID_NOT_LINUX_ARMV";
        } else {
            return false;
        }
        PageData.trackUIEvent(event, status, reason, comment);
        return false;
    }
});
PageData.extend({
    handleInputChange: function(e) {
        var currentTarget = e.currentTarget;
        var fieldName = currentTarget.name;
        CurrentFormData.setData(fieldName, currentTarget.value.trim());
    },
    handleButtonClick: function(e, Data, callBack) {
        var currentTarget = e.currentTarget;
        if (currentTarget.name === "login.submit-guest") {
            CurrentFormData.setData("guest-login-status", "true");
        }
    }
});
PageData.extend({
    parseUsername: function(str) {
        try {
            var data = JSON.parse(str);
            return data["username"];
        } catch(e) {}
        return "";
    },
    handleFormSubmit: function(e, Data, callBack) {
        var pageName = Config.getPageData("page", "");
        var url = Config.apiMapping[pageName];
        var postData = {};
        if ($S.isString(url)) {
            if (pageName === "login") {
                PageData.setData("formSubmitStatus", "in_progress");
                $S.callMethod(callBack);
                var guestLoginStatus = CurrentFormData.getData("guest-login-status", "");
                var username = CurrentFormData.getData("login.username", "");
                var password = CurrentFormData.getData("login.password", "");
                if (guestLoginStatus === "true") {
                    username = "Guest";
                    password = "Guest";
                    CurrentFormData.setData("guest-login-status", "false");
                }
                postData["username"] = username;
                postData["password"] = password;
                postData["user_agent"] = PageData.getUserAgentTrackingData();
                $S.sendPostRequest(Config.JQ, url, postData, function(ajax, status, response) {
                    PageData.setData("formSubmitStatus", "completed");
                    console.log(response);
                    if (status === "FAILURE") {
                        $S.callMethod(callBack);
                        response = {"status": "FAILURE_RESPONSE"};
                        response["data"] = PageData.parseUsername(ajax.data);
                        GATracking.trackResponse("login", response);
                        alert("Error in login, Please Try again.");
                    } else {
                        GATracking.trackResponse("login", response);
                        PageData.handleApiResponse(Data, callBack, pageName, ajax, response);
                    }
                });
            } else if (pageName === "change_password") {
                PageData.setData("formSubmitStatus", "in_progress");
                $S.callMethod(callBack);
                postData["old_password"] = CurrentFormData.getData("change_password.old_password", "");
                postData["new_password"] = CurrentFormData.getData("change_password.new_password", "");
                postData["confirm_password"] = CurrentFormData.getData("change_password.confirm_password", "");
                $S.sendPostRequest(Config.JQ, url, postData, function(ajax, status, response) {
                    PageData.setData("formSubmitStatus", "completed");
                    console.log(response);
                    if (status === "FAILURE") {
                        $S.callMethod(callBack);
                        GATracking.trackResponseAfterLogin("change_password", {"status": "FAILURE_RESPONSE"});
                        alert("Error in change password, Please Try again.");
                    } else {
                        GATracking.trackResponseAfterLogin("change_password", response);
                        PageData.handleApiResponse(Data, callBack, pageName, ajax, response);
                    }
                });
            } else if (pageName === "register") {
                PageData.setData("formSubmitStatus", "in_progress");
                $S.callMethod(callBack);
                postData["username"] = CurrentFormData.getData("register.username", "");
                postData["passcode"] = CurrentFormData.getData("register.passcode", "");
                postData["password"] = CurrentFormData.getData("register.password", "");
                postData["display_name"] = CurrentFormData.getData("register.displayName", "");
                postData["mobile"] = CurrentFormData.getData("register.mobile", "");
                postData["email"] = CurrentFormData.getData("register.email", "");
                postData["user_agent"] = PageData.getUserAgentTrackingData();
                $S.sendPostRequest(Config.JQ, url, postData, function(ajax, status, response) {
                    PageData.setData("formSubmitStatus", "completed");
                    console.log(response);
                    if (status === "FAILURE") {
                        $S.callMethod(callBack);
                        response = {"status": "FAILURE_RESPONSE"};
                        response["data"] = PageData.parseUsername(ajax.data);
                        GATracking.trackResponse("register", response);
                        alert("Error in register user, Please Try again.");
                    } else {
                        GATracking.trackResponse("register", response);
                        PageData.handleApiResponse(Data, callBack, pageName, ajax, response);
                    }
                });
            } else if (pageName === "forgot_password") {
                PageData.setData("formSubmitStatus", "in_progress");
                $S.callMethod(callBack);
                postData["username"] = CurrentFormData.getData("forgot_password.username", "");
                postData["mobile"] = CurrentFormData.getData("forgot_password.mobile", "");
                postData["email"] = CurrentFormData.getData("forgot_password.email", "");
                postData["user_agent"] = PageData.getUserAgentTrackingData();
                $S.sendPostRequest(Config.JQ, url, postData, function(ajax, status, response) {
                    PageData.setData("formSubmitStatus", "completed");
                    $S.callMethod(callBack);
                    console.log(response);
                    if (status === "FAILURE") {
                        response = {"status": "FAILURE_RESPONSE"};
                        response["data"] = PageData.parseUsername(ajax.data);
                        GATracking.trackResponse("forgot_password", response);
                        alert("Error in forgot password user, Please Try again.");
                    } else {
                        var r = {"status": response.status};
                        r["data"] = PageData.parseUsername(ajax.data);
                        GATracking.trackResponse("forgot_password", r);
                        PageData.handleApiResponse(Data, callBack, pageName, ajax, response);
                    }
                });
            } else if (pageName === "create_password") {
                PageData.setData("formSubmitStatus", "in_progress");
                $S.callMethod(callBack);
                postData["username"] = CurrentFormData.getData("create_password.username", "");
                postData["create_password_otp"] = CurrentFormData.getData("create_password.create_password_otp", "");
                postData["new_password"] = CurrentFormData.getData("create_password.new_password", "");
                postData["confirm_password"] = CurrentFormData.getData("create_password.confirm_password", "");
                postData["user_agent"] = PageData.getUserAgentTrackingData();
                $S.sendPostRequest(Config.JQ, url, postData, function(ajax, status, response) {
                    PageData.setData("formSubmitStatus", "completed");
                    console.log(response);
                    if (status === "FAILURE") {
                        $S.callMethod(callBack);
                        response = {"status": "FAILURE_RESPONSE"};
                        response["data"] = PageData.parseUsername(ajax.data);
                        GATracking.trackResponse("create_password", response);
                        alert("Error in register user, Please Try again.");
                    } else {
                        GATracking.trackResponse("create_password", response);
                        PageData.handleApiResponse(Data, callBack, pageName, ajax, response);
                    }
                });
            }
        }
    }
});
PageData.extend({
    trackUIEvent: function(event, status, reason, comment) {
        var postData = {"event": event, "status": status, "reason": reason};
        postData["comment"] = comment;
        var url = Config.apiMapping["track_event"];
        $S.sendPostRequest(Config.JQ, url, postData);
    },
    handleApiResponse: function(Data, callBack, apiName, ajax, response) {
        function getLoginRedirectUrl(response) {
            if ($S.isObject(response) && response.status === "SUCCESS") {
                if ($S.isObject(response.data) && $S.isString(response.data.loginRedirectUrl)) {
                    if (response.data.loginRedirectUrl.length > 0) {
                        return response.data.loginRedirectUrl;
                    }
                }
            }
            return Config.loginRedirectUrl;
        }
        if (["login", "register"].indexOf(apiName) >= 0) {
            if (response.status === "FAILURE") {
                $S.callMethod(callBack);
                alert(Config.getAleartMessage(response));
                if (response.failureCode === "USER_ALREADY_LOGIN") {
                    FTPHelper.pageReload();
                }
            } else {
                FTPHelper.lazyRedirect(getLoginRedirectUrl(response), 250);
            }
        } else if (["create_password"].indexOf(apiName) >= 0) {
            if (response.status === "FAILURE") {
                $S.callMethod(callBack);
                alert(Config.getAleartMessage(response));
                if (response.failureCode === "USER_ALREADY_LOGIN") {
                    FTPHelper.pageReload();
                } else if (response.failureCode === "CREATE_PASSWORD_OTP_EXPIRED") {
                    Config.location.href = "/forgot_password";
                }
            } else {
                FTPHelper.lazyRedirect(getLoginRedirectUrl(response), 250);
            }
        } else if (["forgot_password"].indexOf(apiName) >= 0) {
            if (response.status === "FAILURE") {
                alert(Config.getAleartMessage(response));
                if (response.failureCode === "USER_ALREADY_LOGIN") {
                    FTPHelper.pageReload();
                } else if (response.failureCode === "FORGOT_PASSWORD_REPEAT_REQUEST") {
                    Config.location.href = "/create_password";
                }
            } else {
                alert(Config.getSuccessMessage(response));
                Config.location.href = "/create_password";
            }
        } else if (apiName === "change_password") {
            if (response.status === "FAILURE") {
                $S.callMethod(callBack);
                alert(Config.getAleartMessage(response));
                if (response.failureCode === "UNAUTHORIZED_USER") {
                    FTPHelper.pageReload();
                }
            } else {
                FTPHelper.lazyRedirect(getLoginRedirectUrl(response), 250);
            }
        }
    }
});
})($S);

export default PageData;
