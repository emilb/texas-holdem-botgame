$(function () {
    "use strict";


    var header = $('#header');
    var content = $('#content');
    var input = $('#input');
    var status = $('#status');
    var messagelog = $('#messagelog');
    var leader = $('#leader');
    var state = $('#state');

    var enableMessagelog = false; // TODO: checkbox value instead
    var newMessages = [];

    var playerStateUpdateEventHandlers;

    var player = null;
    var client;

    var spec = {
        url:'http://localhost:8080/poker',

        actionResponseCallback:function (actionResponse) {
            addMessage(actionResponse.type, actionResponse.action.actionType, 'green', new Date());
        },
        onConnectionOpen:function (response) {
            content.html($('<p>', { text:'Poker connected using ' + response.transport }));
            input.removeAttr('disabled').focus();
            status.text('Spelarnamn:');
        },
        onTransportFailure:function (errorMsg, request) {
            header.html($('<h3>', { text:'Transport failure:' + errorMsg + ' Default transport is WebSocket, fallback is ' + request.fallbackTransport }));
        },
        onError:function (response) {
            content.html($('<p>', { text:'Sorry, but there\'s some problem with your '
                + 'socket or the server is down' }));
        },
        onMessage:function (clazz) {
            addMessage(clazz, "", 'blue', new Date());
        },
        onUpdatePlayerState:function (eventJson, eventName) {
            updatePlayerState(eventJson, eventName);
        },
        onPlayerState:function (playerState) {
            var msg = newMessages.pop();
            if (msg) {
                content.html($('<p>', { text : msg }));
            }
        }
    };

    function updatePlayerState (eventJson, eventName) {
        var updatehandler = playerStateUpdateEventHandlers[eventName];
        if (updatehandler) {
            updatehandler(eventJson);
        }
    }

    // handlers for updating view from player state and Events
    playerStateUpdateEventHandlers = {

        onRegisterForPlayResponse : function (playResponse) {
            addViewMessage('Registered for play');
        },
        onPlayIsStartedEvent : function (event) {
            //addViewMessage('new round: '+player.state.table.players.length+' players');
        },
        onServerIsShuttingDownEvent : function (event) {
        },
        onShowDownEvent : function (event) {
            var leaderPlayer = player.state.table.players.sortBy('chipCount').last();
            if (leaderPlayer) {
                leader.text('leader: '+leaderPlayer.name+' : '+leaderPlayer.chipCount);
            }
        },
        onTableChangedStateEvent : function (event) {
        },
        onTableIsDoneEvent : function (event) {
            if (player.isWinner()) {
                addViewMessage('Table is done - You won');
            } else {
                addViewMessage('Table is done - '+player.state.winner.name+' won');
            }
        },
        onYouWonAmountEvent : function (event) {
            state.text('Your chipCount: '+player.state.amount);
            if (event.wonAmount > 0) {
                addViewMessage('You won '+event.wonAmount);
            }
        }
    };

    function addViewMessage(newMessage) {
        newMessages.push(newMessage);
    }


    function addMessage(author, message, color, datetime) {
        if (enableMessagelog) {
            messagelog.append('<p><span style="color:' + color + '">' + author + '</span> @ ' +
                +datetime.getMinutes() + ':' + datetime.getSeconds()
                + ': ' + message + '</p>');
                
        }
    }


    client = pokerClient(spec); // från pokerClient.js

    $('#button').click(function () {
        // starta
        var name = input.val();
        var room = $('#room').val();
        player = pokerPlayer(name); // från pokerPlayer.js
        client.register(player, room);
        $('#button').attr('disabled', 'disabled');
    });


});

