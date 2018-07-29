
require('console-stamp')(console, 'mm/dd/yy HH:MM:ss.l');

var mqtt = require('mqtt');
var fs = require('fs');
var sleep = require('system-sleep');

var serverUri = 'tcp://ec2-54-243-18-99.compute-1.amazonaws.com:1883';
var clientId = 'test-client';
var username = 'humblehome';
var password = '1896seniordesign';

var GetBreakerInfo = 'GetBreakerInfo';
var PutBreakerInfo = 'PutBreakerInfo';
var SetBreakerInfo = 'SetBreakerInfo';
var GetBreakerState = 'GetBreakerState';
var PutBreakerState = 'PutBreakerState';
var SetBreakerState = 'SetBreakerState';
var GetBreakerData = 'GetBreakerData';
var PutBreakerData = 'PutBreakerData';

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri);
	
	// Call test function(s) here

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

// Test functions for different features

function getBreakerInfo() {
	client.publish(GetBreakerInfo, '*');
}

function getBreakerInfoById() {
	for (var i = 1; i <= 32; i++) {
		client.publish(GetBreakerInfo, String(i));
		// Wait 5 seconds
		sleep(5000);
	}
}

function putBreakerInfoById() {
	client.publish(PutBreakerInfo, '{ "breakerId": "2", "label": "Living Room", "description": "Lights, TV, Computer", "breakerState": "1" }');
	
}

function getBreakerData() {
	// TODO
}

function putBreakerData() {
	sampling();
}

function sampling() {
	var power = 0.0;
	var time = 0.0;
	for (var i = 0; i < 50; i++) {
		for (var j = 1; j <= 8; j++) {
			var now = Date.now();
			if (time == 0) time = now;
			var message = {time: now, breakerId: j, current: i, voltage: i};
			client.publish(PutBreakerData, JSON.stringify(message));
			power += i * i;
			if (now - time >= 5000) {
				console.log('Average Power: ' + (power / (now - time)));
				power = 0.0;
				time = now;
			}
			
			sleep(100);
		}
	}
}

function powerFactor() {
	// TODO
}

// Comment out calls to sampling.sample() and ddb_access.putBreakerData() in server.js message receive (topic = PutBreakerData)
function peakShaving() {
	// Assume cmd = node test.js path\to\test\data\file
	fs.readFile(process.argv[2], function(error, data) {
		if (!error) {
			var loads = data.toString().split('\n');
			
			for (var i = 0; i < loads.length; i++) {
				console.log(loads[i]);
				client.publish(PutBreakerData, loads[i]);
				// Wait 5 seconds
				sleep(5000);
			}
		} else console.log('error: ' + error);
	});
}