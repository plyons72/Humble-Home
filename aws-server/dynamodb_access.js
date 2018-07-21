
// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
AWS.config.loadFromPath('./config.json');

// Create DynamoDB service object
var ddb = new AWS.DynamoDB({apiVersion: '2012-10-08'});

var INFO_TABLE = 'humblehome-mobilehub-2128789566-Breakers';
var USER_ID = '48756d626c65486f6d65';

var DATA_TABLE = '';

module.exports = {
	
	getBreakerInfo: function (id) {
		
		console.log(id);
		
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
				return error;
			} else {
				console.log(result);
				return result;
			}
		});
		
	},
	
	putBreakerData: function (data) {

		var params = {
			TableName: DATA_TABLE,
			// Table attributes here
		};
	
		ddb.putItem(params, function(error, result) {
			if (error) {
				console.log('Error', error);
			} else {
				console.log('Success', results);
			}
		});
	
	}
	
}