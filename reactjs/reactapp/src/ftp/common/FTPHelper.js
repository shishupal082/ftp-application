import $S from "../../interface/stack.js";
import Api from "../../common/Api";
import TemplateHelper from "../../common/TemplateHelper";
import Config from "./Config";
import PageData from "./PageData";

var FTPHelper = {};

(function($S){
var DT = $S.getDT();
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

        var isAdmin = Data.getData("isUserAdmin", false);
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
        if (["dashboard", "upload_file", "change_password", "logout"].indexOf(pageName) >= 0) {
            if (!isLogin) {
                Config.location.href = Config.basepathname + "/login";
                redirectStatus = true;
            }
        } else if (["forgot_password", "login", "register"].indexOf(pageName) >= 0) {
            if (isLogin) {
                Config.location.href = Config.basepathname + "/dashboard";
                redirectStatus = true;
            }
        }
        return redirectStatus;
    }
});
FTP.extend({
    _generateFileinfoField: function(Data, currentUserName, fileResponse, currentPdfLink) {
        var template = Data.getTemplate("dashboard.fileinfo", {});
        var field, fullFilename;
        fullFilename = fileResponse.actualFilename;

        // Changing display text parameter
        field = TemplateHelper(template).searchFieldV2("dashboard.fileinfo.filename");
        field.text = fileResponse.filename;
        if (fullFilename === currentPdfLink) {
            TemplateHelper.addClassTemplate(field, "dashboard.fileinfo.filename", "text-danger");
        }
        // Changing view parameter
        field = TemplateHelper(template).searchFieldV2("dashboard.fileinfo.view");
        field.value = fullFilename;
        if (fullFilename === currentPdfLink) {
            TemplateHelper.addClassTemplate(field, "dashboard.fileinfo.view", "disabled");
        }

        // Changing open in new tab link parameter
        field = TemplateHelper(template).searchFieldV2("dashboard.fileinfo.open-in-new-tab");
        field.href = PageData.getPdfViewLink(fullFilename);

        // Changing download link parameter
        field = TemplateHelper(template).searchFieldV2("dashboard.fileinfo.download");
        field.href = PageData.getPdfDownloadLink(fullFilename);

        // Changing delete link parameter
        field = TemplateHelper(template).searchFieldV2("dashboard.fileinfo.delete");
        field.value = fullFilename;
        if (!$S.isBooleanTrue(fileResponse.deleteOption)) {
            TemplateHelper.addClassTemplate(field, "dashboard.fileinfo.delete", "disabled");
            TemplateHelper.removeClassTemplate(field, "dashboard.fileinfo.delete", "text-danger");
        }
        return template;
    },
    getDashboardFieldOrderByDate: function(Data) {
        var apiData = PageData.getData("dashboard.apiResponseByDate", []);
        if (apiData.length < 1) {
            return Data.getTemplate("noDataFound", {});
        }
        var dashboardTemplate = Data.getTemplate("dashboard", {});
        var dashboardTemplateData = {"dashboardRow": []};
        var template2, template2Data;
        template2 = Data.getTemplate("dashboardOrderByOption", {});
        TemplateHelper.setTemplateAttr(template2, "dashboard.orderbydropdown.td", "colSpan", 3);
        dashboardTemplateData.dashboardRow.push(template2);
        var i, j, count;
        var parentTemplateName = "dashboardRowDataByDate";
        var currentPdfLink = PageData.getData("dashboard.currentPdfLink", "");
        var currentUserName = Data.getData("userName", "");
        for(i=0; i<apiData.length; i++) {
            if ($S.isArray(apiData[i].fieldData) && apiData[i].fieldData.length > 0) {
                template2 = Data.getTemplate("dashboardRowHeading", {});
                TemplateHelper.setTemplateAttr(template2, "dashboardRowHeading.heading", "colSpan", 3);

                template2Data = {"rowHeading": apiData[i].heading};
                TemplateHelper.updateTemplateText(template2, template2Data);
                dashboardTemplateData.dashboardRow.push(template2);

                template2 = Data.getTemplate("dashboard1stRowByDate", {});
                dashboardTemplateData.dashboardRow.push(template2);
                count=1;
                for (j=0; j<apiData[i].fieldData.length; j++) {
                    template2 = Data.getTemplate(parentTemplateName, {});
                    template2Data = {};
                    template2Data[parentTemplateName+".s.no"] = count++;
                    template2Data[parentTemplateName+".username"] = apiData[i].fieldData[j]["username"];

                    template2Data[parentTemplateName+".fileinfo"] = FTP._generateFileinfoField(Data, currentUserName, apiData[i].fieldData[j], currentPdfLink);
                    TemplateHelper.updateTemplateText(template2, template2Data);
                    dashboardTemplateData.dashboardRow.push(template2);
                }
            }
        }
        TemplateHelper.updateTemplateText(dashboardTemplate, dashboardTemplateData);
        return dashboardTemplate;
    },
    getDashboardField: function(Data, pageName) {
        var orderBy = PageData.getData("dashboard.orderBy", null);
        if (orderBy === "orderByDate") {
            return FTP.getDashboardFieldOrderByDate(Data);
        }
        var apiData = PageData.getData("dashboard.apiResponseByUser", []);
        if (apiData.length < 1) {
            return Data.getTemplate("noDataFound", {});
        }
        var currentUserName = Data.getData("userName", "");
        var dashboardTemplate = Data.getTemplate(pageName, {});
        var dashboardTemplateData = {"dashboardRow": []};

        var template2, template2Data;
        template2 = Data.getTemplate("dashboardOrderByOption", {});
        dashboardTemplateData.dashboardRow.push(template2);

        var i, j, count;
        var parentTemplateName = "dashboardRowData";
        var currentPdfLink = PageData.getData("dashboard.currentPdfLink", "");
        for(i=0; i<apiData.length; i++) {
            if ($S.isArray(apiData[i].fieldData) && apiData[i].fieldData.length > 0) {
                template2 = Data.getTemplate("dashboardRowHeading", {});
                template2Data = {"rowHeading": apiData[i].heading};
                TemplateHelper.updateTemplateText(template2, template2Data);
                dashboardTemplateData.dashboardRow.push(template2);

                template2 = Data.getTemplate("dashboard1stRow", {});
                dashboardTemplateData.dashboardRow.push(template2);
                count=1;
                for (j=0; j<apiData[i].fieldData.length; j++) {
                    template2 = Data.getTemplate(parentTemplateName, {});
                    template2Data = {};
                    template2Data[parentTemplateName+".s.no"] = count++;
                    template2Data[parentTemplateName+".fileinfo"] = FTP._generateFileinfoField(Data, currentUserName, apiData[i].fieldData[j], currentPdfLink);
                    TemplateHelper.updateTemplateText(template2, template2Data);
                    dashboardTemplateData.dashboardRow.push(template2);
                }
            }
        }
        TemplateHelper.updateTemplateText(dashboardTemplate, dashboardTemplateData);
        return dashboardTemplate;
    }
});
//getFieldTemplateByPageName
FTP.extend({
    displayVisibleItem: function(dashboardField) {
        var displayFileName = PageData.getData("dashboard.currentPdfLink");
        var fileinfo = FTP.generateFileInfo(displayFileName);
        var field, ext, displayLink;
        // var dcurrentDropDownValue = TemplateHelper.getTemplateAttr(Template, "dashboard.orderbydropdown", "value", null);

        var dashboardOrderBy = PageData.getData("dashboard.orderBy", null);
        TemplateHelper.setTemplateAttr(dashboardField, "dashboard.orderbydropdown", "value", dashboardOrderBy);
        if (fileinfo && $S.isString(fileinfo.ext)) {
            ext = fileinfo.ext.toLowerCase();
            displayLink = PageData.getCurrentPdfLink();
            if (Config.imgExt.indexOf(ext) >= 0) {
                field = TemplateHelper(dashboardField).searchFieldV2("dashboard.display.object.div");
                TemplateHelper.addClassTemplate(field, "dashboard.display.object.div", "d-none");

                field = TemplateHelper(dashboardField).searchFieldV2("dashboard.display.img");
                field.src = displayLink;
                field.alt = fileinfo.filename;
            } else {
                field = TemplateHelper(dashboardField).searchFieldV2("dashboard.display.img.div");
                TemplateHelper.addClassTemplate(field, "dashboard.display.img.div", "d-none");
                field = TemplateHelper(dashboardField).searchFieldV2("pdfViewObject");
                field.data = displayLink;
                // field.type = "application/" + ext;
                field = TemplateHelper(dashboardField).searchFieldV2("pdfViewEmbed");
                field.src = displayLink;
                // field.type = "application/" + ext;
            }
        }
    },
    uploadSubmitButtonStatus: function(pageName, template) {
        var formPage = ["login", "change_password", "register", "upload_file"];
        if (formPage.indexOf(pageName) < 0) {
            return template;
        }
        var submitBtnNames = {"upload_file": "upload_file.submit"};
        submitBtnNames["login"] = "login.submit";
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
        if (pageName === "upload_file") {
            var percentComplete = PageData.getData("upload_file.percentComplete", 0);
            if (formSubmitStatus === "in_progress" && $S.isNumber(percentComplete) && percentComplete > 0) {
                percentComplete = "Uploaded "+percentComplete+"%";
                TemplateHelper.setTemplateAttr(template, "upload_file.complete-status", "text", percentComplete);
            } else {
                TemplateHelper.setTemplateAttr(template, "upload_file.complete-status", "text", "");
            }
        }
        return template;
    },
    getFieldTemplateByPageName: function(Data, pageName) {
        var pageTemplate = [];
        var template = {};
        if (pageName === "upload_file") {
            template = Data.getTemplate(pageName, {});
            var message = Config.getApiConfig("uploadFileInstruction", "");
            TemplateHelper.setTemplateAttr(template, "upload_file.message", "text", message);
            FTP.uploadSubmitButtonStatus(pageName, template);
            pageTemplate.push(template);
        } else if (pageName === "dashboard") {
            var dashboardField = FTP.getDashboardField(Data, pageName);
            FTP.displayVisibleItem(dashboardField);
            pageTemplate.push(dashboardField);
        } else {
            template = Data.getTemplate(pageName, {});
            FTP.uploadSubmitButtonStatus(pageName, template);
            pageTemplate.push(template);
        }
        return pageTemplate;
    }
});
//loadPageData, loadStaticData
FTP.extend({
    loadStaticData: function(Data, callBack) {
        var url = Config.apiMapping["static_file"];
        $S.loadJsonData(null, [url], function(response, apiName, ajaxDetails) {
            if ($S.isObject(response) && $S.isObject(response.data)) {
                if ($S.isObject(response.data.config)) {
                    Config.setApiConfig(response.data.config);
                }
            }
        }, function() {
            $S.callMethod(callBack);
        }, null, Api.getAjaxApiCallMethod());
    },
    _generateDashboardResponseByUser: function(dashboardApiResponse) {
        var responseByUser = {};
        if ($S.isArray(dashboardApiResponse)) {
            var i;
            for(i=0; i<dashboardApiResponse.length; i++) {
                if ($S.isString(dashboardApiResponse[i]["username"]) && dashboardApiResponse[i]["username"].length) {
                    if (responseByUser[dashboardApiResponse[i]["username"]]) {
                        responseByUser[dashboardApiResponse[i]["username"]].push(dashboardApiResponse[i]);
                    } else {
                        responseByUser[dashboardApiResponse[i]["username"]] = [dashboardApiResponse[i]];
                    }
                }
            }
        }
        var keys = Object.keys(responseByUser).sort();
        var finalResponse = [], key;
        key = "public";
        if ($S.isArray(responseByUser[key]) && responseByUser[key].length > 0) {
            finalResponse.push({"heading": key, "fieldData": responseByUser[key]})
        }
        for(i=0; i<keys.length; i++) {
            key = keys[i];
            if (key === "public") {
                continue;
            }
            finalResponse.push({"heading": key, "fieldData": responseByUser[key]});
        }
        return finalResponse;
    },
    _generateDashboardResponseByDate: function(dashboardApiResponse) {
        var responseByDate = {};
        var i;
        if ($S.isArray(dashboardApiResponse)) {
            for(i=0; i<dashboardApiResponse.length; i++) {
                if ($S.isString(dashboardApiResponse[i]["dateHeading"]) && dashboardApiResponse[i]["dateHeading"].length) {
                    if (responseByDate[dashboardApiResponse[i]["dateHeading"]]) {
                        responseByDate[dashboardApiResponse[i]["dateHeading"]].push(dashboardApiResponse[i]);
                    } else {
                        responseByDate[dashboardApiResponse[i]["dateHeading"]] = [dashboardApiResponse[i]];
                    }
                }
            }
        }
        var keys = Object.keys(responseByDate).sort();
        var finalResponse = [], key;
        for(i=keys.length-1; i>=0; i--) {
            key = keys[i];
            if (key === "others") {
                continue;
            }
            finalResponse.push({"heading": key, "fieldData": responseByDate[key]});
        }
        key = "others";
        if ($S.isArray(responseByDate[key]) && responseByDate[key].length > 0) {
            finalResponse.push({"heading": key, "fieldData": responseByDate[key]})
        }
        return finalResponse;
    },
    generateFileInfo: function (str) {
        if (!$S.isString(str)) {
            return null;
        }
        function parseDateHeading(filename) {
            var dateHeading = "others";
            var p1 = /[1-9]{1}[0-9]{3}-[0-1][0-9]-[0-3][0-9]/i;
            var dateObj, temp;
            if ($S.isString(filename) && filename.length >= 10) {
                temp = filename.substring(0, 10);
                dateObj = DT.getDateObj(temp);
                if (dateObj !== null) {
                    if (temp.search(p1) >= 0) {
                        dateHeading = temp;
                    }
                }
            }
            return dateHeading;
        }
        var strArr = str.split("/");
        var r = {}, temp;
        if (strArr.length === 2) {
            r = {"actualFilename": str, "filename": strArr[1], "username": strArr[0], "ext": "", "dateHeading": ""};
            r["dateHeading"] = parseDateHeading(r["filename"]);
            temp = r.filename.split(".");
            if (temp.length > 1) {
                r["ext"] = temp[temp.length-1];
            }
            return r;
        }
        return null;
    },
    _generateDashboardResponse: function(response) {
        var tempResult = [];
        var finalResult = [];
        var dashboardResult = [];
        function reverseFileName(obj) {
            if (!$S.isObject(obj) || !$S.isString(obj.filepath)) {
                return obj;
            }
            var str = obj.filepath;
            var strArr = str.split("/");
            if (strArr.length === 2) {
                obj.filepath = strArr[1] + "/" + strArr[0];
            }
            return obj;
        }
        if ($S.isArray(response)) {
            var i, fileResponse;
            for(i=0; i<response.length; i++) {
                fileResponse = reverseFileName(response[i]);
                if (fileResponse !== null) {
                    tempResult.push(fileResponse);
                }
            }
            tempResult = tempResult.sort();
            for(i=0; i<tempResult.length; i++) {
                fileResponse = reverseFileName(tempResult[i]);
                if (fileResponse !== null) {
                    finalResult.push(fileResponse);
                }
            }
            for(i=finalResult.length-1; i>=0; i--) {
                fileResponse = FTP.generateFileInfo(finalResult[i].filepath);
                Object.assign(finalResult[i], fileResponse);
                dashboardResult.push(finalResult[i]);
            }
        }
        return dashboardResult;
    },
    loadPageData: function(Data, callBack) {
        var pageName = Config.getPageData("page", "");
        if (pageName === "dashboard") {
            var url = Config.apiMapping["get_files"];
            $S.loadJsonData(null, [url], function(response, apiName, ajaxDetails) {
                if ($S.isObject(response) && $S.isArray(response.data)) {
                    var dashboardApiResponse = FTP._generateDashboardResponse(response.data);
                    var apiResponseByDate = FTP._generateDashboardResponseByDate(dashboardApiResponse);
                    if (apiResponseByDate && apiResponseByDate.length > 0) {
                        if (apiResponseByDate[0].fieldData && apiResponseByDate[0].fieldData.length > 0) {
                            PageData.setData("dashboard.currentPdfLink", apiResponseByDate[0].fieldData[0].actualFilename);
                        }
                    }
                    PageData.setData("dashboard.apiResponse", dashboardApiResponse);
                    PageData.setData("dashboard.apiResponseByUser", FTP._generateDashboardResponseByUser(dashboardApiResponse));
                    PageData.setData("dashboard.apiResponseByDate", apiResponseByDate);
                }
            }, function() {
                $S.callMethod(callBack);
            }, null, Api.getAjaxApiCallMethod());
        } else {
            $S.callMethod(callBack);
        }
    }
});
FTPHelper = FTP;
})($S);

export default FTPHelper;
