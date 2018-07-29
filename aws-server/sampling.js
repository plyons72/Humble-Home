
var sleep = require('system-sleep');

// Sampling period in milliseconds
var period = 5000;
var breaker_buffers = [
	new breaker_buf(1),
	new breaker_buf(2),
	new breaker_buf(3),
	new breaker_buf(4),
	new breaker_buf(5),
	new breaker_buf(6),
	new breaker_buf(7),
	new breaker_buf(8)
];

module.exports = {
	
	sample: function (data, callback) {
		breaker_buffers[data.breakerId - 1].buf.push(new measurement(data.time, data.current, data.voltage));
		
		//printBreakerBuffers();
		
		var index = breaker_buffers[data.breakerId - 1].setIndex();
		
		var arrived = true;
		for (var i = 0; i < breaker_buffers.length; i++) {
			if (breaker_buffers[i].index == -1) {
				//console.log('not all samples have arrived');
				arrived = false;
				break;
			}
		}

		if (arrived) {
			console.log('all samples have arrived');
			// calculate average power for all breakers over the sample period and reset buffers
			var totalAvgPower = 0.0;
			for (var i = 0; i < breaker_buffers.length; i++) {
				totalAvgPower += breaker_buffers[i].getAvgPower();			
			    breaker_buffers[i].resetBuffer();
			}
			console.log("Total Average Power: " + totalAvgPower);
		}
		
		//callback(data);
	}
	
}

function breaker_buf(id) {
	this.breakerId = id;
	this.buf = [];
	this.lastTime = 0;
	this.index = -1;
	
	this.setIndex = function() {
		if (this.index == -1) {
			for (var i = 0; i < this.buf.length; i++) {
				if (this.buf[i].time - this.lastTime >= period) {
					this.index = i;
					break;
				}
			}
		}
		return this.index;
	};
	
	this.resetBuffer = function() {
		var lastSample = null;
		for (var i = 0; i <= this.index; i++) {
			lastSample = this.buf.shift();
		}
		if (lastSample != null) {
			this.lastTime = lastSample.time;
		}
		this.index = -1;
	};
	
	this.getAvgPower = function() {
		var totalPower = 0.0;
		for (var i = 0; i <= this.index; i++) {
			totalPower += this.buf[i].power;
		}
		return totalPower / (this.buf[this.index].time - this.lastTime);
	};
	
	this.toString = function() {
		var s = 'breakerId: ' + this.breakerId + '\n';
		s += 'buf: [\n';
		for (var i = 0; i < this.buf.length; i++) {
			s += '\t' + this.buf[i].toString();
		}
		s += ']';
		return s;
	};
}

function measurement(t, c, v) {
	this.time = t;
	this.current = c;
	this.voltage = v;
	this.power = this.current * this.voltage;
	
	this.toString = function() {
		return String('time: ' + this.time + ', current: ' + this.current + ', voltage: ' + this.voltage + ', power: ' + this.power +'\n');
	};
}

function printBreakerBuffers() {
	var s = 'breaker_buffers = [\n';
	
	for (var i = 0; i < breaker_buffers.length; i++) {
		s += breaker_buffers[i].toString() + '\n';
	}
		
	s += ']';
	console.log(s);
}