package ble.solaqua;

import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BLEService extends Service {

    private BluetoothGattCharacteristic BATTERY_LEVEL;
    private BluetoothGattCharacteristic TEMP_READINGS;
    private BluetoothGattCharacteristic DELTA_TEMP;
    private BluetoothGattCharacteristic MAX_TEMP;
    private BluetoothGattCharacteristic DWELL_TIME;
    private BluetoothGattCharacteristic PUMP_ON_TIME;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;
    public int connectionState = 0;
    public boolean connectionStateChanged = false;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();

    public String macAddress;
    public Integer batteryLevel;
    public Integer tempWater;
    public Integer tempHeater;
    public Integer tempMax;
    public Integer tempDelta;
    public Integer dwellTime;
    public Integer pumpTime;
    public String[] tempString = new String[10];

    public Integer tempReadCounter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Function to connect to a specified Bluetooth LE Device identified by macAddress
    @SuppressLint("MissingPermission")
    public boolean connect(BluetoothAdapter bleAdapter, final String macAddress) throws SecurityException {
        // Check connection
        Log.wtf("BLE SERVICES CONNECT", "Permission = " + BLUETOOTH_CONNECT);
        // Try connecting
        Log.w("BLE_SERVICES_CONNECT", "device = " + device + " MAC = " + macAddress);
        // Setting autoConnect to false is the preferred connectGatt approach
        Log.w("BLE_SERVICES_CONNECT", "Assigned bleGatt = " + bleGatt);
    }

    // Function to disconnect and close device (Delete Device - Information Fragment)
    @SuppressLint("MissingPermission")
    public boolean disconnect() throws SecurityException {
        // Verify connection exists
        Log.i("BLE_SERVICES_DISCONNECT","bleGatt = " + bleGatt);
        // Wait until connection is stopped or time expires
        Log.i("BLE SERVICES DISCONNECT","While loop exited -> waitTime = " + waitTime);
    }

    // Function to disconnect and close device (Delete Device - Information Fragment)
    @SuppressLint("MissingPermission")
    public boolean close() throws SecurityException {
        // Verify connection exists
        Log.i("BLE_SERVICES_CLOSE","bleGatt = " + bleGatt);
        // Wait until connection is stopped or time expires
        Integer waitTime = 0;
        Log.i("BLE SERVICES CLOSE","While loop exited -> waitTime = " + waitTime);
        // Check if disconnect occurs
        return true;
    }

    // Function to "Check" the connected Bluetooth LE Device
    public boolean checkService(String macAddress, String service) throws SecurityException {
        Navigation navigation = Navigation.getInstance();
        //Log.d("BLE_SERVICE_CHECK", "Service UUID = " + serviceUUID);
        Log.d("BLE_SERVICES_CHECK", "BLE Gatt Service = " + typeService);
        // TODO: Steps to follow if service is unknown. Need to develop restart queue
        // Service not found
        if (typeService == null) {
            Log.d("BLE_SERVICES_CHECK", "BLE Gatt Service is NULL");
            return false;
        }
        Log.d("BLE_SERVICES_CHECK", "bleGatt = " + bleGatt + "BT Gatt Service = " + typeService);
        switch (service) {
            case "user_data":
                break;
            case "scan_params":
                break;
        }
        return true;
    }

        /*private void readCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) throws SecurityException {
            Log.d("READ_CHARACTERISTIC", "Start reading" + characteristic);
            isReading = gatt.readCharacteristic(characteristic);
            while (isReading) {
                synchronized (readLock) {
                    try {
                        readLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/

        // Function automatically called when READ bleGatt charateristic
        @Override
        public void onCharacteristicRead (BluetoothGatt bleGatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("ON_CHARACTERISTIC_READ", "Start reading " + characteristic.getUuid().toString());
            switch (characteristic.getUuid().toString()) {
                    Log.i("ON CHARACTERISTIC READ", "Battery Level = " + batteryLevel);
                    Log.i("BLE READ","Size of Battery byte array --> " + battRawData.length);
                    // ToDo: determine offset with important data and add to tempDevice
                    Log.i("BLE_RAW_DATA","BLE Scan Record RAW data for BATTERY = " + hexValue);
                    break;
                    break;
                case SolAquaBLE.delta_temp:
                    Log.i("ON CHARACTERISTIC READ", "Delta Temp = " + tempDelta);
                    // TODO: Save Delta Temp Value
                    break;
                case SolAquaBLE.max_water_temp:
                    Log.i("ON CHARACTERISTIC READ", "Max Water Temp = " + tempMax);
                    break;
                case SolAquaBLE.dwell_time:
                    Log.i("ON CHARACTERISTIC READ", "Dwell Time = " + dwellTime);
                    // TODO: Save Dwell Time Value
                    break;
                case SolAquaBLE.pump_on_time:
                    Log.i("ON CHARACTERISTIC READ", "Pump On Time = " + pumpTime);
                    // TODO: Save Pump On Time Value
                    break;
            }
        }

        // Function automatically called when WRITE bleGatt characteristic
        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicWrite (BluetoothGatt bleGatt, BluetoothGattCharacteristic characteristic, int status) {
            Navigation navigation = Navigation.getInstance();
            if(status != BluetoothGatt.GATT_SUCCESS){
                Log.d("ON CHARACTERISTIC WRITE", "Failed write, retrying");
                navigation.setText("ErrorWriteTempMax");
            }
            Log.d("ON CHARACTERISTIC WRITE", "Write completed");
        }

        // TODO: Hardware to add descriptors for the characteristics to alert App of changes
        @Override
        public void onCharacteristicChanged(BluetoothGatt bleGatt, BluetoothGattCharacteristic characteristic) {
            // TODO: Update this Function!!
            Log.d("ON_CHAR_CHANGED", "\nCharacteristic changed: " + characteristic.getUuid().toString() + "\nNEW VALUE: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
        }
    };

    //  -----------------------------------------BLE Functions ---------------------------------------------------------

    /**
     * Function to Read battery level
     *
     * @return
     * @throws SecurityException
     */
    @SuppressLint("MissingPermission")
    public void readBattery(String macAddress) throws SecurityException {
        Navigation navigation = Navigation.getInstance();
        // Verify service is known
        if (userDataService == null) {
            Log.d("BLE_SERVICES_BATTERY",">>>> bleGatt(user data services is null) " + bleGatt);
        }
        BluetoothGattCharacteristic BATTERY_LEVEL = userDataService.getCharacteristic(UUID.fromString(SolAquaBLE.battery_level));
        this.macAddress = macAddress;
        if (BATTERY_LEVEL != null) {
            //Log.i("BLEServices - BATTERY", "battery_level read");
        } else {
            Log.i("BLEServices - BATTERY", "battery_level NULL");
        }
    }


    /**
     * Function to Read temperature levels
     *
     * @return
     * @throws SecurityException
     */
    @SuppressLint("MissingPermission")
    public void readTemps(String macAddress) throws SecurityException {
        Navigation navigation = Navigation.getInstance();
        // Verify service is known
        if (userDataService == null) {
            Log.d("BLE_SERVICES_TEMPS",">>>> bleGatt(user data services is null) " + bleGatt);
            tempWater = -100;
        }
        Log.wtf("BLE SERVICE TEMP","Number of descriptors = " + bluetoothGattDescriptorsList.size());
        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptorsList) {
            Log.d("BLE SERVICE TEMP","BLE Descriptors = " + bluetoothGattDescriptor.getUuid().toString());
        }
        //BluetoothGattDescriptor TEMP_FORMAT = TEMP_READINGS.getDescriptor(UUID.fromString(SolAquaBLE.temp_readings));
        if (TEMP_READINGS != null) {
            //Log.i("BLEServices - BATTERY", "battery_level read");
        } else {
            Log.i("BLEServices - TEMPS", "Temp Readings NULL");
            tempWater = -100;
        }
    }


    /**
     * Function to Read maximum temperature level
     *
     * @return
     * @throws SecurityException
     */
    @SuppressLint("MissingPermission")
    public void readTempMax(String macAddress) throws SecurityException {
        Navigation navigation = Navigation.getInstance();
        Log.d("BLE_SERVICES_TEMP MAX",">>>> User Data Service " + userDataService);
        // Verify service is known
        if (userDataService == null) {
            Log.d("BLE_SERVICES_TEMP MAX",">>>> bleGatt(user data services is null) " + bleGatt);
            tempMax = -100;
        }
        this.macAddress = macAddress;
        if (MAX_WATER_TEMP != null) {
            Log.i("BLEServices - TEMP MAX", "TEMP MAX read started");
        } else {
            Log.i("BLEServices - MAX TEMP", "max_water_temp NULL");
            navigation.setText("ErrorReadTempMax");
        }
    }

    /**
     * Function to set max temperature
     *
     * @param macAddress
     * @param tempMax
     * @return
     * @throws SecurityException
     */
    @SuppressLint("MissingPermission")
    public void setTempMax(String macAddress, Integer tempMax) throws SecurityException {
        Navigation navigation = Navigation.getInstance();
        BluetoothGattService scanParamsService = bleGatt.getService(UUID.fromString(SolAquaBLE.scan_params));
        // Verify service is known
        if (scanParamsService == null) {
            Log.d("BLE_SERVICES_ALARM", ">>>> bleGatt(scan params services is null) " + bleGatt);
        }

        // Write to device
    }

    public boolean permissionCheck(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    class LocalBinder extends Binder {
        public BLEService getService() {
        }
    }
}