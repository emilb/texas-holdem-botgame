var responseClass = "se.cygni.texasholdem.communication.message.response.ActionResponse";


var player;

module('General');
test('name', function () {
    player = pokerPlayer('qunit');
    equal(player.name, 'qunit', 'name is ok');
});

module('Action');
test('actionRequestHandler function exists', function () {
    player = pokerPlayer('qunit');
    expect(2);
    ok(player.actionRequestHandler, 'actionRequestHandler function should exist');
    equal(typeof player.actionRequestHandler, 'function', 'actionRequestHandler should be a function');
});

// TODO: test that player chooses one of the given possibleActions; type amd amount.
// For actionType RAISE the max amount is the given one and min is 1 ?
test('ActionResponse raise', function () {
    player = pokerPlayer('qunit');
    var possibleActions = [
        {"actionType":"FOLD", "amount":0},
        {"actionType":"CALL", "amount":50},
        {"actionType":"RAISE", "amount":25},
        {"actionType":"ALL_IN", "amount":1000}
    ];

    var expected = {"actionType":"RAISE", "amount":25};
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
test('eventHandlers exists ', function () {
    player = pokerPlayer('qunit');
    for (var i = 0; i < possibleEvents.length; i++) {
        var eventHandlerName = possibleEvents[i];
        ok(player.eventHandlers[eventHandlerName], 'eventHandler ' + eventHandlerName + ' should exist');
        equal(typeof player.eventHandlers[eventHandlerName], 'function', 'eventHandler ' + eventHandlerName + ' should be a function');
    }
    ;
});

module('Player State');

test('playerState correct before round', function () {
    player = pokerPlayer('qunit');
    equal(player.state.amount, 0, 'amount should be a 0 before game');
    equal(player.state.isTableDone, false, 'isTableDone should be false before game');
});

// Test events by calling player event handlers with real game data
// and verify that player can receive them without throwing exception
test('can execute event playerState correct from snapshot game round', function () {
    player = pokerPlayer('sdsdsd');
    for (var i = 0; i < gameRoundEvents.length; i++) {
        var json = gameRoundEvents[i];
        var clazz = json.type.split('.').pop();
        var actionRequest = (clazz === 'ActionRequest');
        if (actionRequest) {
            var actionResponse = player.actionRequestHandler(json.possibleActions);
            ok(actionResponse, 'actionResponse should exist');
        } else {
            var eventName = 'on' + clazz;
            player.eventHandlers[eventName](json);
            player.playerEventHandlers[eventName](json);
        }
        ok(true, 'eventHandler should run ok');
    }
});

test('playerState correct after round', function () {
    equal(player.state.amount, 0, 'amount should be a 0 when losing game');
    equal(player.state.isTableDone, true, 'isTableDone should be a true when losing game');
});


test('playerState correct after onTableIsDoneEvent', function () {
    player = pokerPlayer('Crazy_7');
    var event = {"players":[
        {"name":"Crazy_5", "chipCount":0},
        {"name":"Crazy_6", "chipCount":0},
        {"name":"Crazy_7", "chipCount":50000},
        {"name":"Raiser_8", "chipCount":7},
        {"name":"Hellmuth_9", "chipCount":10000},
        {"name":"sdsdsd", "chipCount":0}
    ], "type":"se.cygni.texasholdem.communication.message.event.TableIsDoneEvent"};
    player.eventHandlers['onTableIsDoneEvent'](event);
    equal(player.isWinner(), true, 'should be winner');
});


