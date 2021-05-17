import $S from "../../interface/stack.js";
// import Api from "../../common/Api";
import TemplateHelper from "../../common/TemplateHelper";
import Config from "./Config";
import PageData from "./PageData";
// import GATracking from "./GATracking";
import TemplateHandler from "./TemplateHandler";


var FTPHelper = {};

(function($S){
// var DT = $S.getDT();
// var TextFilter = $S.getTextFilter();
var FTP = function(arg) {
    return new FTP.fn.init(arg);
};

FTP.fn = FTP.prototype = {
    constructor: FTP,
    init: function(arg) {
        this.arg = arg;
        return this;
    }
};
$S.extendObject(FTP);

//setLinkTemplate
FTP.extend({
    setLinkTemplate: function(Data) {
        var linkTemplate = Data.getTemplate("link", {});
        var field = TemplateHelper(linkTemplate).searchField("link.loginAs");
        field.text = Data.getData("userName", "");

        var isAdmin = Data.getData("isAdminTextDisplayEnable", false);
        if ($S.isBooleanTrue(isAdmin)) {
            TemplateHelper.removeClassTemplate(linkTemplate, "link.is-admin", "d-none");
        } else {
            TemplateHelper.addClassTemplate(linkTemplate, "link.is-admin", "d-none");
        }
        Data.setData("linkTemplate", linkTemplate);
        return linkTemplate;
    }
});

// checkForRedirect
FTP.extend({
    checkForRedirect: function(Data) {
        var pageName = Config.getPageData("page", "");
        var isLogin = Data.getData("isLogin", false);
        var redirectStatus = false;
        if (["change_password", "logout"].indexOf(pageName) >= 0) {
            if (!isLogin) {
                FTPHelper.lazyRedirect("/login", 250);
                redirectStatus = true;
            }
        } else if (["forgot_password", "login", "register", "create_password"].indexOf(pageName) >= 0) {
            if (isLogin) {
                FTPHelper.lazyRedirect(Config.loginRedirectUrl, 250);
                redirectStatus = true;
            }
        }
        return redirectStatus;
    },
    pageReload: function() {
        Config.location.reload();
    },
    lazyRedirect: function(url, delay) {
        if ($S.isNumber(delay)) {
            window.setTimeout(function() {
                Config.location.href = Config.basepathname + url;
            }, delay);
        } else {
            Config.location.href = Config.basepathname +  url;
        }
    }
});

//getFieldTemplateByPageName
FTP.extend({
    uploadSubmitButtonStatus: function(pageName, template) {
        var formPage = ["login", "forgot_password", "create_password", "change_password", "register"];
        if (formPage.indexOf(pageName) < 0) {
            return template;
        }
        var submitBtnNames = {};
        submitBtnNames["login"] = "login.submit";
        submitBtnNames["forgot_password"] = "forgot_password.submit";
        submitBtnNames["create_password"] = "create_password.submit";
        submitBtnNames["change_password"] = "change_password.submit";
        submitBtnNames["register"] = "register.submit";
        var formSubmitStatus = PageData.getData("formSubmitStatus", "");
        if (formSubmitStatus === "in_progress") {
            TemplateHelper.removeClassTemplate(template, submitBtnNames[pageName], "btn-primary");
            TemplateHelper.addClassTemplate(template, submitBtnNames[pageName], "btn-link disabled");
        } else {
            TemplateHelper.addClassTemplate(template, submitBtnNames[pageName], "btn-primary");
            TemplateHelper.removeClassTemplate(template, submitBtnNames[pageName], "btn-link disabled");
        }
        return template;
    },
    getFieldTemplateByPageName: function(Data, pageName) {
        var pageTemplate = [];
        var template = {};
        if (pageName === "login") {
            template = Data.getTemplate(pageName, {});
            var isGuestLinkEnable = Config.getPageData("is_guest_enable", "false");
            if (isGuestLinkEnable !== "true") {
                TemplateHelper.addClassTemplate(template, "login.guest-login-link", "d-none");
            }
            FTP.uploadSubmitButtonStatus(pageName, template);
            pageTemplate.push(template);
        } else {
            template = Data.getTemplate(pageName, {});
            FTP.uploadSubmitButtonStatus(pageName, template);
            pageTemplate.push(template);
        }
        var footerTemplate = Data.getTemplate("footerLinkJson", {});
        var footerTemplateAfterLogin = Data.getTemplate("footerLinkJsonAfterLogin", {});
        var isLogin = Data.getData("isLogin", false);
        if (isLogin) {
            footerTemplate = footerTemplateAfterLogin;
        }
        var field = TemplateHelper(pageTemplate).searchFieldV2("footer");
        if ($S.isObject(field) && field.name === "footer") {
            footerTemplate = TemplateHandler.checkUserDependentFooterLink(footerTemplate);
            TemplateHelper.setTemplateAttr(field, "footer", "text", footerTemplate);
        }
        return pageTemplate;
    }
});
FTPHelper = FTP;
})($S);

export default FTPHelper;
