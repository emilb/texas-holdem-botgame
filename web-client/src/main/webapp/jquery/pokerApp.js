$(function () {
    "use strict";


    var header = $('#header');
    var content = $('#content');
    var input = $('#input');
    var status = $('#status');


    var spec = {
        url: 'http://localhost:8080/poker',
        actionResponseCallback : function(actionResponse) {
            addMessage(actionResponse.type, actionResponse.action.actionType, 'green', new Date());
        },
        onConnectionOpen : function(response) {
            content.html($('<p>', { text: 'Poker connected using ' + response.transport }));
        },
        onTransportFailure : function(errorMsg, request) {
            header.html($('<h3>', { text: 'Transport failure:'+errorMsg+' Default transport is WebSocket, fallback is ' + request.fallbackTransport }));
        },
        onError : function(response) {
            content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
                + 'socket or the server is down' }));
        },
        onMessage : function(clazz) {
            addMessage(clazz, "", 'blue', new Date());
        }
    };

    function addMessage(author, message, color, datetime) {
        content.append('<p><span style="color:' + color + '">' + author + '</span> @ ' +
            + datetime.getMinutes() + ':' + datetime.getSeconds()
            + ': ' + message + '</p>');
    }



    var player = null;
    var client = pokerClient(spec); // från poker.js
    
    $('#button').click(function(){
        // starta
        //alert('startaknapp ');
        var name='Spelare';
        player = pokerPlayer(name);              
        client.register(player);
    });


});

