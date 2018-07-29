var mqtt = require('mqtt');
var sleep = require('system-sleep');

var serverUri = 'tcp://ec2-54-243-18-99.compute-1.amazonaws.com:1883';
var clientId = 'breaker-info-test';
var username = 'humblehome';
var password = '1896seniordesign';

var GetBreakerInfo = 'GetBreakerInfo';
var PutBreakerInfo = 'PutBreakerInfo';
var SetBreakerInfo = 'SetBreakerInfo';

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri);
	
	/*for (var i = 1; i <= 32; i++) {
		client.publish(GetBreakerInfo, String(i));
		// Wait 5 seconds
		sleep(5000);
	}*/
	
	//client.publish(GetBreakerInfo, '*');
	
	//client.publish(PutBreakerInfo, '{ "breakerId": "2", "label": "Living Room", "description": "Lights, TV, Computer", "breakerState": "1" }');
	
	//client.publish('SetBreakerState', '{ "breakerId": "2", "breakerState": "2" }');

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