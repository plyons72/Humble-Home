
var threshold = 0.25;

var appliances = [
	// AC1: Kitchen
	[
		new appliance("Dishwasher", 1800.0, 0.800),
		new appliance("Refrigerator", 146.0, 0.890),
		new appliance("Microwave", 999.996, 0.900)
	],
	// AC2: Range
	[
		new appliance("Electric Range/Oven", 8320.0, 0.457)
	],
	// AC3: Laundry1
	[
		new appliance("Washing Machine", 1440.0, 0.833)
	],
	// AC4: Laundry2
	[
		new appliance("Dryer", 6000.0, 0.900),
	],
	// AC5: HVAC
	[
		new appliance("AC", 1909.0, 0.850)
	],
	// AC6: Bedroom
	[
		new appliance("TV", 120.0, 0.750),
		new appliance("Overhead Fan", 70.56, 0.850)
	]
	// DC1
	// DC2
];

var lastPower = 0.0;

module.exports = {
	
	// Used by test.js
	getBreakerLoads: function(callback) {
		callback(appliances);
	},
	
	// Compare lastPower to power (voltage * current) to determine difference and power factor
	getPowerFactor: function (breakerId, power/*current, voltage*/, callback) {
		
		console.log('powerFactor() breakerId: ' + breakerId + ', power: ' + power/*', current: ' + current + ', voltage: ' + voltage*/);
		
		console.log('Last Power: ' + lastPower);
		
		var appliedPower = power/*voltage * current*/;
		console.log('Applied Power: ' + appliedPower);
		
		var powerDiff = appliedPower - lastPower;
		console.log('Power Difference: ' + powerDiff);
		
		var breakerLoads = appliances[breakerId - 1];
		for (var i = 0; i < breakerLoads.length; i++) {
			if (Math.abs(powerDiff) >= breakerLoads[i].min && Math.abs(powerDiff) <= breakerLoads[i].max) {
			console.log(breakerLoads[i].toString());
				if (powerDiff > 0) {
					console.log(breakerLoads[i].name + ' is on');
				} else {
					console.log(breakerLoads[i].name + ' is off');
				}
				callback(breakerLoads[i].powerFactor);
			}
		}
		
		lastPower += powerDiff;
	}
  
};

function appliance(n, s, pf) {
	this.name = n;
	this.appliedPower = s;
	this.min = this.appliedPower - (threshold * this.appliedPower);
	this.max = this.appliedPower + (threshold * this.appliedPower);
	this.powerFactor = pf;
	
	this.toString = function() {
		return 'name: ' + this.name + ', appliedPower: ' + this.appliedPower + ', min: ' + this.min + ', max: ' + this.max + ', powerFactor: ' + this.powerFactor;
	};
}