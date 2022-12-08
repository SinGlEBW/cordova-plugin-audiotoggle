## AudioToggle

Cordova plugin for switching between speaker and earpiece when playing audio.

    cordova plugin add https://github.com/SinGlEBW/cordova-plugin-audiotoggle
    
### Supported Platforms

- Android
- iOS

### Usage

To set the current audio mode, use the `setAudioMode` method:
#### Mode
		/*Android and iOS*/
		AudioToggle.SPEAKER = 'speaker';
		AudioToggle.EARPIECE = 'earpiece';
		/*Android*/
		AudioToggle.BLUETOOTH = 'bluetooth';
		/* iOS */
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
	
	AudioToggle.displayIOSAudioRoutingComponent();
	AudioToggle.hasAudioRoutingOptions(callback);

	AudioToggle.getOutputDevices(callback);
	
	AudioToggle.on("devicechange", callback);
	AudioToggle.getAudioMode(callback);
	AudioToggle.hasBuiltInEarpiece(callback);
	AudioToggle.hasBuiltInSpeaker(callback);
	
#### Example

    AudioToggle.setAudioMode(AudioToggle.SPEAKER);
    AudioToggle.setAudioMode(AudioToggle.EARPIECE);


    AudioToggle.on('devicechange', ({devices}) => {

			let currentModeOn = AudioToggle.getAudioMode();

			devices.forEach((device) => {
				let { deviceId, type, name, mode } = device;
			});

			//mode - "speaker" | "bluetooth" | "headphones"
		});

