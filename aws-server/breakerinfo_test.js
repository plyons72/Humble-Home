var mqtt = require('mqtt');
var sleep = require('system-sleep');
/*
var serverUri = 'tcp://ec2-54-209-17-201.compute-1.amazonaws.com:1883';
var clientId = 'breaker-info-test';
var username = 'euDErYuDo857MH6Y2sQs';
var password = null;
*/
var serverUri = 'ssl://b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com:8883';
var clientId = 'breaker-info-test';
var username = 'user';
var password = 'humblehome1896';

var GetBreakerInfo = 'GetBreakerInfo';
var SetBreakerInfo = 'SetBreakerInfo';

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri);
	
	client.subscribe(SetBreakerInfo);
	
	for (var i = 1; i <= 32; i++) {
		client.publish(GetBreakerInfo, String(i));
		// Wait 5 seconds
		sleep(5000);
	}
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