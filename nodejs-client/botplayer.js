var playerClient = require('./modules/playerclient.js');

var player = {

    getName : function() {
        throw new Error('Did you forget to specify your name?');
    },

    onRegisterForPlayResponse : function (playResponse) {
    },

    onPlayIsStartedEvent : function (event) {
        console.log("I got a PlayIsStartedEvent!");
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

    onServerIsShuttingDownEvent : function (event) {
    },

    onShowDownEvent : function (event) {
    },

    onTableChangedStateEvent : function (event) {
    },

    onTableIsDoneEvent : function (event) {
    },

    onYouHaveBeenDealtACardEvent : function (event) {
    },

    onYouWonAmountEvent : function (event) {
    },

    onActionRequest : function (possibleActions) {

        console.log('I got an action request!')

        var raiseAction, callAction, checkAction, foldAction, allInAction;
        var i, action;
        for (i = 0; i < possibleActions.length; i += 1) {
            action = possibleActions[i];
            switch (action.actionType) {
                case "RAISE" : 
                    raiseAction = action;
                    break;
                case "CALL" : 
                    callAction = action;
                    break;
                case "CHECK" : 
                    checkAction = action;
                    break;
                case "FOLD" : 
                    foldAction = action;
                    break;
                case "ALL_IN" :
                    allInAction = action;
                    break;
                default : 
                    break;
            }
        }

        var chosenAction = checkAction || callAction || raiseAction || foldAction || allInAction;
        return chosenAction;
    }
}

playerClient.connect('localhost', 4711, player);
playerClient.playAGame(playerClient.ROOM_TRAINING());