require('./modules/sugar-1.3.min.js');
var stateUpdater = require('./modules/playerStateUpdater.js').playerStateUpdater();

var playerState = stateUpdater.playerState; // private object ref. that can't be changed via player
var playerName = null;

var player = {
	
    getName : function() {
    	if (playerName) {
    		return playerName;
    	}
        throw new Error('Did you forget to specify your name? A good idea is to use your e-mail as username!');
    },
    setName : function(name) {
    	playerName = name;
    },
    state : playerState,  // property "state" can be accessed with player.state

    isWinner : function () {
        return playerState.winner && playerState.winner.name === this.getName();
    },
    amIDealerPlayer : function() {
    	return playerState.table.dealer === this.getName();
    },
    amISmallBlindPlayer : function() {
    	return playerState.table.smallBlindPlayer === this.getName();
    },
    amIBigBlindPlayer : function() {
    	return playerState.table.bigBlindPlayer === this.getName();
    },
    haveIFolded : function () {
        return stateUpdater.hasPlayerFolded(this.getName());
    },
    haveIGoneAllIn : function () {
        return stateUpdater.hasPlayerGoneAllIn(this.getName());
    },
    getMyInvestmentInPot : function () {
        return stateUpdater.getInvestmentInPotFor(this.getName());
    },


    // Event handlers

    onRegisterForPlayResponse : function (playResponse) {
    },

    onPlayIsStartedEvent : function (event) {
        console.log("I got a PlayIsStartedEvent, tableId:"+playerState.tableId);
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
exports.botplayer = player;


