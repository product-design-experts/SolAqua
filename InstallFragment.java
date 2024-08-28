package ble.solaqua;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.material.snackbar.Snackbar;

public class InstallFragment extends Fragment {

    View rootView;

    Button btnSave;

    ArrayList deviceList;
    String macAddress;
    TextView txtWarning;
    TextView txtInit1;
    TextView txtInit2;
    TextView txtInit3;
    TextView txtInit4;
    TextView txtInit5;
    ListView listTempMax;
    ArrayAdapter<String> listTempMaxAdapter;
    List<String> listTempMaxArr;
    Integer selectedMaxTemp;
    Boolean initDevice = false;

    public static AtomicBoolean isComplete = new AtomicBoolean(false);

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("DEVICE DETAILS", "onCreate started");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_install, container, false);

        // Find device
        Navigation navigation = Navigation.getInstance();
        deviceList = MainActivity.mBLE.deviceList;
        if (navigation.getText().toString().equals("Device1")) {
            macAddress = deviceList.get(0).toString();
        }
        if (navigation.getText().toString().equals("Device2")) {
            macAddress = deviceList.get(1).toString();
        }
        if (navigation.getText().toString().equals("Device3")) {
            macAddress = deviceList.get(2).toString();
        }
        if (navigation.getText().toString().equals("Device4")) {
            macAddress = deviceList.get(3).toString();
        }

        txtInit2 = (TextView) rootView.findViewById(R.id.textInit2);
        txtInit3 = (TextView) rootView.findViewById(R.id.textInit3);
        txtInit4 = (TextView) rootView.findViewById(R.id.textInit4);
        txtInit5 = (TextView) rootView.findViewById(R.id.textInit5);
        txtWarning = (TextView) rootView.findViewById(R.id.textWarning);
        txtInit2.setVisibility(View.INVISIBLE);
        txtInit3.setVisibility(View.INVISIBLE);
        txtInit4.setVisibility(View.INVISIBLE);
        txtInit5.setVisibility(View.INVISIBLE);
        txtWarning.setVisibility(View.INVISIBLE);
        return rootView;
    }

    @SuppressLint("CheckResult")
    public void onStart() {
        super.onStart();
        Log.d("DEVICE DETAILS", "onStart started");
        Navigation navigation = Navigation.getInstance();
        Log.wtf("DEVICE DETAILS", "onStart Thread = " + Thread.currentThread().getName());

        txtInit1 = (TextView) rootView.findViewById(R.id.textInit1);

        Animation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(500);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txtInit1.startAnimation(anim);

        // BLE Connect launches a new thread. So the Device Fragment runnables
        // execute on a different Thread than the Main Thread. Only once the QUEUE completes,
        // the Device Fragment can affect the Screen view.

        // Runnables
        Runnable connect = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS CONNECT", "connect Runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.connect(MainActivity.mBLE.bleAdapter, macAddress);
            }
        };
        Runnable disconnect = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS DISCONNECT", "connect Runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.disconnect();
            }
        };
        Runnable checkConnection = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "check connection runnable started. Thread = " + Thread.currentThread().getName());
                if (MainActivity.mBLEService.connectionState == 2) {
                    String bleGattActive = bleGattActive = String.valueOf(MainActivity.mBLEService.bleGatt);
                } else {
                    Log.i("DEVICE DETAILS", "Check connection Runnable ERROR - Device is NOT Connected");
                    Navigation navigation = Navigation.getInstance();
                    navigation.setText("ErrorConnect");
                    MainActivity.mQueue.jumpToEnd();
                    // ToDo: display connection lost
                }
                MainActivity.mQueue.completedCommand();
            }
        };
        Runnable checkService = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "check service Runnable started. Thread = " + Thread.currentThread().getName());
                // String service = "user_data" for temps
                // String service = "scan_params" for max temp
                MainActivity.mBLEService.checkService(macAddress, "user_data");
            }
        };
        Runnable readBattery = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "read battery runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.readBattery(macAddress);
            }
        };
        Runnable complete = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "complete Runnable started. Thread = " + Thread.currentThread().getName());
                isComplete.set(true);
                MainActivity.mQueue.clear();
            }
        };

        // Initialize display
        isComplete.set(false);
        MainActivity.mQueue.clear();
        MainActivity.mQueue.addCommand(connect);
        MainActivity.mQueue.addCommand(checkConnection);
        MainActivity.mQueue.addCommand(checkService);
        MainActivity.mQueue.addCommand(readBattery);
        MainActivity.mQueue.addCommand(complete);
        Log.w("DEVICE INFO", "COMMAND QUEUE start");
        MainActivity.mQueue.startCommand();

        while (!isComplete.get()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // Update display
        //updateDisplay();

        // Check if device is initializing
        if (MainActivity.mBLEService.batteryLevel >= 10 && MainActivity.mBLEService.batteryLevel <=100) {
        //if (MainActivity.mBLEService.batteryLevel != 0 && MainActivity.mBLEService.batteryLevel != null) {
            Log.wtf("INSTALL", "Batery does not read ZERO");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadFragment(new DetailsFragment());
                }
            }, 1500);
        } else {
            initDevice = true;
            Log.wtf("INSTALL","Batery reads ZERO");
            // Show install messaging
            txtInit1.setText("Initializing Device");
            txtInit2.setVisibility(View.VISIBLE);
            txtInit3.setVisibility(View.VISIBLE);
            txtInit4.setVisibility(View.VISIBLE);
            txtInit5.setVisibility(View.VISIBLE);
            // Wait 5 minutes
            int waitTime = 6 * 60 * 1000;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.w("WAIT","Wait 5 minutes");// Repeat until battery no longer reads 0 or some exit event
                    int passes = 1;
                    while (initDevice) {
                        Log.i("DETAILS", "Number of passes = " + passes);
                        isComplete.set(false);
                        if (MainActivity.mBLEService.connectionState != 2) {
                            MainActivity.mQueue.addCommand(connect);
                        }
                        MainActivity.mQueue.addCommand(checkConnection);
                        MainActivity.mQueue.addCommand(readBattery);
                        MainActivity.mQueue.addCommand(complete);
                        MainActivity.mQueue.startCommand();
                        while (!isComplete.get()) {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Log.i("DETAILS","Battery level reads --> " + MainActivity.mBLEService.batteryLevel);
                        //if (MainActivity.mBLEService.batteryLevel != 0) {
                        if (MainActivity.mBLEService.batteryLevel >= 10 && MainActivity.mBLEService.batteryLevel <=100) {
                            initDevice = false;
                            loadFragment(new DetailsFragment());
                            break;
                        }
                        passes = passes +1;
                        // Check if time expired
                        if (passes > 5) {
                            Log.wtf("INITIALIZATION", "FAILED due to Time Out");
                            txtWarning.setVisibility(View.VISIBLE);
                            txtWarning.setText("ERROR Installing Device");
                            break;
                        }
                        if (navigation.getText().equals("Status22")) {
                            Log.wtf("INITIALIZATION", "FAILED due to Status 22 Error");
                            txtWarning.setVisibility(View.VISIBLE);
                            txtWarning.setText("ERROR Installing Device");
                            break;
                        }
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, waitTime);



        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("DEVICE DETAILS","ON STOP method");
    }

    public void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.getClass().getName());
        fragmentTransaction.commit(); // save the changes
    }

}
