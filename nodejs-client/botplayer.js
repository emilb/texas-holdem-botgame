var playerClient = require('./modules/playerclient.js');
require('./modules/sugar-1.3.min.js');
var playerStateUpdaterModule = require('./modules/playerStateUpdater.js');

var playerState = {
    isPlaying:false,
    isTableDone:false,
    amount:0,
    myCards:[],
    communityCards:[],
    potTotal:0,
    winner:null,
    table:{
        state:'',
        players:[],
        smallBlindAmount:0,
        bigBlindAmount:0,
        dealer:null,
        smallBlindPlayer:null,
        bigBlindPlayer:null
    }
};

var stateUpdater = playerStateUpdaterModule.playerStateUpdater(playerState);
    
var player = {

    getName : function() {
        throw new Error('Did you forget to specify your name? A good idea is to use your e-mail as username!');
    	//return 'MyBot';
    },

    state : playerState,  // now state can be accessed with player.state

    isWinner:function () {
        return playerState.winner && playerState.winner.name === this.getName();
    },


    onRegisterForPlayResponse : function (playResponse) {
    },

    onPlayIsStartedEvent : function (event) {
        console.log("I got a PlayIsStartedEvent!");
        console.log("I got chips: "+playerState.amount);        
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
    	console.log("I'am the winner: "+this.isWinner());
    },

    onYouHaveBeenDealtACardEvent : function (event) {
    },

    onYouWonAmountEvent : function (event) {
    },

    onActionRequest : function (possibleActions) {

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
        console.log('I chose action: ' + chosenAction.actionType);
        return chosenAction;
    },
    
    dispatchEvent : function(event) {
    	var clazz = event.type.split('.').pop();
    	stateUpdater.eventHandlers['on' + clazz](event); // update currentPlayState
    	this['on' + clazz](event);
    }
}

playerClient.connect('localhost', 4711, player);
playerClient.playAGame(playerClient.ROOM_TRAINING());
