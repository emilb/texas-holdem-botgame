var responseClass = "se.cygni.texasholdem.communication.message.response.ActionResponse";


module('General');
test('name', function() {  
	var player = pokerPlayer('qunit');
    equal( player.name, 'qunit', 'name is ok');  
});

module('Action');
test('ActionResponse raise', function() {  
	var player = pokerPlayer('qunit');
	var request = {
     "requestId":"c8586f44-39b8-4aaf-a6ef-572ea64eb7c3",
     "possibleActions":[
	     {"actionType":"FOLD","amount":0},
	     {"actionType":"CALL","amount":50},
	     {"actionType":"RAISE","amount":25},
	     {"actionType":"ALL_IN","amount":1000}
     ]};

	var response = player.actionRequestHandler(request);

	var expected = {
            "type":responseClass,
            "requestId":"c8586f44-39b8-4aaf-a6ef-572ea64eb7c3",
            "action":{"actionType":"RAISE","amount":25}
	};

    deepEqual(response, expected, 'response is RAISE 25');  
});

module('EventCallbacks');
// TODO



