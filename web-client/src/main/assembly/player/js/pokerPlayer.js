var pokerPlayer = function (name) {
    "use strict";


    var playerState = {
        isPlaying : false,
        isTableDone : false,
        amount : 0,
        table : { 
            state : '',
            players : [],
            smallBlindAmount : 0,
            bigBlindAmount : 0,
            dealer : null, 
            smallBlindPlayer : null,
            bigBlindPlayer : null
        }
    };

    var updater = playerStateUpdater(playerState);

    var player = {
         name : name,


    /*
     The main function in the player - to respond with an actionType
        For example:
     "possibleActions":[
         {"actionType":"FOLD","amount":0},
         {"actionType":"CALL","amount":50},
         {"actionType":"RAISE","amount":25},
         {"actionType":"ALL_IN","amount":1000}
     ]
     */

        actionRequestHandler : function (possibleActions) {
            var chosenAction1, chosenAction2, chosenAction3;
            var i, action;
            for (i = 0; i < possibleActions.length; i += 1) {
                action = possibleActions[i];
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
            return chosenAction;
        },

        // public state for view
        state : playerState,

        eventHandlers : updater.eventHandlers // playerStateUpdater.js
    };

    return player;
};
