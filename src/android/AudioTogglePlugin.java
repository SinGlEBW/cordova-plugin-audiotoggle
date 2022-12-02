package ru.cordova.android.audiotoggle;

import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioDeviceInfo;

import java.util.ArrayList;
import java.util.List;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.os.Build;

/*------------------------------------------ */
import org.apache.cordova.PluginResult;
import android.content.IntentFilter;
import android.widget.Toast;
import android.content.Intent;
import android.media.AudioDeviceInfo;
import android.content.BroadcastReceiver;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.app.Activity;
/*------------------------------------------ */

public class AudioTogglePlugin extends CordovaPlugin {
  public static final String ACTION_SET_AUDIO_MODE = "setAudioMode";
  public static final String ACTION_SET_BLUETOOTH_ON = "setBluetoothScoOn";
  public static final String ACTION_SET_SPEAKER_ON = "setSpeakerphoneOn";
  public static final String ACTION_GET_OUTPUT_DEVICES = "getOutputDevices";
  public static final String ACTION_GET_AUDIO_MODE = "getAudioMode";
  public static final String ACTION_GET_AUDIO_SYSTEM = "getAudioSystem";
  public static final String ACTION_IS_SPEAKER_ON = "isSpeakerphoneOn";
  public static final String ACTION_IS_BLUETOOTH_ON = "isBluetoothScoOn";
  public static final String ACTION_HAS_EARPIECE = "hasBuiltInEarpiece";
  public static final String ACTION_HAS_SPEAKER = "hasBuiltInSpeaker";
  public static final String ACTION_SET_DEVICE = "setAudioDevice";
  public static final String ACTION_SET_REGISTER_LISTENER = "registerListener";
  private CallbackContext callbackContext = null;
  private Context contextApplication;
  private AudioTogglePlugin _this = this;


  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    this.contextApplication = cordova.getActivity().getApplicationContext();
    if (action.equals(ACTION_SET_AUDIO_MODE)) {
      if (!setAudioMode(args.getString(0))) {
        callbackContext.error("Invalid audio mode");
        return false;
      }

      return true;
    } else if (action.equals(ACTION_SET_BLUETOOTH_ON)) {
      setBluetoothScoOn(args.getBoolean(0));
      return true;
    } else if (action.equals(ACTION_SET_SPEAKER_ON)) {
      setSpeakerphoneOn(args.getBoolean(0));
      return true;
    } else if (action.equals(ACTION_GET_OUTPUT_DEVICES)) {
      callbackContext.success(getOutputDevices());
      return true;
    } else if (action.equals(ACTION_GET_AUDIO_MODE)) {
      callbackContext.success(getAudioMode());
      return true;
    } else if (action.equals(ACTION_IS_SPEAKER_ON)) {
      callbackContext.success(isSpeakerphoneOn().toString());
      return true;
    } else if (action.equals(ACTION_IS_BLUETOOTH_ON)) {
      callbackContext.success(isBluetoothScoOn().toString());
      return true;
    } else if (action.equals(ACTION_HAS_EARPIECE)) {
      callbackContext.success(hasBuiltInEarpiece().toString());
      return true;
    } else if (action.equals(ACTION_HAS_SPEAKER)) {
      callbackContext.success(hasBuiltInSpeaker().toString());
      return true;
    } else if (action.equals(ACTION_GET_AUDIO_SYSTEM)) {
      callbackContext.success(getAudioSystem().toString());
      return true;
    } else if (action.equals(ACTION_SET_DEVICE)) {
      setAudioDevice(args.getInt(0));
      return true;
    } else if (action.equals(ACTION_SET_REGISTER_LISTENER)) {
      this.callbackContext = callbackContext;
      registerListener();
      return true;
    }

    callbackContext.error("Invalid action");
    return false;
  }

  public Boolean hasBuiltInEarpiece() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    try {
      AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

      JSONArray retdevs = new JSONArray();
      for (AudioDeviceInfo dev : devices) {
        if (dev.isSink()) {
          if (dev.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
            return true;
          }
        }
      }

      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public Boolean hasBuiltInSpeaker() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    try {
      AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);

      JSONArray retdevs = new JSONArray();
      for (AudioDeviceInfo dev : devices) {
        if (dev.isSink()) {
          if (dev.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
            return true;
          }
        }
      }

      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public void setBluetoothScoOn(boolean on) {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    audioManager.setBluetoothScoOn(on);
    if (on) {
      audioManager.startBluetoothSco();
    } else {
      audioManager.stopBluetoothSco();
    }
  }

  public void setSpeakerphoneOn(boolean on) {
    final Context context = webView.getContext();

    if (Build.VERSION.SDK_INT >= 31) {

      final AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
      // audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

      // audioManager.setSpeakerphoneOn(on);
      // Get an AudioManager instance
      ArrayList<Integer> targetTypes = new ArrayList<>();
      // add types according to needs, may be few in order of importance
      if (!on) {
        /**
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
        targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
        targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADSET);
        targetTypes.add(AudioDeviceInfo.TYPE_USB_HEADSET);
        targetTypes.add(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
        targetTypes.add(AudioDeviceInfo.TYPE_BLE_HEADSET);
        targetTypes.add(AudioDeviceInfo.TYPE_BLE_SPEAKER);
        targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
        Log.i("AUDIO_MANAGER", "EARPIECE SETTING");
      } else { // play out loud
        targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
        targetTypes.add(AudioDeviceInfo.TYPE_BLE_SPEAKER);
        Log.i("AUDIO_MANAGER", "SPEAKER SETTING");
      }

      Boolean result = null;
      List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
      outer: for (Integer targetType : targetTypes) {
        for (AudioDeviceInfo device : devices) {
          int type = device.getType();
          Log.i("AUDIO_MANAGER", "getType " + type);
          if (device.getType() == targetType) {
            if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
              audioManager.setBluetoothScoOn(true);
              audioManager.startBluetoothSco();
            }
            result = audioManager.setCommunicationDevice(device);
            Log.i("AUDIO_MANAGER", "setCommunicationDevice type:" + targetType + " result:" + result);
            if (result)
              break outer;
          }
        }
      }

      if (result == null) {
        Log.i("AUDIO_MANAGER", "setCommunicationDevice targetType NOT FOUND!!");
      }
    } else {
      final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      audioManager.setSpeakerphoneOn(on);
    }
  }

  public void setAudioDevice(int type) {
    final Context context = webView.getContext();

    if (Build.VERSION.SDK_INT >= 31) {

      final AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
      // audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

      // audioManager.setSpeakerphoneOn(on);
      // Get an AudioManager instance
      ArrayList<Integer> targetTypes = new ArrayList<>();
      // add types according to needs, may be few in order of importance
      /**
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
      targetTypes.add(type);

      Boolean result = null;
      List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
      outer: for (Integer targetType : targetTypes) {
        for (AudioDeviceInfo device : devices) {
          Log.i("AUDIO_MANAGER", "getType " + type);
          if (device.getType() == targetType) {
            result = audioManager.setCommunicationDevice(device);
            Log.i("AUDIO_MANAGER", "setCommunicationDevice type:" + targetType + " result:" + result);
            if (result)
              break outer;
          }
        }
      }

      if (result == null) {
        Log.i("AUDIO_MANAGER", "setCommunicationDevice targetType NOT FOUND!!");
      }
    }
  }

  public void setAudioTypeDevice(String type) {
    final Context context = webView.getContext();
    if (Build.VERSION.SDK_INT >= 31) {

      final AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
      // audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

      // audioManager.setSpeakerphoneOn(on);
      // Get an AudioManager instance
      ArrayList<Integer> targetTypes = new ArrayList<>();
      // add types according to needs, may be few in order of importance
      /**
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
      switch (type) {
        case "bluetooth":
          audioManager.setBluetoothScoOn(true);
          audioManager.startBluetoothSco();
          targetTypes.add(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
          targetTypes.add(AudioDeviceInfo.TYPE_BLE_HEADSET);
          targetTypes.add(AudioDeviceInfo.TYPE_BLE_SPEAKER);
          break;
        case "headphones":
          targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
          targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADSET);
          targetTypes.add(AudioDeviceInfo.TYPE_USB_HEADSET);
          break;
        case "speaker":
          targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
        case "earpiece":
          targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE);
          break;
        default:
          break;
      }

      Boolean result = null;
      List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
      outer: for (Integer targetType : targetTypes) {
        for (AudioDeviceInfo device : devices) {
          Log.i("AUDIO_MANAGER", "getType " + type);
          if (device.getType() == targetType) {
            result = audioManager.setCommunicationDevice(device);
            Log.i("AUDIO_MANAGER", "setCommunicationDevice type:" + targetType + " result:" + result);
            if (result)
              break outer;
          }
        }
      }

      if (result == null) {
        Log.i("AUDIO_MANAGER", "setCommunicationDevice targetType NOT FOUND!!");
      }
    }
  }

  public boolean setAudioMode(String mode) {

    final Context context = webView.getContext();
    if (Build.VERSION.SDK_INT >= 31) {

      final AudioManager audioManager = (AudioManager) context.getSystemService(AudioManager.class);
      if (mode.equals("bluetooth")) {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        // audioManager.setSpeakerphoneOn(false);
        setAudioTypeDevice("bluetooth");
        return true;
      } else if (mode.equals("incall")) {
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        // audioManager.stopBluetoothSco();
        // audioManager.setBluetoothScoOn(false);
        // audioManager.setSpeakerphoneOn(false);
        setAudioTypeDevice("earpiece");
        return true;
      } else if (mode.equals("earpiece")) {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        // audioManager.stopBluetoothSco();
        // audioManager.setBluetoothScoOn(false);
        setSpeakerphoneOn(false);
        return true;
      } else if (mode.equals("speaker")) {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        // audioManager.stopBluetoothSco();
        // audioManager.setBluetoothScoOn(false);
        setSpeakerphoneOn(true);
        return true;
      } else if (mode.equals("ringtone")) {
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        setSpeakerphoneOn(false);
        return true;
      } else if (mode.equals("normal")) {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        setSpeakerphoneOn(false);
        return true;
      }

    } else {
      final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
      if (mode.equals("bluetooth")) {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();
        return true;
      } else if (mode.equals("earpiece")) {
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(false);
        return true;
      } else if (mode.equals("speaker")) {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(true);
        return true;
      } else if (mode.equals("ringtone")) {
        audioManager.setMode(AudioManager.MODE_RINGTONE);
        audioManager.setSpeakerphoneOn(false);
        return true;
      } else if (mode.equals("normal")) {
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
        return true;
      } else if (mode.equals("incall")) {
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(false);

        return true;
      }
    }

    return false;
  }

  public JSONObject getOutputDevices() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    JSONArray retdevs = new JSONArray();
    

    if (Build.VERSION.SDK_INT >= 31) {

      try {
        List<AudioDeviceInfo> devices = audioManager.getAvailableCommunicationDevices();
     
        
        for (AudioDeviceInfo dev : devices) {

       
          retdevs.put(
            new JSONObject()
            .put("id", dev.getId())
            .put("type", dev.getType())
            .put("name",dev.getProductName().toString())
            .put("mode", this.getCustomMode(dev))
          );
          
        }

        return new JSONObject().put("devices", retdevs);
      } catch (JSONException e) {
        // lets hope json-object keys are not null and not duplicated :)
      }

    } else {

      try {
        AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        

        for (AudioDeviceInfo dev : devices) {
    
           
      

          if (dev.isSink()) {
            if (dev.getType() != AudioDeviceInfo.TYPE_BUILTIN_EARPIECE && dev.getType() != AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
              retdevs.put(
                new JSONObject()
                .put("id", dev.getId())
                .put("type", dev.getType())
                .put("name",dev.getProductName().toString())
                .put("mode", this.getCustomMode(dev))
              );
            }
          }
        }

        return new JSONObject().put("devices", retdevs);
      } catch (JSONException e) {
        // lets hope json-object keys are not null and not duplicated :)
      }

    }

    return new JSONObject();
  }

  public String getAudioMode() {
    final Context context = webView.getContext();
    AudioManager audioManager;

    if (Build.VERSION.SDK_INT >= 31) {
      audioManager = (AudioManager) context.getSystemService(AudioManager.class);
    }else{
      audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    int mode = audioManager.getMode();
    boolean isBluetoothScoOn = audioManager.isBluetoothScoOn();
    boolean isSpeakerphoneOn = audioManager.isSpeakerphoneOn();

    if (mode == AudioManager.MODE_IN_COMMUNICATION && isBluetoothScoOn) {
      return "bluetooth";
    } else if (mode == AudioManager.MODE_IN_COMMUNICATION && !isBluetoothScoOn && !isSpeakerphoneOn) {
      return "earpiece";
    } else if (mode == AudioManager.MODE_IN_COMMUNICATION && !isBluetoothScoOn && isSpeakerphoneOn) {
      return "speaker";
    } else if (mode == AudioManager.MODE_RINGTONE && !isSpeakerphoneOn) {
      return "ringtone";
    } else if (mode == AudioManager.MODE_NORMAL && !isSpeakerphoneOn) {
      return "normal";
    }

    return "normal";
  }

  private String getCustomMode(AudioDeviceInfo dev) {
    String mode = "";
    if (dev.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) {
      mode = "speaker";
    }
    if (dev.getType() == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) {
      mode = "earpiece";
    }
    if (dev.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
      mode = "bluetooth";
    }
    if (dev.getType() == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) {
      mode = "headphones";
    }
    return mode;
  
  }
  public String getAudioSystem() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    switch (audioManager.getRingerMode()) {
      case AudioManager.RINGER_MODE_SILENT:
        return "RINGER_MODE_SILENT";
      case AudioManager.RINGER_MODE_VIBRATE:
        return "RINGER_MODE_VIBRATE";
      case AudioManager.RINGER_MODE_NORMAL:
        return "RINGER_MODE_NORMAL";
    }

    return "unknown";
  }

  public Boolean isBluetoothScoOn() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    return audioManager.isBluetoothScoOn();
  }

  public Boolean isSpeakerphoneOn() {
    final Context context = webView.getContext();
    final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    return audioManager.isSpeakerphoneOn();
  }

  public void registerListener() {
   
    Activity activity = this.cordova.getActivity();
    // IntentFilter filter = new IntentFilter();
    // filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    // filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    // filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    // filter.addAction(Intent.ACTION_HEADSET_PLUG);
    // this.contextApplication.registerReceiver(BTReceiver, filter);

    
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            return;
        }
    }


  }

  private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        // Discovery has found a device. Get the BluetoothDevice
        // object and its info from the Intent.
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
        Log.i("Bluetooth", "got device " + deviceName);
      }

      if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
        Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_SHORT).show();
      } else 
      if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
        Toast.makeText(context, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
      } else 
      if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
        int state = intent.getIntExtra("state", -1);
        switch (state) {
          case 0:
            Toast.makeText(context, "Wired device disconnected", Toast.LENGTH_SHORT).show();
            break;
          case 1:
            Toast.makeText(context, "Wired device connected", Toast.LENGTH_SHORT).show();
            break;
          default:
            break;
        }
      }
   
      setTimeout(() -> _this.sendResult(getOutputDevices(), true), 500);
      
    }
  };



  private void sendResult(JSONObject info, boolean keepCallback) {
    if (this.callbackContext != null) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, info);
      result.setKeepCallback(keepCallback);
      this.callbackContext.sendPluginResult(result);
    }
  }
  public static void setTimeout(Runnable runnable, int delay){
    new Thread(() -> {
        try {
            Thread.sleep(delay);
            runnable.run();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }).start();
}

}
