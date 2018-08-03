
require('console-stamp')(console, 'mm/dd/yy HH:MM:ss.l');

var mqtt = require('mqtt');
var fs = require('fs');
var sleep = require('system-sleep');

var power_factor = require('./power_factor');

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
var SetBreakerData = 'SetBreakerData';
var SwitchSource = 'SwitchSource';

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri);
	
	// Call test function(s) here
	peakShaving();
	//putBreakerData();
	//getBreakerData('0');
	//getBreakerData('2');
	//powerFactor();
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

function getBreakerData(param) {
	client.publish(GetBreakerData, param);
}

function putBreakerData() {
	//sampling();
	// Assume cmd = node test.js path\to\test\data\file
	fs.readFile(process.argv[2], function(error, data) {
		if (!error) {
			var loads = data.toString().split('\n');
			
			for (var i = 0; i < loads.length; i++) {
				var message = { timestamp: String(Date.now()), power: String(Number(loads[i])) };
				console.log(message);
				client.publish(PutBreakerData, JSON.stringify(message));
				sleep(500);
			}
		} else console.log('error: ' + error);
	});
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

// Comment out calls to sampling.sample() and ddb_access.putBreakerData() in server.js message receive (topic = PutBreakerData)
function powerFactor() {
	power_factor.getBreakerLoads(function(breakers) {
		for (var i = 0; i < breakers.length; i++) {
			var loads = breakers[i];
			for (var j = 0; j < loads.length; j++) {
				var message = { breakerId: (i + 1), power: loads[j].appliedPower };
				console.log('appliance on: ' + loads[j].name);
				console.log(message);
				client.publish(PutBreakerData, JSON.stringify(message));
				sleep(5000);
				
				message = { breakerId: (i + 1), power: 0.0 };
				console.log('appliance off: ' + loads[j].name);
				console.log(message);
				client.publish(PutBreakerData, JSON.stringify(message));
				sleep(5000);
			}
		}
	});
}

// Comment out calls to sampling.sample() and ddb_access.putBreakerData() in server.js message receive (topic = PutBreakerData)
function peakShaving() {
	putBreakerData();
	/*// Assume cmd = node test.js path\to\test\data\file
	fs.readFile(process.argv[2], function(error, data) {
		if (!error) {
			var loads = data.toString().split('\n');
			
			for (var i = 0; i < loads.length; i++) {
				console.log(loads[i]);
				client.publish(PutBreakerData, loads[i]);
				sleep(100);
			}
		} else console.log('error: ' + error);
	});*/
}