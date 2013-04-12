/*global pokerfront, io*/
'use strict';


/*
pokerfront.factory('socket', function ($rootScope) {
    var socket = io.connect();
    return {
        on: function (eventName, callback) {
            socket.on(eventName, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                    callback.apply(socket, args);
                });
            });
        },
        emit: function (eventName, data, callback) {
            socket.emit(eventName, data, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                    if (callback) {
                        callback.apply(socket, args);
                    }
                });
            })
        }
    };
});
*/

pokerfront.factory('serverStatusSocket', function ($rootScope) {
    var ws = new WebSocket("ws://localhost:8080/websockets/test");

    ws.onopen = function(ev) {
        console.log("connection is up");
        ws.send('{ "command" : "options", "value" : "noop" }');
    };

    ws.onmessage = function(ev) {
        console.log(ev.data);
    };

    ws.onclose = function(ev) {
        console.log("Lost connection to websocket");
    }



});