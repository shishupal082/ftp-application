import React from 'react';
import $S from "../interface/stack.js";
// import Api from "../common/Api";

import Template from "./common/Template";
import Config from "./common/Config";
import FTPHelper from "./common/FTPHelper";
import PageData from "./common/PageData";

import RenderComponent from "./component/RenderComponent";


// var RequestId = $S.getRequestId();
// var DT = $S.getDT();
// var baseapi = Config.baseapi;
var currentPageName = Config.getPageData("page", "");
var Data = $S.getDataObj();

var keys = ["FTPTemplate", "userData", "linkTemplate"];
var userDataKeys = ["isLogin", "userName", "isUserAdmin", "userDisplayName"];
keys = keys.concat(userDataKeys);

Data.getTemplate = function(key, defaultTemplate) {
    var allTemplate = Data.getData("FTPTemplate", {});
    if ($S.isObject(allTemplate)) {
        if ($S.isDefined(allTemplate[key])) {
            return allTemplate[key];
        }
    }
    return defaultTemplate;
};
// Data.initData = function() {
//     for (var i = 0; i < keys.length; i++) {
//         if (["FTPTemplate", "userData"].indexOf(keys[i]) >= 0) {
//             continue;
//         }
//         Data.setData(keys[i], null);
//     }
// };

Data.setKeys(keys);
// Data.initData();
Data.setData("FTPTemplate", Template);
var isLogin = Config.getUserData("isLogin") === "true" ? true : false;
var isUserAdmin = Config.getUserData("isAdminUser") === "true" ? true : false;
var userName = Config.getUserData("username", "");
var userDisplayName = Config.getUserData("displayName", "");

Data.setData("isLogin", isLogin);
Data.setData("userName", userName);
Data.setData("isUserAdmin", isUserAdmin);
Data.setData("userDisplayName", userDisplayName);

PageData.setData("ui.username", userName);

if (!isUserAdmin) {
    PageData.setData("dashboard.orderBy", "orderByUsername");
} else {
    PageData.setData("dashboard.orderBy", "orderByDate");
}

function checkAndroid() {
    var isAndroid = PageData.isAndroid();
    if (isAndroid) {
        PageData.setData("platform", "Android");
    }
}


class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            renderField: []
        };
        this.dropDownChange = this.dropDownChange.bind(this);
        this.onClick = this.onClick.bind(this);
        this.onFormSubmit = this.onFormSubmit.bind(this);
    }
    onFormSubmit(e) {
        var self = this;
        e.preventDefault();
        PageData.handleFormSubmit(e, Data, function() {
            self.setRenderField();
        });
        return false;
    }
    onClick(e) {
        //On button click
        var self = this;
        PageData.handleButtonClick(e, Data, function(setRenderField) {
            if ($S.isBooleanTrue(setRenderField)) {
                self.setRenderField();
            }
        });
    }
    onChange(e) {
        // var terget = e.currentTarget;
        PageData.handleInputChange(e);
    }
    dropDownChange(e) {
        // alert(e.currentTarget.value);
        var self = this;
        PageData.handleDropDownChange(e, Data, function(setRenderField) {
            if ($S.isBooleanTrue(setRenderField)) {
                self.setRenderField();
            }
        });
    }
    setRenderField(isLoading) {
        var renderField = [];
        renderField.push(Data.getTemplate("heading", {}));
        if (isLoading) {
            renderField.push(Data.getTemplate("loading", {}));
            this.setState({renderField: renderField});
            return;
        }
        var isLogin = Data.getData("isLogin", false);
        if (isLogin) {
            FTPHelper.setLinkTemplate(Data);
            renderField.push(Data.getData("linkTemplate", {}));
            renderField.push(FTPHelper.getFieldTemplateByPageName(Data, currentPageName));
        } else {
            renderField.push(FTPHelper.getFieldTemplateByPageName(Data, currentPageName));
        }
        this.setState({renderField: renderField});
    }
    componentDidMount() {
        checkAndroid();
        var redirectStatus = FTPHelper.checkForRedirect(Data);
        if (redirectStatus) {
            return;
        }
        // var renderField = [];
        // renderField.push(Data.getTemplate("loading", {}));
        // this.setState({renderField: renderField});
        var self = this;
        self.setRenderField(true);
        /**
        if (currentPageName === "upload_file") {
            FTPHelper.loadStaticData(Data, function() {
                self.setRenderField();
            });
        }
        */
        FTPHelper.loadPageData(Data, function() {
            self.setRenderField();
        });
    }
    render() {
        var renderFieldRow = this.state.renderField;
        return(
            <RenderComponent renderFieldRow={renderFieldRow}
                onFormSubmit={this.onFormSubmit}
                onClick={this.onClick}
                onChange={this.onChange}
                dropDownChange={this.dropDownChange}
            />
        );
    }
}

export default App;
