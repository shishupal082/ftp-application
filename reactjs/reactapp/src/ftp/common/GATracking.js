import $$$ from '../../interface/global';
import $S from "../../interface/stack.js";
import Config from "./Config";
import PageData from "./PageData";


var GATracking;

(function($S){
var GaTrackingEnable = $$$.gaTrackingEnable;
var Gtag = $$$.gtag;

GATracking = function(trackingAction) {
    return new GATracking.fn.init(trackingAction);
};

GATracking.fn = GATracking.prototype = {
    constructor: GATracking,
    init: function(trackingAction) {
        this.trackingAction = trackingAction;
        return this;
    },
    send: function(eventCategory, eventLabel) {
        if (GaTrackingEnable && Gtag !== null) {
            Gtag('event', this.trackingAction, {
              'event_category' : eventCategory,
              'event_label' : eventLabel
            });
        }
    }
};
$S.extendObject(GATracking);

GATracking.extend({
    trackResponse: function(event, response) {
        var userAgent = PageData.getUserAgentTrackingData();
        if (!$S.isObject(response)) {
            return;
        }
        var eventCategory = event + "_" + response.status;
        GATracking(event).send(eventCategory, userAgent);
    },
    trackUser: function(event) {
        var userAgent = PageData.getUserAgentTrackingData();
        var eventCategory = Config.getPageData("username", "");
        GATracking(event).send(eventCategory, userAgent);
    }
});


})($S);

export default GATracking;
