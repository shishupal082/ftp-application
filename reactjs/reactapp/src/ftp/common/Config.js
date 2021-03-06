import $$$ from '../../interface/global';
import $S from "../../interface/stack.js";
import TemplateHelper from "../../common/TemplateHelper.js";
import Template from "./Template";

var Config = {"name": "Config", "imgExt": ["jpg", "jpeg", "png"]};
var PageData = {};
var baseapi = $$$.baseapi;
var basepathname = $$$.basepathname;
var headingJson = $$$.headingJson;
var uploadFileInstruction = $$$.uploadFileInstruction;

try {
    headingJson = JSON.parse(headingJson);
    Template["heading"] = headingJson;
} catch(e) {}

if ($S.isString(uploadFileInstruction)) {
    var template = Template["upload_file"];
    TemplateHelper.setTemplateAttr(template, "upload_file.message", "text", uploadFileInstruction);
}

/**
var ApiConfig = {};
Config.setApiConfig = function(apiConfig) {
    if ($S.isObject(apiConfig)) {
        ApiConfig = apiConfig;
    }
}
Config.getApiConfig = function(key, defaultValue) {
    if ($S.isDefined(ApiConfig[key])) {
        return ApiConfig[key];
    }
    return defaultValue;
}
*/

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
    hrefPathMapping[basepathname + "/dashboard"] = "dashboard";
    hrefPathMapping[basepathname + "/login"] = "login";
    hrefPathMapping[basepathname + "/logout"] = "logout";
    hrefPathMapping[basepathname + "/register"] = "register";
    hrefPathMapping[basepathname + "/forgot_password"] = "forgot_password";
    hrefPathMapping[basepathname + "/create_password"] = "create_password";
    hrefPathMapping[basepathname + "/upload_file"] = "upload_file";
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

var uiUsername = Config.getPageData("username", "");
var RequestId = Config.getPageData("app_version", "");
Config.apiMapping = {};
Config.apiMapping["static_file"] = baseapi + "/api/get_static_file?v=" + RequestId;

Config.apiMapping["login"] = baseapi + "/api/login_user";
Config.apiMapping["register"] = baseapi + "/api/register_user";
Config.apiMapping["forgot_password"] = baseapi + "/api/forgot_password";
Config.apiMapping["create_password"] = baseapi + "/api/create_password";
Config.apiMapping["change_password"] = baseapi + "/api/change_password?u="+uiUsername;

Config.apiMapping["upload_file"] = baseapi + "/api/upload_file?u="+uiUsername;
Config.apiMapping["delete_file"] = baseapi + "/api/delete_file?u="+uiUsername;
Config.apiMapping["track_event"] = baseapi + "/api/track_event?u="+uiUsername;

Config.apiMapping["get_files"] = baseapi + "/api/get_files_info?v=" + RequestId;


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
