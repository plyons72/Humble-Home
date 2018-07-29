
require('console-stamp')(console, 'mm/dd/yy HH:MM:ss.l');

var ddb_access = require('./dynamodb_access');
var sampling = require('./sampling');
var power_factor = require('./power_factor');
var peak_shaving = require('./peak_shaving');

var mqtt = require('mqtt');

var serverUri = 'tcp://ec2-54-243-18-99.compute-1.amazonaws.com:1883';
var clientId = 'aws-client';
var username = 'humblehome';
var password = '1896seniordesign';

var GetBreakerInfo = 'GetBreakerInfo';
var PutBreakerInfo = 'PutBreakerInfo';
var SetBreakerInfo = 'SetBreakerInfo';
var GetBreakerState = 'GetBreakerState';
var PutBreakerState = 'PutBreakerState';
var SetBreakerState = 'SetBreakerState';
var BreakerData = 'BreakerData';

var client = mqtt.connect(serverUri, {
	clientId: clientId,
    username: username,
    password: password,
    rejectUnauthorized: false
});
    
client.on('connect', function(connack) {
    console.log('connected to ' + serverUri); 
	
	client.subscribe(GetBreakerInfo);
	client.subscribe(PutBreakerInfo);
	client.subscribe(SetBreakerInfo);
	client.subscribe(GetBreakerState);
	client.subscribe(PutBreakerState);
	client.subscribe(SetBreakerState);
	client.subscribe(BreakerData);
});
    
client.on('reconnect', function() {
    console.log('reconnected to ' + serverUri);
});
   
client.on('error', function(error) {
    console.log('error: ' + error); 
});
    
client.on('message', function(topic, message) {
    //console.log('topic: ' + topic + '\nmessage: ' + message);
	
	if (topic == GetBreakerInfo) {
		if (message.toString() == '*') {
			ddb_access.getBreakerInfo(function(response) {
				client.publish(SetBreakerInfo, JSON.stringify(response));
			});
		} else {
			ddb_access.getBreakerInfoById(message.toString(), function(response) {
				client.publish(SetBreakerInfo, JSON.stringify(response));
			});	
		}
	} else if (topic == PutBreakerInfo) {
		ddb_access.putBreakerInfoById(JSON.parse(message), function(response) {
			// Do nothing?
		});
	} else if (topic == BreakerData) {
		sampling.sample(JSON.parse(message), function(timestamp, power) {
			var data = {timestamp: String(timestamp), power: String(power)};
			ddb_access.putBreakerData(data);
			//peak_shaving.peak_detect(Number(message));
		});
	}
});