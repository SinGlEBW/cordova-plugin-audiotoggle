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

import org.apache.cordova.LOG;
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
//  public static final String ACTION_SET_DEVICE = "setAudioDevice";
  public static final String ACTION_SET_REGISTER_LISTENER = "registerListener";
  public static final String ACTION_SET_UNREGISTER_LISTENER = "unregisterListener";

  private CallbackContext callbackContext = null;
  private Context contextApplication;
  private AudioTogglePlugin _this = this;
  private int delayTimeout = 600;
  private boolean currentSpeakerStatus = false;
  private boolean currentHeadphonesStatus = false;
  private boolean currentBluetoothStatus = false;
  private boolean currentEarpieceStatus = false;
  private String currentMode = "";

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
//      callbackContext.success(isSpeakerphoneOn().toString());
      _this.sendResult(new JSONObject().put("status", getCurrentStatusSpeaker()), true);
      return true;
    } else if (action.equals(ACTION_IS_BLUETOOTH_ON)) {
      _this.sendResult(new JSONObject().put("status", getCurrentStatusBluetooth()), true);
//      callbackContext.success(getStatusBluetooth().toString());

      return true;
    } else if (action.equals(ACTION_HAS_EARPIECE)) {
      callbackContext.success(hasBuiltInEarpiece().toString());
      return true;
    } else if (action.equals(ACTION_HAS_SPEAKER)) {
      callbackContext.success(hasBuiltInSpeaker().toString());
      return true;
    } else
//      if (action.equals(ACTION_GET_AUDIO_SYSTEM)) {
//      callbackContext.success(getAudioSystem().toString());
//      return true;
//    } else
//      if (action.equals(ACTION_SET_DEVICE)) {
//      setAudioDevice(args.getInt(0));
//      return true;
//    } else
      if (action.equals(ACTION_SET_REGISTER_LISTENER)) {
      this.callbackContext = callbackContext;
      registerListener();
      return true;
    } else if (action.equals(ACTION_SET_UNREGISTER_LISTENER)) {
      unregisterListener();
      return true;
    }

    callbackContext.error("Invalid action");
    return false;
  }

  public Boolean hasBuiltInEarpiece() {
    try {
      AudioDeviceInfo[] devices = getDevices();

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
    try {
      AudioDeviceInfo[] devices = getDevices();

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
    final AudioManager audioManager = getAudioManager();

    audioManager.setBluetoothScoOn(on);
    if (on) {
      audioManager.startBluetoothSco();
    } else {
      audioManager.stopBluetoothSco();
    }
  }

  public void setSpeakerphoneOn(boolean on) {
    final AudioManager audioManager = getAudioManager();

    if (Build.VERSION.SDK_INT >= 31) {
      ArrayList<Integer> targetTypes = new ArrayList<>();

      if (!on) {
        /*
         TYPE_BLE_HEADSET: 26, TYPE_BLE_SPEAKER: 27, TYPE_BLUETOOTH_SCO: 7, TYPE_BUILTIN_EARPIECE: 1,
         TYPE_BUILTIN_SPEAKER: 2, TYPE_USB_HEADSET: 22, TYPE_WIRED_HEADPHONES: 4, TYPE_WIRED_HEADSET: 3, TYPE_TELEPHONY: 18
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
      audioManager.setSpeakerphoneOn(on);
    }
  }


  private void setAudioTypeDevice(String type) {

    final AudioManager audioManager = getAudioManager();

    if (Build.VERSION.SDK_INT >= 31) {
      ArrayList<Integer> targetTypes = new ArrayList<>();
      /* TYPE_BLE_HEADSET: 26, TYPE_BLE_SPEAKER: 27, TYPE_BLUETOOTH_SCO: 7, TYPE_BUILTIN_EARPIECE: 1, TYPE_BUILTIN_SPEAKER: 2
       * TYPE_USB_HEADSET: 22, TYPE_WIRED_HEADPHONES: 4, TYPE_WIRED_HEADSET: 3, TYPE_TELEPHONY: 18*/

      switch (type) {
          case "bluetooth":
            targetTypes.add(AudioDeviceInfo.TYPE_BLUETOOTH_SCO);
            targetTypes.add(AudioDeviceInfo.TYPE_BLE_HEADSET);
            targetTypes.add(AudioDeviceInfo.TYPE_BLE_SPEAKER); break;
          case "headphones":
            targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADPHONES);
            targetTypes.add(AudioDeviceInfo.TYPE_WIRED_HEADSET);
            targetTypes.add(AudioDeviceInfo.TYPE_USB_HEADSET); break;
          case "speaker":
            targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER); break;
          case "earpiece":
            targetTypes.add(AudioDeviceInfo.TYPE_BUILTIN_EARPIECE); break;
        default: break;
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
    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
      Log.i("setAudioTypeDevice: ", "Код обработки для данной версии android не написан");
    }

  }

  private static void reset(AudioManager audioManager) {
    if (audioManager != null) {
      audioManager.stopBluetoothSco();
      audioManager.setBluetoothScoOn(false);
      audioManager.setSpeakerphoneOn(false);
      audioManager.setWiredHeadsetOn(false);
      audioManager.setMode(AudioManager.MODE_NORMAL);
    }
  }

  public static void connectEarpiece(AudioManager audioManager) {
    reset(audioManager);
    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
  }

  public static void connectSpeaker(AudioManager audioManager) {
    reset(audioManager);
    audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    audioManager.setSpeakerphoneOn(true);
  }

  public static void connectHeadphones(AudioManager audioManager) {
    reset(audioManager);
    //Наушники подкидываються автоматом, присваивая приотитет физически подключнным
    audioManager.setWiredHeadsetOn(true);
  }

  public static void connectBluetooth(AudioManager audioManager) {
    reset(audioManager);
  }


  public boolean setAudioMode(String mode) {
    final AudioManager audioManager =  this.getAudioManager();

      if (mode.equals("speaker")) {
        connectSpeaker(audioManager);

        setCurrentStatusSpeaker(true);
        setCurrentStatusEarpiece(false);
        setCurrentStatusBluetooth(false);
        setCurrentStatusHeadphones(false);
        this.currentMode = mode;

        return true;
      }
//внутренний динамик. Если воткнуты наушники то работать будут они
    if (mode.equals("earpiece")) {
      connectEarpiece(audioManager);

      setCurrentStatusBluetooth(false);
      setCurrentStatusEarpiece(true);
      setCurrentStatusHeadphones(false);
      setCurrentStatusSpeaker(false);
      this.currentMode = mode;

      return true;
    }
      if (mode.equals("bluetooth") && checkDeviceConnect("bluetooth")) {
        connectBluetooth(audioManager);

        setCurrentStatusSpeaker(false);
        setCurrentStatusBluetooth(true);
        setCurrentStatusEarpiece(false);
        setCurrentStatusHeadphones(false);
        this.currentMode = mode;

        return true;
      }
       if (mode.equals("headphones") && checkDeviceConnect("headphones")) {
         connectHeadphones(audioManager);

         setCurrentStatusSpeaker(false);
         setCurrentStatusEarpiece(false);
         setCurrentStatusBluetooth(false);
         setCurrentStatusHeadphones(true);
         this.currentMode = mode;

         return true;
       }


    if (Build.VERSION.SDK_INT >= 31) { setAudioTypeDevice(mode); }

    return false;
  }


  public String getAudioMode() {
    String mode = "speaker";
    AudioManager am = getAudioManager();
    int modeType = am.getMode();
    LOG.i("modeType: ", " ", modeType);
    if(AudioManager.MODE_NORMAL == modeType){
      if(checkDeviceConnect("bluetooth")){  mode = "bluetooth"; }
      if(checkDeviceConnect("headphones")){ mode = "headphones";  }
    }
    if(this.currentMode != mode) { this.currentMode = mode; };
    return mode;

//    String mode = "speaker";
//    AudioManager am = getAudioManager();
//
//    boolean is = am.isWiredHeadsetOn();
//    boolean isBluetoothConnect = checkDeviceConnect("bluetooth");
//    boolean isHeadphonesConnect = checkDeviceConnect("headphones");
//    boolean isSpeakerOn = isSpeakerphoneOn();
//
//  //boolean isEarpiece = getCurrentStatusEarpiece();
//
//    if(isBluetoothConnect && isBluetoothScoOn()){ mode = "bluetooth"; }
//    if(isHeadphonesConnect && (!isBluetoothScoOn())){ mode = "headphones"; }
//
//  //if(isEarpiece){ mode = "earpiece"; }
//
////    if(!this.currentMode.isEmpty()){ this.currentMode = mode; }

  }

  public JSONObject getOutputDevices() {

    JSONArray retdevs = new JSONArray();

    if (Build.MODEL.contains("Nokia 5.4")) { };

      try {
        AudioDeviceInfo[] devices = getDevices();
        for (AudioDeviceInfo dev : devices) {
          if (dev.isSink()) {
            if (
                    dev.getType() != AudioDeviceInfo.TYPE_TELEPHONY &&
                    dev.getType() != AudioDeviceInfo.TYPE_BLUETOOTH_SCO &&
                    dev.getType() != AudioDeviceInfo.TYPE_BUILTIN_EARPIECE &&
                    dev.getType() != AudioDeviceInfo.TYPE_BUILTIN_SPEAKER

            ) {
              // AudioDeviceInfo.TYPE_BLUETOOTH_A2DP - оставляем такой вариант отображения
              retdevs.put(this.getPayloadEvent(dev));
            }
          }
        }
        return new JSONObject().put("devices", retdevs);
      } catch (JSONException e) {
        // lets hope json-object keys are not null and not duplicated :)
      }
//
    return new JSONObject();
  }



  private String getCustomMode(int type) {
    String mode = "";
    if (type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER) { mode = "speaker"; }
    if (type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE) { mode = "earpiece"; }
    if (type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO | type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP) { mode = "bluetooth"; }
    if (type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES) { mode = "headphones"; }

    int[] arrayTypes = {
            AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
            AudioDeviceInfo.TYPE_BUILTIN_EARPIECE,
            AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
            AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
            AudioDeviceInfo.TYPE_WIRED_HEADPHONES
    };



    return mode;
  }



  public void registerListener() {
    Activity activity = this.cordova.getActivity();
     IntentFilter filter = new IntentFilter();
     filter.addAction(BluetoothDevice.ACTION_FOUND);
     filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
     filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
     filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
     filter.addAction(Intent.ACTION_HEADSET_PLUG);
     this.contextApplication.registerReceiver(BTReceiver, filter);

   /*
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    this.contextApplication.registerReceiver(BTReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
   */

    if (ContextCompat.checkSelfPermission(activity,
            Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.BLUETOOTH_CONNECT }, 2);
        return;
      } else {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
          setTimeout(() -> _this.sendResult(getOutputDevices(), true), delayTimeout);
        }
      }
    }
  }

  public void unregisterListener() {
    if (this.contextApplication != null) {
      this.contextApplication.unregisterReceiver(BTReceiver);
      this.contextApplication = null;
    }
  }

  private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

//      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//        // Discovery has found a device. Get the BluetoothDevice
//        // object and its info from the Intent.
//        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//        String deviceName = device.getName();
//        String deviceHardwareAddress = device.getAddress(); // MAC address
//        Log.i("Bluetooth", "got device " + deviceName);
//      }

      if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
        setCurrentStatusBluetooth(true);
        if(getCurrentStatusSpeaker()) setCurrentStatusSpeaker(false);
        if(getCurrentStatusHeadphones()) setCurrentStatusHeadphones(false);
        Toast.makeText(context, "Bluetooth connected", Toast.LENGTH_SHORT).show();
      } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
        setCurrentStatusBluetooth(false);
        Toast.makeText(context, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
      } else if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
        int state = intent.getIntExtra("state", -1);
        switch (state) {
          case 0:
            setCurrentStatusHeadphones(false);
            if(isBluetoothScoOn()) setCurrentStatusBluetooth(true);
            Toast.makeText(context, "Wired device disconnected", Toast.LENGTH_SHORT).show();
            break;
          case 1:
            setCurrentStatusHeadphones(true);
            if(getCurrentStatusBluetooth()) setCurrentStatusBluetooth(false);
            if(getCurrentStatusSpeaker()) setCurrentStatusSpeaker(false);
            Toast.makeText(context, "Wired device connected", Toast.LENGTH_SHORT).show();
            break;
          default:
            break;
        }
      }
      if(!getCurrentStatusBluetooth() && !getCurrentStatusHeadphones()){
        setCurrentStatusSpeaker(true);
      }
      setTimeout(() -> _this.sendResult(getOutputDevices(), true), delayTimeout);
    }
  };


  private boolean getCurrentStatusSpeaker() { return this.currentSpeakerStatus; }
  private void setCurrentStatusSpeaker(boolean is) { this.currentSpeakerStatus = is; }

  private boolean getCurrentStatusHeadphones() { return this.currentHeadphonesStatus; }
  private void setCurrentStatusHeadphones(boolean is) { this.currentHeadphonesStatus = is; }

  private boolean getCurrentStatusBluetooth() { return this.currentBluetoothStatus; }
  private void setCurrentStatusBluetooth(boolean is) { this.currentBluetoothStatus = is; }

  private boolean getCurrentStatusEarpiece() { return this.currentEarpieceStatus; }
  private void setCurrentStatusEarpiece(boolean is) { this.currentEarpieceStatus = is; }

  private Boolean isBluetoothScoOn() { return this.getAudioManager().isBluetoothScoOn() | this.getAudioManager().isBluetoothA2dpOn(); }
  private Boolean isSpeakerphoneOn() { return this.getAudioManager().isSpeakerphoneOn(); }


  private Boolean checkDeviceConnect(String deviceMode) {
    JSONObject obDevices = getOutputDevices();
    boolean isHeadphones = false;
    try {
      JSONArray devices = obDevices.getJSONArray("devices");
      for(int i = 0; i < devices.length(); i++){
        JSONObject dev = devices.getJSONObject(i);
        if(dev.has("mode")){
          String mode = dev.getString("mode");
          if(mode.equals(deviceMode)){ isHeadphones = true; break; }
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return isHeadphones;
  }

  private AudioManager getAudioManager() {
    final Context context = webView.getContext();
    AudioManager audioManager;

    if (Build.VERSION.SDK_INT >= 31) {
      audioManager = (AudioManager) context.getSystemService(AudioManager.class);
    } else {
      audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    return audioManager;
  }

  private AudioDeviceInfo[] getDevices() {
    AudioManager audioManager = getAudioManager();
    return audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
  }

  private JSONObject getPayloadEvent(AudioDeviceInfo dev) throws JSONException {
    int id = dev.getId();
    int type = dev.getType();
    String name = dev.getProductName().toString();
    String mode = this.getCustomMode(type);

    return (new JSONObject().put("deviceId", id).put("type", type).put("name", name).put("mode", mode));
  }

  private void sendResult(JSONObject info, boolean keepCallback) {
    if (this.callbackContext != null) {
      PluginResult result = new PluginResult(PluginResult.Status.OK, info);
      result.setKeepCallback(keepCallback);
      this.callbackContext.sendPluginResult(result);
    }
  }

  public static void setTimeout(Runnable runnable, int delay) {
    new Thread(() -> {
      try {
        Thread.sleep(delay);
        runnable.run();
      } catch (Exception e) {
        System.err.println(e);
      }
    }).start();
  }

  public String getDeviceName() {
    String manufacturer = Build.MANUFACTURER;
    String model = Build.MODEL;
    if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
      return capitalize(model);
    } else {
      return capitalize(manufacturer) + " " + model;
    }
  }
  private String capitalize(String s) {
    if (s == null || s.length() == 0) {
      return "";
    }
    char first = s.charAt(0);
    if (Character.isUpperCase(first)) {
      return s;
    } else {
      return Character.toUpperCase(first) + s.substring(1);
    }
  }


  private void setAudioDevice(int type) {
    final AudioManager audioManager = getAudioManager();

    if (Build.VERSION.SDK_INT >= 31) {
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

  private String getAudioSystem() {
    final AudioManager audioManager = this.getAudioManager();
    switch (audioManager.getRingerMode()) {
      case AudioManager.RINGER_MODE_SILENT: return "RINGER_MODE_SILENT";
      case AudioManager.RINGER_MODE_VIBRATE: return "RINGER_MODE_VIBRATE";
      case AudioManager.RINGER_MODE_NORMAL: return "RINGER_MODE_NORMAL";
    }
    return "unknown";
  }
}
