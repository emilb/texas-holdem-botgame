var net = require('net');

var HOST = '127.0.0.1';
var PORT = 4711;
var jsonDelimiter = '_-^emil^-_';

var client = new net.Socket();
client.connect(PORT, HOST, function() {

    console.log('CONNECTED TO: ' + HOST + ':' + PORT);
    // Write a message to the socket as soon as the client is connected, the server will receive it as message from the client
    //client.write('I am Chuck Norris!');
    client.write('{"type":"se.cygni.texasholdem.communication.message.request.RegisterForPlayRequest","sessionId":null,"requestId":"39b9fda8-8807-4ac7-aa22-95fab0783ac6","name":"nodejs","room":"TRAINING"}_-^emil^-_');

});

// Add a 'data' event handler for the client socket
// data is what the server sent to this socket
client.on('data', function(data) {
    console.log('data: ' + data);

    var d1 = data.toString();
    var dataObj = JSON.parse(d1.replace(jsonDelimiter, ''));

    console.log('type: ' + dataObj.type);
});

// Add a 'close' event handler for the client socket
client.on('close', function() {
    console.log('Connection closed');
});

client.on('error', function() {
    console.log('Error');
})