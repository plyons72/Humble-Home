
var mqtt = require('mqtt');
var fs = require('fs');
var sleep = require('system-sleep');
/*
var serverUri = 'tcp://ec2-54-209-17-201.compute-1.amazonaws.com:1883';
var clientId = 'breaker-data-test';
var username = 'euDErYuDo857MH6Y2sQs';
var password = null;
*/
var serverUri = 'ssl://b-f6c789c3-b708-4d73-b004-2a6245bd7c5d-1.mq.us-east-1.amazonaws.com:8883';
var clientId = 'breaker-data-test';
var username = 'user';
var password = 'humblehome1896';

var BreakerData = "BreakerData";

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('\nconnected to ' + serverUri);
	
	// assume cmd = node test.js path\to\test\data\file
	fs.readFile(process.argv[2], function(error, data) {
		if (!error) {
			var loads = data.toString().split('\n');
			
			for (var i = 0; i < loads.length; i++) {
				console.log(loads[i]);
				client.publish(BreakerData, loads[i]);
				// Wait 5 seconds
				sleep(5000);
				//sleep(1000);
			}
		} else console.log('error: ' + error);
	});
});
    
client.on('reconnect', function() {
    console.log('\nreconnected to ' + serverUri);
});
   
client.on('error', function(error) {
    console.log('\nerror: ' + error); 
});
    
client.on('message', function(topic, message) {
    console.log('\ntopic: ' + topic + ' \nmessage: ' + message);
});