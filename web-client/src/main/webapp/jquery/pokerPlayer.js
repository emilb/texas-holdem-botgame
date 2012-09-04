var pokerPlayer = function (name) {
    "use strict";


    var responseClass = "se.cygni.texasholdem.communication.message.response.ActionResponse";

    var playerState = {
        isPlaying : false,
        isTableDone : false,
        newMessages : [],
        amount : 0
    };

    function addViewMessage(newMessage) {
        playerState.newMessages.push(newMessage);
    }

    var player = {
         name : name,


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

        actionRequestHandler : function (actionRequest) {
            var chosenAction1, chosenAction2, chosenAction3;
            var i;
            for (i = 0; i < actionRequest.possibleActions.length; i += 1) {
                var action = actionRequest.possibleActions[i];
                switch (action.actionType) {
                    case "RAISE":
                        chosenAction1 = action;
                        break;
                    case "CALL":
                        chosenAction2 = action;
                        break;
                    case "CHECK":
                        chosenAction3 = action;
                        break;
                    case "FOLD":
                        chosenAction3 = action;
                        break;
                    case "ALL_IN":
                        //chosenAction3 = action;
                        break;
                    default:
                        break;
                }
            }

            var chosenAction = chosenAction1 || chosenAction2 || chosenAction3;
            // TODO: move type and requestId to poker.js
            return {
                "type":responseClass,
                "requestId":actionRequest.requestId,
                "action":chosenAction
            };
        },

        // public state for view
        state : playerState,

        // Event handlers - should not return a value, only collect information about the game
        eventHandlers : {

            onRegisterForPlayResponse : function (playResponse) {
                playerState.isPlaying = true;
                addViewMessage('Registered for play');
            },

            onCommunityHasBeenDealtACardEvent : function (event) {
            },
            onPlayerBetBigBlindEvent : function (event) {
            },
            onPlayerBetSmallBlindEvent : function (event) {
            },
            onPlayerCalledEvent : function (event) {
            },
            onPlayerCheckedEvent : function (event) {
            },
            onPlayerFoldedEvent : function (event) {
            },
            onPlayerQuitEvent : function (event) {
            },
            onPlayerRaisedEvent : function (event) {
            },
            onPlayerWentAllInEvent : function (event) {
            },
            onPlayIsStartedEvent : function (event) {
            },
            onServerIsShuttingDownEvent : function (event) {
            },
            onShowDownEvent : function (event) {
            },
            onTableChangedStateEvent : function (event) {
            },
            onTableIsDoneEvent : function (event) {
                playerState.isTableDone = true;
                addViewMessage('Table is done');
            },
            onYouHaveBeenDealtACardEvent : function (event) {
            },
            onYouWonAmountEvent : function (event) {
                playerState.amount = parseInt(event.yourChipAmount);
                if (parseInt(event.wonAmount) > 0) {
                    addViewMessage('You won '+event.wonAmount);
                }
            }
        }

    };

    return player;
};

