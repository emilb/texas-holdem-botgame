var net = require('net');
require('./uuid.js'); // Adds Math.uuid()

var jsonDelimiter = '_-^emil^-_';
var client = new net.Socket();
client.setNoDelay(true);
client.setKeepAlive(true, 0);

var player;

/**
 * Connects to the poker server.
 *
 * @param host
 * @param port
 * @param botPlayer - The player implementation
 */
function connect(host, port, botPlayer) {

	var saveForNext = null;
	
    player = botPlayer;

    client.connect(port, host, function () {
        console.log('CONNECTED TO: ' + host + ':' + port);
    });

    // Add a 'data' event handler for the client socket
    // data is what the server sent to this socket
    client.on('data', function (data) {

        var stringData = data.toString();
        if (saveForNext) {
        	stringData = saveForNext+stringData;
        	saveForNext = null;
        }
        var isComplete = endsWith(stringData, jsonDelimiter);
        var jsonArr = stringData.split(jsonDelimiter);
        var jsonStr;
        
        while(jsonArr.length > 0) {
            jsonStr = jsonArr.shift();
	        if (jsonArr.length === 0 && !isComplete) {
		    	saveForNext = jsonStr; // if last in array is incomplete then save it and exit
		    	break;
	        }
	        if (jsonStr) {
	            routeEvent(JSON.parse(jsonStr));
	        }
        }
    });

    // Add a 'close' event handler for the client socket
    client.on('close', function () {
        console.log('Connection closed');
    });

    client.on('error', function () {
        console.log('Socket Error');
    })
};
exports.connect = connect;

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
};

/**
 * Registers the player for a new game in the supplied room.
 *
 * @param room
 */
function playAGame(room) {
    client.write(JSON.stringify(createRegisterForPlayRequest(room)) + jsonDelimiter);
}
exports.playAGame = playAGame;

function ROOM_TRAINING() {
    return 'TRAINING';
}
exports.ROOM_TRAINING = ROOM_TRAINING;

function ROOM_TOURNAMENT() {
    return 'TRAINING';
}
exports.ROOM_TOURNAMENT = ROOM_TOURNAMENT;

function ROOM_FREEPLAY() {
    return 'FREEPLAY';
}
exports.ROOM_FREEPLAY = ROOM_FREEPLAY;

/**
 * Creates a RegisterForPlay request
 *
 * @param room
 * @return {Object}
 */
function createRegisterForPlayRequest(room) {

    if (!player.getName) {
        throw new Error('No method getName found');
    }

    var request = {
        type: 'se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest',
        sessionId : '',
        requestId: Math.uuid(),
        name: player.getName(),
        room: room
    }

    return request;
}

/**
 * Routes the event to the supplied player
 *
 * @param event
 */
function routeEvent(event) {
    var type = event.type;
    var parts = type.split('.');
    var clazz = parts.pop();
    var pkg = parts.pop(); // last part of Java package; events,request,exception

    //console.log('Routing event of type: ' + clazz);


    if (clazz === 'ActionRequest') {
        var chosenAction = player.onActionRequest(event.possibleActions);
        var actionResponse = {
            type : 'se.cygni.texasholdem.communication.message.response.ActionResponse',
            requestId : event.requestId,
            action : chosenAction
        };

        client.write(JSON.stringify(actionResponse) + jsonDelimiter);
    } else {
    	if (pkg === 'event') {
	        if (!player['on' + clazz]) {
	            throw new Error('Could not find player method on' + clazz);
	        }
            player.dispatchEvent(event);
    	}
    	if (pkg === 'exception') {
            throw new Error('Got Exception from server: '+clazz+', ' + event.message);
    	}
    }
}

