import $S from "../../interface/stack.js";
import TemplateHelper from "../../common/TemplateHelper";
import Config from "./Config";

var TemplateHandler

(function($S){
TemplateHandler = function(arg) {
    return new TemplateHandler.fn.init(arg);
};

TemplateHandler.fn = TemplateHandler.prototype = {
    constructor: TemplateHandler,
    init: function(arg) {
        this.arg = arg;
        return this;
    }
};
$S.extendObject(TemplateHandler);

TemplateHandler.extend({
    checkUserDependentFooterLink: function(template) {
        var isUsersControlEnable = Config.getUserData("isUsersControlEnable", "false");
        if (isUsersControlEnable === "true") {
            TemplateHelper.removeClassTemplate(template, "footerLink.users_control", "d-none");
        }
        return template;
    }
});

})($S);

export default TemplateHandler;
