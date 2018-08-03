
// This script is based on the pseudocode provided by:
// Jean-Paul van Brakel, Smoothed z-score algorithm, 2016, [online] Available: http://stackoverflow.com/questions/22583391/peak-signal-detection-in-realtime-timeseries-data.

var WINDOW_SIZE = 96;

// the lag of the moving window
var lag = 4;    
// the z-score at which the algorithm signals
var threshold = 32;
// the influence (between 0 and 1) of new signals on the mean and standard deviation
var influence = 0.05;

var count = 0;
var signals = new Array(WINDOW_SIZE).fill(0);
var filteredData = new Array(WINDOW_SIZE).fill(0.0);
var avgFilter = new Array(WINDOW_SIZE).fill(0.0);
var stdFilter = new Array(WINDOW_SIZE).fill(0.0);


module.exports = {
	
	peakDetect: function (data, callback) {
    	
		console.log('peakDetect()' + ' data = ' + data + ' count = ' + count);
		
		var peakDetect = 0;
		
		if (count == WINDOW_SIZE - 1) {
			var signals = 'signals: ';
			var filteredData = 'filteredData: ';
			for (var i = 0; i < signals.length; i++) {
				signals += '\n' + signals[i];
				output += '\n' + filteredData[i];
			}
			//console.log(signals);
			//console.log(filteredData);
		}
			
		if (count == WINDOW_SIZE) {
			// Reset algorithm
			count = 0;
			signals = new Array(WINDOW_SIZE).fill(0);
			filteredData = new Array(WINDOW_SIZE).fill(0.0);
			avgFilter = new Array(WINDOW_SIZE).fill(0.0);
			stdFilter = new Array(WINDOW_SIZE).fill(0.0);
		}
			
		if (count > lag) {
				
			if (count == lag + 1) {
				var sum = 0.0;
				for (var i = 0; i < lag + 1; i++) {
					sum = sum + filteredData[i];
				}
				var avg = sum / lag;
					
				var stdSum = 0.0;
				for (var i = 0; i < lag + 1; i++) {
					stdSum = stdSum + Math.pow((filteredData[i] - avg), 2);
				}
				var std = stdSum / (lag - 1);
				
				avgFilter[lag] = avg;
				stdFilter[lag] = std;
			}
			
			if (Math.abs(data - avgFilter[count - 1]) > threshold * stdFilter[count - 1]) {
					
				if (data > avgFilter[count - 1]) {
					signals[count] = 1;
				} else {
					signals[count] = -1;
				}
				filteredData[count] = influence * data + (1 - influence) * filteredData[count - 1];
				
			} else {
			
				signals[count] = 0;
				filteredData[count] = data;
				
			}
			
			peakDetect = signals[count];
			
			var sum = 0.0;
			for (var i = count - lag; i < count; i++) {
				sum = sum + filteredData[i];
			}
			var avg = sum / lag;
			
			var stdSum = 0.0;
			for (var i = count - lag; i < count; i++) {
				stdSum = stdSum + Math.pow((filteredData[i] - avg), 2);
			}
			var std = stdSum / (lag - 1);
			
			avgFilter[count] = avg;
			stdFilter[count] = std;
		
		} else {
		
			filteredData[count] = data;
			
		}
		
		count++;
		
		callback(peakDetect);
		
	}
  
};