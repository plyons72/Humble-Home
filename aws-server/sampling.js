
var sleep = require('system-sleep');

// Sampling period in milliseconds
var period = 5000;

var power = 0.0;
var time = 0;

module.exports = {
	
	sample: function (data, callback) {
		
		power += data.current * data.voltage;
		//console.log('power: ' + power);
		
		if (time == 0) {
			time = data.time;
		}

		if (data.time - time >= period) {
			// Calculate average power for all breakers over the sample period
			var avgPower = power / (data.time - time);
			console.log('Average Power: ' + avgPower);
			
			power = 0.0;
			time = data.time;
		
			callback(data.time, avgPower);
		}
		
	}
	
}
