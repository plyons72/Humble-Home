
var WINDOW_SIZE = 96;

// the lag of the moving window
var lag = 5;    
// the z-score at which the algorithm signals
var threshold = 3.5;
// the influence (between 0 and 1) of new signals on the mean and standard deviation
var influence = 0.35;

var cnt = 0;
var signals = new Array(WINDOW_SIZE).fill(0);
var filteredData = new Array(WINDOW_SIZE).fill(0.0);
var avgFilter = new Array(WINDOW_SIZE).fill(0.0);
var stdFilter = new Array(WINDOW_SIZE).fill(0.0);


module.exports = {
	
	peakDetect: function (data) {
    	
		console.log('\npeakDetect()' + '\ndata = ' + data + '\ncnt = ' + cnt);
		
		if (cnt < WINDOW_SIZE) {
			
			if (cnt > lag) {
				
				if (cnt == lag + 1) {
					var sum = 0.0;
					for (var i = 0; i < lag + 1; i++)
						sum = sum + filteredData[i];
					var avg = sum / lag;
					
					var stdSum = 0.0;
					for (var i = 0; i < lag + 1; i++)
						stdSum = stdSum + Math.pow((filteredData[i] - avg), 2);
					var std = stdSum / (lag - 1);
					
					for (var i = 0; i < lag + 1; i++) {
						avgFilter[i] = avg;
						stdFilter[i] = std;
					}
				}
			
				if (Math.abs(data - avgFilter[cnt - 1]) > threshold * stdFilter[cnt - 1]) {
						
						if (data > avgFilter[cnt - 1])
							signals[cnt] = 1;
						else
							signals[cnt] = -1;
						
						filteredData[cnt] = influence * data + (1 - influence) * filteredData[cnt - 1];
					
				} else {
				
					signals[cnt] = 0;
					filteredData[cnt] = data;
					
				}
				
				var sum = 0.0;
				for (var i = cnt - lag; i < cnt; i++)
					sum = sum + filteredData[i];
				var avg = sum / lag;
				
				var stdSum = 0.0;
				for (var i = cnt - lag; i < cnt; i++)
					stdSum = stdSum + Math.pow((filteredData[i] - avg), 2);
				var std = stdSum / (lag - 1);
				
				avgFilter[cnt] = avg;
				stdFilter[cnt] = std;
			
			} else {
			
				filteredData[cnt] = data;
				
			}
			
			cnt++;
			
		} else {
			
			console.log('\nwindow full...reset?');
				
		}
			
		console.log('\nfilteredData = ' + filteredData);
		console.log('\nsignals = ' + signals);
		
	}
  
};