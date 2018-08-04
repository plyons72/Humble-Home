
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
	
	// Get record for all breakers (id, label, description, state) from DynamoDB Breakers table
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
	
	// Get record for one breaker (id, label, description, state) from DynamoDB Breakers table
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
	
	// Update record for one breaker (label, description, state) in DynamoDB Breakers table
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
	
	// Get all records from DynamoDB CurrentVoltageData table
	// Process records to return the requested information
	getBreakerData: function (id, callback) {
		
		console.log('getBreakerData() id: ' + id);
		
		var params = {
			TableName: DATA_TABLE,
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
				
				// Request for all records
				if (id == -1) {				
					callback(result);
				} else {
					var dailyDataPoints = 96;		// number of expected data points in one day (every 15 mins for 24 hrs)
					console.log(result.Count);			
					
					var index = 0;
					var data = [];
					var dataPoints = result.Items;
					
					// Request for records from the current day
					if (id == 0) {
						if (result.Count > dailyDataPoints) {
							if (result.Count % dailyDataPoints != 0) {
								index = result.Count - (result.Count % dailyDataPoints);
							} else {
								index = result.Count - dailyDataPoints;
							}
						}
						console.log('index: ' + index);
						data = dataPoints.slice(index);
						
					// Request for records from the last [id] number of days
					} else {
						if (Math.floor(result.Count / dailyDataPoints) < id) {
							for (var i = 0; i < (id - Math.floor(result.Count / dailyDataPoints)); i++) {
								data.push(0.0);
							}
						} else if (result.Count % dailyDataPoints != 0) {
							index = result.Count - (result.Count % dailyDataPoints) - ((id - 1) * dailyDataPoints);
						} else {
							index = result.Count - (id * dailyDataPoints);
						}
						console.log('index: ' + index);
						var temp = 0.0;
						for (var i = index; i < result.Count; i++) {
							temp += Number(dataPoints[i].power.N);
							if (((i + 1) % dailyDataPoints) == 0) {
								data.push(temp);
								temp = 0.0;
							}
						}
						if (temp != 0.0) data.push(temp);
					}
					var response = { id: id, data: data };
					callback(response);
				}
			}
		});
	
	},
	
	// Add new record to DynamoDB CurrentVoltageData table
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