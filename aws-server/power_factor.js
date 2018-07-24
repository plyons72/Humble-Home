
var appliances = {
	Kitchen: [
		new appliance("Dishwasher", 1440.0, 1800.0),
		new appliance("Refrigerator", 130.0, 146.0),
		new appliance("Electric Range/Oven", 3800.0, 8320.0),
		new appliance("Microwave", 900.0, 900.0)
	],
	Laundry: [
		new appliance("Washing Machine", 1200.0, 1440.0),
		new appliance("Dryer", 5400.0, 6000.0)
	],
	Living_Room: [
		new appliance("TV", 28.0, 39.996),
		new appliance("Gaming System", 137.0, 137.0),
	],
	HVAC: [
		new appliance("AC", 5200.0, 13340.0),
	],
	Bathroom: [
		new appliance("Hair Dryer", 1800.0, 0.0),
		new appliance("Fan", 11.6, 60.0),
	],
	Bedroom_1: [
		new appliance("TV", 28.0, 39.996),
		new appliance("Overhead Fan", 60.0, 55.0),
		new appliance("Sleep Apnea Machine", 53.0, 57.0),
	],
	Bedroom_2: [
		new appliance("TV", 28.0, 39.996),
		new appliance("Overhead Fan", 60.0, 55.0),
	],
	DC_Lighting_Circuit: [
		new appliance("Light", 9.0, 9.0),
		new appliance("Charger", 60.0, 60.0),
	]
};

var last_power = 0.0;

module.exports = {
	
	power_factor: function (breakerId, current, voltage) {
		// compare last_power to power (current * voltage) to determine difference and power factor
	}
  
};

function appliance(n, p, s) {
	this.name = n;
	this.inst_power = p;
	this.app_power = s;
}