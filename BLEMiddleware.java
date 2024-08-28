package ble.solaqua;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BLEMiddleware {

    public boolean scanning;
    public boolean scanResult;
    private Integer scanCount;

    /**
     * Constructor for the BLEMiddleware class
     *
     * NOTE: An instance of the BLEMiddleware is needed **PER** Activity / Intent
     * @param activity              the activity / intent the BLEMiddleware is
     *                              being instantiated from
     */
    public BLEMiddleware(Activity activity) {
        // The activity stored per instance of BLEMiddleware
        // Build ScanFilter and add filter to the list
        // Scan settings and set run in background mode
        // Create a new bleManager instance
        // Get the Devices' BLE Manager Adapter
    }

    // Scan for devices
    @SuppressLint("MissingPermission")
    public boolean scanLeDevice() {

        Log.wtf("BLE SCAN", "List BONDED devices = " + bleAdapter.getBondedDevices());

        // ToDo: Remove the three dummy data mac addresses
        //       *** INTENDED FOR TESTING USE ONLY ***
        // deviceList.add("00:80:E1:27:C8:11");
        // deviceList.add("00:80:E1:27:C8:22");
        // deviceList.add("00:80:E1:27:C8:33");
        // ToDo: *** INTENDED FOR TESTING USE ONLY ***
        // Start scan and then stop after scan period
            // Start scan here
            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy' at 'HH:mm:ss");
            String currentDateAndTime = sdf.format(new Date());
            Log.i("BLE MIDDLEWARE SCAN", "Scan started at " + currentDateAndTime);
            });
            Thread timer = new Thread() {
                @Override
                public void run() {
                    Log.i("BLE MIDDLEWARE SCAN", "Timer started");
                    try {
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } else {
            // Stop ongoing scan
            scanning = false;
            Log.w("BLE MIDDLEWARE SCAN", "BLE SCAN is not running");
        }
    };

    // Stop BLE Scan
    @SuppressLint("MissingPermission")
    public boolean stopScanLeDevice() {
        //deviceList.clear();
        if (scanning) {
        }
    };

    // Device scan callback. Its called for ech device found during scan
    private ScanCallback leScanCallback =
            new ScanCallback() {
                    Log.i("BLE SCAN CALLBACK", "BLE leScanCallback. started");
                            Log.i("BLE SCAN CALLBACK", "BLE leScanCallback.onScanResult --> " + macAddress);
                    }
                    Log.i("BLE SCAN CALLBACK","Scan count = " + scanCount);
                    }
                }
            };

    public void getBLELocalizationPermissions(Activity activity) {
        if (activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.i("BLE MIDDLEWARE", "Request location services");
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void requestDeviceBluetoothEnabled(Activity activity) {
        if (!bleAdapter.isEnabled()) {
            Log.i("BLE MIDDLEWARE", "Request BLE services");
        }
    }
}
