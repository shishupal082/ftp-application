import $$$ from '../../interface/global';
import $S from "../../interface/stack.js";
import TemplateHelper from "../../common/TemplateHelper.js";
import Template from "./Template";

var Config = {"name": "Config", "imgExt": ["jpg", "jpeg", "png"]};
var PageData = {};
var UserData = {};

var baseapi = $$$.baseapi;
var basepathname = $$$.basepathname;
var headingJson = $$$.headingJson;
var afterLoginLinkJson = $$$.afterLoginLinkJson;
var footerLinkJson = $$$.footerLinkJson;
var footerLinkJsonAfterLogin = $$$.footerLinkJsonAfterLogin;
var loginUserDetails = $$$.loginUserDetails;
var createPasswordOtpInstruction = $$$.createPasswordOtpInstruction;
var loginRedirectUrl = $$$.loginRedirectUrl;


if (!$S.isString(loginRedirectUrl) || loginRedirectUrl.length < 1) {
    loginRedirectUrl = "/view/resource";
}

Config.loginRedirectUrl = loginRedirectUrl;

try {
    headingJson = JSON.parse(headingJson);
    Template["heading"] = headingJson;
} catch(e) {}

try {
    afterLoginLinkJson = JSON.parse(afterLoginLinkJson);
    Template["link"] = afterLoginLinkJson;
} catch(e) {}

try {
    footerLinkJson = JSON.parse(footerLinkJson);
    Template["footerLinkJson"] = footerLinkJson;
} catch(e) {}

try {
    footerLinkJsonAfterLogin = JSON.parse(footerLinkJsonAfterLogin);
    Template["footerLinkJsonAfterLogin"] = footerLinkJsonAfterLogin;
} catch(e) {}

try {
    UserData = JSON.parse(loginUserDetails);
} catch(e) {}




var template;

if ($S.isString(createPasswordOtpInstruction) && createPasswordOtpInstruction.length > 0) {
    template = Template["create_password"];
    TemplateHelper.setTemplateAttr(template, "create_password.otp-instruction", "text", createPasswordOtpInstruction);
}
Config.JQ = $$$.JQ;
Config.location = $$$.location;
Config.baseapi = baseapi;
Config.basepathname = basepathname;

var currentPageData = $$$.currentPageData;
if ($S.isString(currentPageData)) {
    var i, dataArr;
    var strArr = currentPageData.split(",");
    for(i=0; i<strArr.length; i++) {
        dataArr = strArr[i].split("=");
        if (dataArr.length === 2) {
            PageData[dataArr[0]] = dataArr[1];
        }
    }
}

if ($S.isBooleanTrue($$$.isReactEnv)) {
    var hrefPath = $S.getUrlAttribute(Config.location.href, "hrefPath", "");
    var hrefPathMapping = {};
    hrefPathMapping[basepathname + "/login"] = "login";
    hrefPathMapping[basepathname + "/logout"] = "logout";
    hrefPathMapping[basepathname + "/register"] = "register";
    hrefPathMapping[basepathname + "/forgot_password"] = "forgot_password";
    hrefPathMapping[basepathname + "/create_password"] = "create_password";
    hrefPathMapping[basepathname + "/change_password"] = "change_password";

    var origin = Config.location.origin;
    var hrefPathArr = hrefPath.split(origin);

    if (hrefPathArr.length === 2) {
        hrefPath = hrefPathArr[1];
    }
    if ($S.isString(hrefPathMapping[hrefPath])) {
        PageData["page"] = hrefPathMapping[hrefPath];
    }
}

Config.getPageData = function(key, defaultValue) {
    if ($S.isString(PageData[key])) {
        return PageData[key];
    }
    return defaultValue;
};

Config.getUserData = function(key, defaultValue) {
    if ($S.isString(UserData[key])) {
        return UserData[key];
    }
    return defaultValue;
};

Config.getAllUserData = function(key, defaultValue) {
    return UserData;
};

var uiUsername = Config.getUserData("username", "");
// var RequestId = Config.getPageData("app_version", "");
Config.apiMapping = {};

Config.apiMapping["login"] = baseapi + "/api/login_user";
Config.apiMapping["register"] = baseapi + "/api/register_user";
Config.apiMapping["forgot_password"] = baseapi + "/api/forgot_password";
Config.apiMapping["create_password"] = baseapi + "/api/create_password";
Config.apiMapping["change_password"] = baseapi + "/api/change_password?u="+uiUsername;

Config.apiMapping["track_event"] = baseapi + "/api/track_event?u="+uiUsername;

Config.getAleartMessage = function(response) {
    var messageMap = {};
    if (!$S.isObject(response)) {
        return response;
    }
    var messageCode = response.failureCode;
    var error = response.error;
    if ($S.isString(messageMap[messageCode])) {
        return messageMap[messageCode];
    }
    return error;
};

Config.getSuccessMessage = function(response) {
    if ($S.isString(response.data)) {
        return response.data;
    }
    return "SUCCESS";
};

export default Config;
