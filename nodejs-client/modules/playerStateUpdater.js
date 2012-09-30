var playerStateUpdater = function (thePlayerState) {
    "use strict";


    var playerState = thePlayerState;

    function createPlayersForTable(players) {
        return players.map(function (p) {
            return {
                name:p.name,
                chipCount:p.chipCount,
                potInvestment:0,
                folded:false,
                allIn:false
            };
        });
    }

    ;

    function addPotInvestmentToPlayer(name, amount) {
        playerState.potTotal = playerState.potTotal + amount;

        var p = getTablePlayer(name);
        if (p === null) {
            alert('no player by name ' + name);
            return;
        }
        p.potInvestment = p.potInvestment + amount;
    }

    ;

    function getTablePlayer(name) {
        return playerState.table.players.find(function (p) {
            return p.name === name;
        });
    }

    ;


    var updater = {

        getTablePlayer:getTablePlayer,

        hasPlayerFolded:function (name) {
            return playerState.table.players.count(function (p) {
                return p.name === name && p.folded;
            }) > 0;
        },
        hasPlayerGoneAllIn:function (name) {
            return playerState.table.players.count(function (p) {
                return p.name === name && p.allIn;
            }) > 0;
        },
        getInvestmentInPotFor:function (name) {
            var p = getTablePlayer(name);
            if (p) {
                return p.potInvestment;
            }
            return 0;
        },

        // handlers for updating basic player state from Events and RegisterForPlayResponse
        // Event handlers - should not return a value, only collect information about the game
        eventHandlers:{

            onRegisterForPlayResponse:function (playResponse) {
            },

            onCommunityHasBeenDealtACardEvent:function (event) {
                playerState.communityCards.push(event.card);
            },
            onPlayerBetBigBlindEvent:function (event) {
                addPotInvestmentToPlayer(event.player.name, event.bigBlind);
            },
            onPlayerBetSmallBlindEvent:function (event) {
                addPotInvestmentToPlayer(event.player.name, event.smallBlind);
            },
            onPlayerCalledEvent:function (event) {
                addPotInvestmentToPlayer(event.player.name, event.callBet);
                if (event.player.chipCount === 0) {
                    getTablePlayer(event.player.name).allIn = true;
                }
            },
            onPlayerCheckedEvent:function (event) {
            },
            onPlayerFoldedEvent:function (event) {
            },
            onPlayerQuitEvent:function (event) {
            },
            onPlayerRaisedEvent:function (event) {
                addPotInvestmentToPlayer(event.player.name, event.raiseBet);
                if (event.player.chipCount === 0) {
                    getTablePlayer(event.player.name).allIn = true;
                }
            },
            onPlayerWentAllInEvent:function (event) {
                addPotInvestmentToPlayer(event.player.name, event.allInAmount);
                getTablePlayer(event.player.name).allIn = true;
            },
            onPlayIsStartedEvent:function (event) {
                playerState.isPlaying = true;
                playerState.isTableDone = false;
                playerState.myCards = [];
                playerState.communityCards = [];
                playerState.potTotal = 0;
                playerState.winner = null;

                playerState.table.state = '';
                playerState.table.players = createPlayersForTable(event.players);
                playerState.table.smallBlindAmount = event.smallBlindAmount;
                playerState.table.bigBlindAmount = event.bigBlindAmount;
                playerState.table.dealer = event.dealer;
                playerState.table.smallBlindPlayer = event.smallBlindPlayer;
                playerState.table.bigBlindPlayer = event.bigBlindPlayer;
            },
            onServerIsShuttingDownEvent:function (event) {
            },
            onShowDownEvent:function (event) {
                event.playersShowDown.map('player').forEach(function (p) {
                    getTablePlayer(p.name).chipCount = p.chipCount;
                });
            },
            onTableChangedStateEvent:function (event) {
                playerState.table.state = event.state;
            },
            onTableIsDoneEvent:function (event) {
                playerState.isTableDone = true;
                playerState.winner = event.players.sortBy('chipCount').last();
            },
            onYouHaveBeenDealtACardEvent:function (event) {
                playerState.myCards.push(event.card);
            },
            onYouWonAmountEvent:function (event) {
                playerState.amount = event.yourChipAmount;
            }
        }
    };


    return updater;
};

exports.playerStateUpdater = playerStateUpdater;
