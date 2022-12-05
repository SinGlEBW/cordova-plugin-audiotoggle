## AudioToggle

Cordova plugin for switching between speaker and earpiece when playing audio.

    cordova plugin add https://github.com/SinGlEBW/cordova-plugin-audiotoggle
    
### Supported Platforms

- Android
- iOS

### Usage

To set the current audio mode, use the `setAudioMode` method:
    //Mode
	AudioToggle.BLUETOOTH = 'bluetooth';
	AudioToggle.INCALL = 'incall';
	AudioToggle.SPEAKER = 'speaker';
	AudioToggle.EARPIECE = 'earpiece';
	AudioToggle.NORMAL = 'normal';
	AudioToggle.RINGTONE = 'ringtone';
	
	AudioToggle.setAudioMode(mode);


    /** Android 12 * setAudioDevice
    
     * TYPE_BLE_HEADSET: 26
     * TYPE_BLE_SPEAKER: 27
     * TYPE_BLUETOOTH_SCO: 7
     * TYPE_BUILTIN_EARPIECE: 1
     * TYPE_BUILTIN_SPEAKER: 2
     * TYPE_USB_HEADSET: 22
     * TYPE_WIRED_HEADPHONES: 4
     * TYPE_WIRED_HEADSET: 3
     * TYPE_TELEPHONY: 18
     */


	AudioToggle.setAudioDevice(type);
	
	AudioToggle.displayIOSAudioRoutingComponent();
	AudioToggle.hasAudioRoutingOptions(callback);

	AudioToggle.setBluetoothScoOn(bool);
	AudioToggle.setSpeakerphoneOn(bool)
	AudioToggle.getOutputDevices(callback);
	
	AudioToggle.on(eventName, callback);
	AudioToggle.getAudioMode(callback);
	
	AudioToggle.getAudioSystem(callback);
	
	AudioToggle.isBluetoothScoOn(callback);
	AudioToggle.isSpeakerphoneOn(callback);
	AudioToggle.hasBuiltInEarpiecesuccessCb, errorCb)
	AudioToggle.hasBuiltInSpeakersuccessCb, errorCb);


## Example

    AudioToggle.setAudioMode(AudioToggle.SPEAKER);
    AudioToggle.setAudioMode(AudioToggle.EARPIECE);


    AudioToggle.on('devicechange', ({id, type, name, mode}) => {
			//mode - "speaker" | "earpiece" | "bluetooth" | "headphones"
		});

