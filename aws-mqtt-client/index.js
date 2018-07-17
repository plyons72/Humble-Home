
var mqtt = require('mqtt');

var serverUri = "ssl://b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com:8883";
var clientId = "aws-lambda";
var username = "user";
var password = "humblehome1896";

var BreakerInfoRequest = "BreakerInfoRequest";
var BreakerInfoResponse = "BreakerInfoResponse";
var BreakerState = "BreakerState";


var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri); 
});
    
client.on('reconnect', function() {
    console.log('reconnected to ' + serverUri);
});
   
client.on('error', function(error) {
    console.log('error: ' + error); 
});
    
client.on('message', function(topic, message) {
    console.log('topic: ' + topic + '\nmessage: ' + message); 
});