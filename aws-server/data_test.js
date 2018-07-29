
require('console-stamp')(console, 'mm/dd/yy HH:MM:ss.l');

var mqtt = require('mqtt');
var fs = require('fs');
var sleep = require('system-sleep');

var serverUri = 'tcp://ec2-54-243-18-99.compute-1.amazonaws.com:1883';
var clientId = 'breaker-data-test';
var username = 'humblehome';
var password = '1896seniordesign';

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
	/*fs.readFile(process.argv[2], function(error, data) {
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
	});*/
	
	var power = 0.0;
	var time = 0.0;
	for (var i = 0; i < 500; i++) {
		for (var j = 1; j <= 8; j++) {
			var now = Date.now();
			var message = '{ "time": "' + now + '", "breakerId": "' + j + '", "current": "' + i + '", "voltage": "' + i + '" }';
			//console.log('sending message: ' + message);
			client.publish(BreakerData, message);
			power += i * i;
			if (now - time >= 5000) {
				console.log("Total Average Power: " + (power / (now - time)));
				power = 0.0;
				time = now;
			}
			
			sleep(1000);
		}
	}
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
