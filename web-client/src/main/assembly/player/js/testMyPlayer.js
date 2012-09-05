var myPlayer = pokerPlayer('myRaiserPlayer');


module('MyPlayer Action');

test('ActionResponse is raise', function() {  
	var request = {
     "requestId":"c8586f44-39b8-4aaf-a6ef-572ea64eb7c3",
     "possibleActions":[
	     {"actionType":"FOLD","amount":0},
	     {"actionType":"CALL","amount":50},
	     {"actionType":"RAISE","amount":25},
	     {"actionType":"ALL_IN","amount":1000}
     ]};

	var response = myPlayer.actionRequestHandler(request);
    equal(response.action.actionType, "RAISE", 'response is RAISE');
    equal(response.action.amount, 25, 'response amount is 25');
});

test('ActionResponse is call', function() {  
	var request = {
     "requestId":"c8586f44-39b8-4aaf-a6ef-572ea64eb7c3",
     "possibleActions":[
	     {"actionType":"FOLD","amount":0},
	     {"actionType":"CALL","amount":50},
	     {"actionType":"ALL_IN","amount":1000}
     ]};

	var response = myPlayer.actionRequestHandler(request);
    equal(response.action.actionType, "CALL", 'response is CALL');
    equal(response.action.amount, 50, 'response amount is 50');
});

// TODO: 
// Test result of events that change player state