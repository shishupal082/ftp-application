import $$$ from '../../interface/global';
import $S from "../../interface/stack.js";
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
    send: function(eventCategory) {
        var trackingAction = this.trackingAction;
        if (!$S.isString(trackingAction)) {
            trackingAction = "not-string";
        } else if (trackingAction.length === 0) {
            trackingAction = "empty-string";
        }
        if (!$S.isString(eventCategory)) {
            eventCategory = "not-string";
        } else if (eventCategory.length === 0) {
            eventCategory = "empty-string";
        }
        var eventLabel = PageData.getUserAgentTrackingData();
        if (!$S.isString(eventLabel)) {
            eventLabel = "not-string";
        } else if (eventLabel.length === 0) {
            eventLabel = "empty-string";
        }
        if (GaTrackingEnable && Gtag !== null) {
            Gtag('event', trackingAction, {
              'event_category' : eventCategory,
              'event_label' : eventLabel
            });
        }
    }
};
$S.extendObject(GATracking);

GATracking.extend({
    trackResponse: function(event, response) {
        if (!$S.isObject(response)) {
            return;
        }
        var username = PageData.getData(event+".username", "empty-username");
        var action = event + "_" + response.status;
        GATracking(action).send(username);
    },
    trackResponseAfterLogin: function(event, response) {
        if (!$S.isObject(response)) {
            return;
        }
        var username = PageData.getData("ui.username", "empty-username");
        var action = event + "_" + response.status;
        GATracking(action).send(username);
    }
});


})($S);

export default GATracking;
