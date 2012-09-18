var net = require('net');

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
    client.on('data', function (data) {

        var stringData = data.toString();
        var arrayData = stringData.split([jsonDelimiter]);
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
        player['on' + clazz](event);
    }

}

/**
 * From here: http://www.broofa.com/2008/09/javascript-uuid-function/
 */
(function() {
    // Private array of chars to use
    var CHARS = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');

    Math.uuid = function (len, radix) {
        var chars = CHARS, uuid = [], i;
        radix = radix || chars.length;

        if (len) {
            // Compact form
            for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
        } else {
            // rfc4122, version 4 form
            var r;

            // rfc4122 requires these characters
            uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
            uuid[14] = '4';

            // Fill in random data.  At i==19 set the high bits of clock sequence as
            // per rfc4122, sec. 4.1.5
            for (i = 0; i < 36; i++) {
                if (!uuid[i]) {
                    r = 0 | Math.random()*16;
                    uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                }
            }
        }

        return uuid.join('');
    };

    // A more performant, but slightly bulkier, RFC4122v4 solution.  We boost performance
    // by minimizing calls to random()
    Math.uuidFast = function() {
        var chars = CHARS, uuid = new Array(36), rnd=0, r;
        for (var i = 0; i < 36; i++) {
            if (i==8 || i==13 ||  i==18 || i==23) {
                uuid[i] = '-';
            } else if (i==14) {
                uuid[i] = '4';
            } else {
                if (rnd <= 0x02) rnd = 0x2000000 + (Math.random()*0x1000000)|0;
                r = rnd & 0xf;
                rnd = rnd >> 4;
                uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
            }
        }
        return uuid.join('');
    };

    // A more compact, but less performant, RFC4122v4 solution:
    Math.uuidCompact = function() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    };
})();