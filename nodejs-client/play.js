var playerClient = require('./modules/playerclient.js');
var player = require('./botplayer.js').botplayer;

// Cygni poker server:
//playerClient.connect('poker.cygni.se', 4711, player);
playerClient.connect('localhost', 4711, player);
playerClient.playAGame(playerClient.ROOM_TRAINING());
