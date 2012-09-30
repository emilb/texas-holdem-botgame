var assert = require('assert');

var myPlayer = require('./botplayer.js').botplayer;


// Unittests of botplayer

var possibleActions = [
    {"actionType":"FOLD", "amount":0},
    {"actionType":"CALL", "amount":50},
    {"actionType":"RAISE", "amount":25},
    {"actionType":"ALL_IN", "amount":1000}
];

var response = myPlayer.onActionRequest(possibleActions);
assert.equal(response.actionType, "CALL", 'response is CALL');
assert.equal(response.amount, 50, 'response amount is 50');

var possibleActions = [
    {"actionType":"FOLD", "amount":0},
    {"actionType":"CHECK", "amount":25},
    {"actionType":"ALL_IN", "amount":1000}
];

var response = myPlayer.onActionRequest(possibleActions);
assert.equal(response.actionType, "CHECK", 'response is CHECK');
assert.equal(response.amount, 25, 'response amount is 25');

// TODO: 
// Test result of events that change player state