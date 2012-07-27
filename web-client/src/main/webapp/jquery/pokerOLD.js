$(function () {
    "use strict";


/*


RegisterForPlayResponse:

{"type":"se.cygni.texasholdem.communication.message.response.RegisterForPlayResponse",
"requestId":"153c6263-f4d3-436b-9097-8505217c300a","sessionId":"000e449e-614e-426b-bc13-e80830674c34"}

*/


    var header = $('#header');
    var content = $('#content');
    var input = $('#input');
    var status = $('#status');
    var myName = false;
    var author = null;
    var actionRequest = false;
    var socket = $.atmosphere;

    var player = null;

    var validateJson = function (json) {
        if (!json.type) {
            console.log("JSON must contain type: \n"+jQuery.stringifyJSON(json));
            alert("JSON must contain type: \n"+jQuery.stringifyJSON(json));
            return false;
        }

        var isRequest = json.type.split('.').reverse()[1] === 'request';

        if (isRequest && !json.requestId) {
            console.log("JSON must contain requestId: \n"+jQuery.stringifyJSON(json));
            alert("JSON must contain requestId: \n"+jQuery.stringifyJSON(json));
            return false;
        }

        return true;
    };

    // Below is code that can be re-used -->

    // We are now ready to cut the request
    // TODO: parse document.location.toString() and replace poker.html with /poker
    var request = { url: 'http://localhost:8080/poker',
        contentType : "application/json",
        logLevel : 'debug',
        headers :  { "connectionId" : ""+new Date().getTime() }, // skall vara unikt för varje spelare/connection
        transport : 'websocket',
        fallbackTransport: 'long-polling'};


    request.onOpen = function(response) {
        content.html($('<p>', { text: 'Poker connected using ' + response.transport }));
        input.removeAttr('disabled').focus();
        status.text('Choose name:');
    };

    // For demonstration of how you can customize the fallbackTransport based on the browser -->
    request.onTransportFailure = function(errorMsg, request) {
    	//alert('onTransportFailure: '+errorMsg);
        jQuery.atmosphere.info(errorMsg);
        if ( window.EventSource ) {
            request.fallbackTransport = "sse";
        }
        header.html($('<h3>', { text: 'Poker. Default transport is WebSocket, fallback is ' + request.fallbackTransport }));
    };

    request.onReconnect = function (request, response) {
        socket.info("Reconnecting");
    };

    request.onMessage = function (response) {
        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }
        try {
            if (validateJson(json) && player) {


                var clazz = json.type.split('.').pop();
                addMessage(clazz, "", 'blue', new Date());
                // TODO: gör om så att ActionRequest/respons blir egen
                var handler = player.eventHandlers['on'+clazz];
                if (!handler) {
                    console.log('Player eventHandler for '+clazz+' missing.');
                } else {
                    var actionResponse = handler(json);
                    if (actionResponse) {
                        addMessage(actionResponse.type, actionResponse.action.actionType, 'green', new Date());
                        console.log('### handler response for '+clazz+' is: '+jQuery.stringifyJSON(actionResponse));
                        subSocket.push(jQuery.stringifyJSON(actionResponse));
                     }
                }

                actionRequest = (clazz === 'ActionRequest');

                if (actionRequest) {
                    //actionRequest = true;
                    status.text('actionRequest').css('color', 'blue');
                } 

            } else {

            }    
        } catch (e) {
            console.log('error: '+e.toString());
            return;
        }

    };

    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    var subSocket = socket.subscribe(request);
    
    $('#button').click(function(){
        // starta
        //alert('startaknapp ');
        var name = input.val()
        var stringifyJSON = jQuery.stringifyJSON({
            "name":name,
            "room":"TRAINING",
            "type":"se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest"});
        player = pokerPlayer(author);
        subSocket.push(stringifyJSON);
    });


    function addMessage(author, message, color, datetime) {
        content.append('<p><span style="color:' + color + '">' + author + '</span> @ ' +
            + datetime.getMinutes() + ':' + datetime.getSeconds()
            + ': ' + message + '</p>');
    }
});

