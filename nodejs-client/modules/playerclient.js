var net = require('net');
require('./uuid.js'); // Adds Math.uuid()

var jsonDelimiter = '_-^emil^-_';
var client = new net.Socket();
var player;

/**
 * Connects to the poker server.
 *
 * @param host
 * @param port
 * @param botPlayer - The player implementation
 */
function connect(host, port, botPlayer) {

    player = botPlayer;

    client.connect(port, host, function () {
        console.log('CONNECTED TO: ' + host + ':' + port);
    });

    // Add a 'data' event handler for the client socket
    // data is what the server sent to this socket
    // TODO: don't assume last element is "complete" (delimiter is last)
    client.on('data', function (data) {

        var stringData = data.toString();
        var arrayData = stringData.split(jsonDelimiter);
        for (var i = 0; i < arrayData.length; i++) {
            if (arrayData[i] != '') {
                var objData = JSON.parse(arrayData[i]);
                routeEvent(objData)
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
}
exports.connect = connect;

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
    var clazz = type.split('.').pop();

    console.log('Routing event of type: ' + clazz);

    if (!player['on' + clazz]) {
        throw new Error('Could not find method on' + clazz);
    }

    if (clazz === 'ActionRequest') {
        var chosenAction = player.onActionRequest(event.possibleActions);
        var actionResponse = {
            type : 'se.cygni.texasholdem.communication.message.response.ActionResponse',
            requestId : event.requestId,
            action : chosenAction
        };

        client.write(JSON.stringify(actionResponse) + jsonDelimiter);
    } else {
        //player['on' + clazz](event);
        player.dispatchEvent(event);
    }

}

