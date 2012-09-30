var playerClient = require('./modules/playerclient.js');

var player = require('./botplayer.js').botplayer;
//player.setName('MyPlayerName');


playerClient.connect('localhost', 4711, player);
playerClient.playAGame(playerClient.ROOM_TRAINING());
