
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');

// Create DynamoDB service object
var ddb = new AWS.DynamoDB({apiVersion: '2012-08-10'});

var ddb_doc = new AWS.DynamoDB.DocumentClient({apiVersion: '2012-08-10'});

var INFO_TABLE = 'humblehome-mobilehub-2128789566-Breakers';
var DATA_TABLE = 'CurrentVoltageData';
var USER_ID = '48756d626c65486f6d65';

module.exports = {
	
	getBreakerInfo: function (callback) {
		
		console.log('getBreakerInfo()');
		
		var params = {
			TableName: INFO_TABLE,
			FilterExpression: 'contains (userId, :userId)',
			ExpressionAttributeValues: {
				':userId': {S: USER_ID}
			}
		};
		
		ddb.scan(params, function(error, result) {
			if (error) {
				console.log(error, error.stack);
				callback(error);
			} else {
				callback(result);
			}
		});
	
	},
	
	getBreakerInfoById: function (id, callback) {
		
		console.log('getBreakerInfoById() id: ' + id);
		
		var params = {
			TableName: INFO_TABLE,
			Key: {
				'userId': {S: USER_ID},
				'breakerId': {N: id}
			}
		};
		
		ddb.getItem(params, function(error, result) {
			if (error) {
				console.log(error, error.stack);
				callback(error);
			} else {
				callback(result);
			}
		});
		
	},
	
	putBreakerInfoById: function(info, callback) {
		
		console.log('putBreakerInfoById() info: ', info);
		
		var params = {
			TableName: INFO_TABLE,
			Key: {
				'userId': USER_ID,
				'breakerId': Number(info.breakerId)
			},
			UpdateExpression: 'set label = :label, description = :description, breakerState = :breakerState',
			ExpressionAttributeValues: {
				':label': info.label,
				':description': info.description,
				':breakerState': Number(info.breakerState)
			}
		};
		
		console.log(params);
		
		ddb_doc.update(params, function(error, result) {
			if (error) {
				console.log(error, error.stack);
				console.log('Error', error);
				callback(error);
			} else {
				console.log('Success', result);
				callback(result);
			}
		});
		
	},
	
	putBreakerData: function (data) {
		
		console.log('putBreakerData() data: ', data);

		var params = {
			TableName: DATA_TABLE,
			Item: {
				'userId': {S: USER_ID},
				'timestamp': {S: data.timestamp},
				/*'current': {N: data.current},
				'voltage': {N: data.voltage},*/
				'power': {N: data.power}
			}
		};
	
		ddb.putItem(params, function(error, result) {
			if (error) {
				console.log('Error', error);
				return error;
			} else {
				console.log('Success', result);
				return result;
			}
		});
	
	}
	
}