
var sleep = require('system-sleep');

// Sampling period in milliseconds
var period = 5000;

var power = 0.0;
var startTime = 0;

module.exports = {
	
	sample: function (time, instPower, callback) {
		
		power += instPower;
		//console.log('power: ' + power);
		
		if (startTime == 0) {
			startTime = data.time;
		}

		if (data.time - startTime >= period) {
			// Calculate average power for all breakers over the sample period
			var avgPower = power / (data.time - startTime);
			console.log('Average Power: ' + avgPower);
			
			power = 0.0;
			startTime = data.time;
		
			callback(time, avgPower);
		}
		
	}
	
}
