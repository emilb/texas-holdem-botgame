var pokerPlayer = function (name) {
    "use strict";


    var player = { "name":name };

    var responseClass = "se.cygni.texasholdem.communication.message.response.ActionResponse";

    /*
     The main function in the player - to respond with an Action

     {"type":"se.cygni.texasholdem.communication.message.request.ActionRequest",
     "sessionId":null,
     "requestId":"c8586f44-39b8-4aaf-a6ef-572ea64eb7c3",
     "possibleActions":[
     {"actionType":"FOLD","amount":0},
     {"actionType":"CALL","amount":50},
     {"actionType":"RAISE","amount":25},
     {"actionType":"ALL_IN","amount":1000}
     ]}
     */

    player.actionRequestHandler = function (actionRequest) {
        // TODO: at least look in possible actions
        return {
            "type":responseClass,
            "requestId":actionRequest.requestId,
            "action":{"actionType":"RAISE", "amount":25}
        };
    };

    var eventHandlers = {};
    player.eventHandlers = eventHandlers;

    eventHandlers.onActionRequest = player.actionRequestHandler; // For old version of poker.js


    // Event handlers - should not return a value, only collect information about the game
    eventHandlers.onRegisterForPlayResponse = function (playResponse) {
    };

    eventHandlers.onPlayIsStartedEvent = function (event) {
    };
    eventHandlers.onCommunityHasBeenDealtACardEvent = function (event) {
    };
    eventHandlers.onYouHaveBeenDealtACardEvent = function (event) {
    };
    eventHandlers.onShowDownEvent = function (event) {
    };
    eventHandlers.onYouWonAmountEvent = function (event) {
    };
    eventHandlers.onShowDownEvent = function (event) {
    };

    return player;
};

