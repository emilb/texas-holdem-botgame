var myPlayer = pokerPlayer('myRaiserPlayer');


module('MyPlayer Action');

test('ActionResponse is raise', function () {
    var possibleActions = [
        {"actionType":"FOLD", "amount":0},
        {"actionType":"CALL", "amount":50},
        {"actionType":"RAISE", "amount":25},
        {"actionType":"ALL_IN", "amount":1000}
    ];

    var response = myPlayer.actionRequestHandler(possibleActions);
    equal(response.actionType, "RAISE", 'response is RAISE');
    equal(response.amount, 25, 'response amount is 25');
});

test('ActionResponse is call', function () {
    var possibleActions = [
        {"actionType":"FOLD", "amount":0},
        {"actionType":"CALL", "amount":50},
        {"actionType":"ALL_IN", "amount":1000}
    ];

    var response = myPlayer.actionRequestHandler(possibleActions);
    equal(response.actionType, "CALL", 'response is CALL');
    equal(response.amount, 50, 'response amount is 50');
});

// TODO: 
// Test result of events that change player state