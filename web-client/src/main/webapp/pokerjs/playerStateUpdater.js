var playerStateUpdater = function (thePlayerState) {
    "use strict";


    var playerState = thePlayerState;


    function updatePlayerState(eventJson, eventName) {
        var updatehandler = eventHandlers[eventName];
        if (updatehandler) {
            updatehandler(eventJson);
        }
    }

    var updater = {
    // handlers for updating basic player state from Events and RegisterForPlayResponse
    // Event handlers - should not return a value, only collect information about the game
        eventHandlers : {

            onRegisterForPlayResponse : function (playResponse) {
                playerState.isPlaying = true;
                playerState.isTableDone = false;
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
                playerState.table.players = event.players;
                playerState.table.smallBlindAmount = event.smallBlindAmount;
                playerState.table.bigBlindAmount   = event.bigBlindAmount;
                playerState.table.dealer = event.dealer;
                playerState.table.smallBlindPlayer = event.smallBlindPlayer;
                playerState.table.bigBlindPlayer = event.bigBlindPlayer;
            },
            onServerIsShuttingDownEvent : function (event) {
            },
            onShowDownEvent : function (event) {
            },
            onTableChangedStateEvent : function (event) {
                playerState.table.state = event.state;
            },
            onTableIsDoneEvent : function (event) {
                playerState.isTableDone = true;
            },
            onYouHaveBeenDealtACardEvent : function (event) {
            },
            onYouWonAmountEvent : function (event) {
                playerState.amount = parseInt(event.yourChipAmount);
            }
        }
    };


    return updater;
};
