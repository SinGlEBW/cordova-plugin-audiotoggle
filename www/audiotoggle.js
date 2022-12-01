	var exec = require('cordova/exec');
	let classInJava = 'AudioTogglePlugin';
	let isEvent = false;

	exports.BLUETOOTH = 'bluetooth';
	exports.INCALL = 'incall';
	exports.SPEAKER = 'speaker';
	exports.EARPIECE = 'earpiece';
	exports.NORMAL = 'normal';
	exports.RINGTONE = 'ringtone';
	exports.setAudioMode = function (mode) {
		exec(null, null, classInJava, 'setAudioMode', [mode]);
	};
	exports.setAudioDevice = function (type) {
		exec(null, null, classInJava, 'setAudioDevice', [type]);
	};
	
	exports.displayIOSAudioRoutingComponent = function () {
	  exec(null, null, classInJava, 'displayIOSAudioRoutingComponent');
	};
	
	exports.hasAudioRoutingOptions = function (callback) {
	  exec(callback, null, classInJava, 'checkAudioRoutingOptions');
	};
	
	exports.setBluetoothScoOn = function (toggle) {
		exec(null, null, classInJava, 'setBluetoothScoOn', [toggle]);
	};
	
	exports.setSpeakerphoneOn = function (toggle) {
		exec(null, null, classInJava, 'setSpeakerphoneOn', [toggle]);
	};
	
	exports.getOutputDevices = function (callback) {
		exec(callback, null, classInJava, 'getOutputDevices', []);
	};
	
	exports.on = function (eventName, callback) {

		// navigator.mediaDevices.addEventListener('devicechange', navigator.mediaDevices.ondevicechange);
		
		if(eventName === 'devicechange' && !isEvent){
			isEvent = true;
			exec(callback, null, classInJava, "registerListener", ['audioOutputsAvailable']);//argument для ios
		}


	};
	
	exports.getAudioMode = function (callback) {
		exec(callback, null, classInJava, 'getAudioMode', []);
	};
	
	exports.getAudioSystem = function (callback) {
	  exec(callback, null, classInJava, 'getAudioSystem', []);
	};
	
	exports.isBluetoothScoOn = function (callback) {
		exec(callback, null, classInJava, 'isBluetoothScoOn', []);
	};
	
	exports.isSpeakerphoneOn = function (callback) {
		exec(callback, null, classInJava, 'isSpeakerphoneOn', []);
	};
	
	exports.hasBuiltInEarpiece = function(successCb, errorCb) {
		exec(successCb, errorCb, classInJava, 'hasBuiltInEarpiece');
	};
	
	exports.hasBuiltInSpeaker = function(successCb, errorCb) {
		exec(successCb, errorCb, classInJava, 'hasBuiltInSpeaker');
	};
