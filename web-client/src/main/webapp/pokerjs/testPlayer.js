var responseClass = "se.cygni.texasholdem.communication.message.response.ActionResponse";


var player = pokerPlayer('qunit');

module('General');
test('name', function() {  
    equal( player.name, 'qunit', 'name is ok');  
});

module('Action');
test('actionRequestHandler function exists', function() {
	expect(2);
    ok(player.actionRequestHandler, 'actionRequestHandler function should exist');  
    equal(typeof player.actionRequestHandler, 'function', 'actionRequestHandler should be a function');  
});

// TODO: test that player chooses one of the given possibleActions; type amd amount.
// For actionType RAISE the max amount is the given one and min is 1 ?
test('ActionResponse raise', function() {  
	var possibleActions = [
	     {"actionType":"FOLD","amount":0},
	     {"actionType":"CALL","amount":50},
	     {"actionType":"RAISE","amount":25},
	     {"actionType":"ALL_IN","amount":1000}
     ];

	var expected = {"actionType":"RAISE","amount":25};
    deepEqual(player.actionRequestHandler(possibleActions), expected, 'response is RAISE 25');  
});

module('EventCallbacks');

var possibleEvents = [
    'onRegisterForPlayResponse',
    'onCommunityHasBeenDealtACardEvent',
    'onPlayerBetBigBlindEvent',
    'onPlayerBetSmallBlindEvent',
    'onPlayerCalledEvent',
    'onPlayerCheckedEvent',
    'onPlayerFoldedEvent',
    'onPlayerQuitEvent',
    'onPlayerRaisedEvent',
    'onPlayerWentAllInEvent',
    'onPlayIsStartedEvent',
    'onServerIsShuttingDownEvent',
    'onShowDownEvent',
    'onTableChangedStateEvent',
    'onTableIsDoneEvent',
    'onYouHaveBeenDealtACardEvent',
    'onYouWonAmountEvent'
];

// Test that required event handler functions exists
for (var i = 0; i < possibleEvents.length; i++) {
	var eventHandlerName = possibleEvents[i];
	test('eventHandlers exists: '+eventHandlerName, function() {  
	    ok(player.eventHandlers[eventHandlerName], 'eventHandler '+eventHandlerName+' should exist');
	    equal(typeof player.eventHandlers[eventHandlerName], 'function', 'eventHandler '+eventHandlerName+' should be a function');  
	});
};

player = pokerPlayer('qunit');

test('playerState correct before round', function() {
    equal(player.state.amount, 0, 'amount should be a 0 before game');  
    equal(player.state.isTableDone, false, 'isTableDone should be false before game');  
});

// Test events by calling player event handlers with real game data
// and verify that player can receive them without throwing exception
test('can execute event from snapshot game round', function() {  
	for (var i = 0; i < gameRoundEvents.length; i++) {
		var json = gameRoundEvents[i];
		var clazz = json.type.split('.').pop();
        var actionRequest = (clazz === 'ActionRequest');
        if (actionRequest) {
            var actionResponse = player.actionRequestHandler(json.possibleActions);
            ok(actionResponse, 'actionResponse should exist');
		} else {
            var eventName = 'on' + clazz;
	        var handler = player.eventHandlers[eventName];
			handler(json);
		}
		ok(true, 'eventHandler should run ok');
	}
});

test('playerState correct after round', function() {  
    equal(player.state.amount, 0, 'amount should be a 0 when losing game');  
    equal(player.state.isTableDone, true, 'isTableDone should be a true when losing game');  
});


