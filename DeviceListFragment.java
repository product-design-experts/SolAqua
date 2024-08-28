package ble.solaqua;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

public class DeviceListFragment extends Fragment {

    public static View rootView;

    Integer deviceCount;
    TextView txtTitle;
    TextView txtSearching;
    TextView txtReset;
    TextView txtReset2;
    TextView txtInit1;
    TextView txtInit2;
    TextView txtInit3;
    TextView txtInit4;
    TextView txtInit5;
    Button btnReScan;
    Button btnDevice1;
    Button btnDevice2;
    Button btnDevice3;
    Button btnDevice4;
    Button btnReturn;
    Button btnHome;
    ImageView imageDevice1;
    ImageView imageDevice2;
    ImageView imageDevice3;
    ImageView imageDevice4;
    String macAddress;
    BottomNavigationView btmNavBar;
    ProgressBar spinnerSearching;
    Runnable scan;
    Runnable connect;
    Runnable checkConnection;
    Runnable stopScan;
    Runnable getList;
    Runnable scanResults;
    Runnable checkService;
    Runnable readBattery;
    String serviceCheck;

    ArrayList deviceList;

    public static AtomicBoolean isComplete = new AtomicBoolean(false);

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HOME_FRAGMENT", "onCreateView Activity started");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_devicelist, container, false);

        Navigation navigation = Navigation.getInstance();
        SelectedDevice selectedDevice = SelectedDevice.getInstance();
        deviceList = MainActivity.mBLE.deviceList;

        Handler mHandler = new Handler(Looper.getMainLooper());
        Log.i("DEVICE LIST", "Main Executor --> " + mHandler);

        txtTitle = (TextView) rootView.findViewById(R.id.titleHome);
        txtSearching = (TextView) rootView.findViewById(R.id.textSearching);
        //txtInit1 = (TextView) rootView.findViewById(R.id.textInit1);
        txtInit2 = (TextView) rootView.findViewById(R.id.textInit2);
        txtInit3 = (TextView) rootView.findViewById(R.id.textInit3);
        txtInit4 = (TextView) rootView.findViewById(R.id.textInit4);
        txtInit5 = (TextView) rootView.findViewById(R.id.textInit5);
        spinnerSearching = (ProgressBar) rootView.findViewById(R.id.spinnerSearching);
        txtReset = (TextView) rootView.findViewById(R.id.textReset);
        txtReset2 = (TextView) rootView.findViewById(R.id.textReset2);
        btnReScan = (Button) rootView.findViewById(R.id.buttonReScan);
        btmNavBar = (BottomNavigationView) rootView.findViewById(R.id.bottomNavigationView);
        // Hide optional messages and reScan button
        //txtInit1.setVisibility(View.GONE);
        txtInit2.setVisibility(View.GONE);
        txtInit3.setVisibility(View.GONE);
        txtInit4.setVisibility(View.GONE);
        txtInit5.setVisibility(View.GONE);
        txtReset.setVisibility(View.GONE);
        txtReset2.setVisibility(View.GONE);
        btnReScan.setClickable(false);
        btnReScan.setVisibility(View.INVISIBLE);

        btnDevice1 = (Button) rootView.findViewById(R.id.buttonDevice1);
        imageDevice1 = (ImageView) rootView.findViewById(R.id.backgroundDevice1);
        btnDevice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("HOME FRAGMENT", "Clicked on Device1 button");
                navigation.setText("Device1");
                macAddress = deviceList.get(0).toString();
                selectedDevice.setText(macAddress.substring(macAddress.length() - 2));
                loadFragment(new InstallFragment());
            }
        });

        btnDevice2 = (Button) rootView.findViewById(R.id.buttonDevice2);
        imageDevice2 = (ImageView) rootView.findViewById(R.id.backgroundDevice2);
        btnDevice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("HOME FRAGMENT", "Clicked on Device2 button");
                navigation.setText("Device2");
                macAddress = deviceList.get(1).toString();
                selectedDevice.setText(macAddress.substring(macAddress.length() - 2));
                loadFragment(new InstallFragment());
            }
        });

        btnDevice3 = (Button) rootView.findViewById(R.id.buttonDevice3);
        imageDevice3 = (ImageView) rootView.findViewById(R.id.backgroundDevice3);
        btnDevice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("HOME FRAGMENT", "Clicked on Device3 button");
                navigation.setText("Device3");
                macAddress = deviceList.get(2).toString();
                selectedDevice.setText(macAddress.substring(macAddress.length() - 2));
                loadFragment(new InstallFragment());
            }
        });

        btnDevice4 = (Button) rootView.findViewById(R.id.buttonDevice4);
        imageDevice4 = (ImageView) rootView.findViewById(R.id.backgroundDevice4);
        btnDevice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("HOME FRAGMENT", "Clicked on Device4 button");
                navigation.setText("Device4");
                macAddress = deviceList.get(3).toString();
                selectedDevice.setText(macAddress.substring(macAddress.length() - 2));
                loadFragment(new InstallFragment());
            }
        });

        resetScreen();

        // Runnables
        scan = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE LIST", "BLE Scan runnable started");
                MainActivity.mBLE.scanLeDevice();
                MainActivity.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetScreen();
                        //spinnerSearching.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        stopScan = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE LIST", "BLE STOP Scan runnable started");
                MainActivity.mBLE.stopScanLeDevice();
            }
        };
        getList = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE LIST", "Get deviceList and check Scan Result runnable started");
                Log.i("DEVICE LIST", "Update Observable Thread = " + Thread.currentThread().getName());
                deviceList = MainActivity.mBLE.deviceList;
                Log.i("DEVICE LIST", "Device list = " + deviceList);
                MainActivity.mQueue.completedCommand();
            }
        };
        scanResults = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE LIST", "scanResults runnable started --> Thread = " + Thread.currentThread().getName());
                deviceCount = MainActivity.mBLE.deviceList.size();
                MainActivity.mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //spinnerSearching.setVisibility(View.INVISIBLE);
                        Log.i("DEVICE LIST", "scanResults UI Thread Device Count = " + deviceCount);
                        // Set up display based on number of devices
                        displayDevices(deviceCount);
                        // Adjust position of reset messages
                        adjustLayout(deviceCount);
                        // Create message
                        messageCreate();
                        // display bottom bar
                        btmNavBar.setVisibility(View.VISIBLE);
                    }
                });
                MainActivity.mQueue.clear();
            }
        };

        checkService = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "check service Runnable started. Thread = " + Thread.currentThread().getName());
                // String service = "user_data" for temps
                // String service = "scan_params" for max temp
                MainActivity.mBLEService.checkService(macAddress, serviceCheck);
            }
        };
        readBattery = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "read battery runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.readBattery(macAddress);
            }
        };

        // Set listeners for the bottom navbar
        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_return) {
                    // ToDo: Return to previous screen
                    Log.i("NAVIGATION","RETURN button pressed");
                    loadFragment(new MenuFragment());
                } else if (id == R.id.navigation_refresh) {
                    // ToDo: Refresh current screen
                    resetScreen();
                    Log.i("HOME FRAGMENT","ReScan button pressed");
                    txtSearching.setVisibility(View.VISIBLE);
                    txtSearching.setText("Searching for solaqua devices...");
                    txtReset2.setVisibility(View.GONE);
                    btnReScan.setVisibility(View.GONE);
                    btnReScan.setClickable(false);
                    MainActivity.mQueue.clear();
                    if (MainActivity.mBLE.scanning) {
                        MainActivity.mQueue.addCommand(stopScan);
                    }
                    MainActivity.mQueue.addCommand(scan);
                    MainActivity.mQueue.addCommand(getList);
                    MainActivity.mQueue.addCommand(scanResults);
                    MainActivity.mQueue.startCommand();
                } else if (id == R.id.navigation_home) {
                    // ToDo: Return to menu screen
                    Log.i("NAVIGATION","HOME button pressed");
                    loadFragment(new MenuFragment());
                }
                return false;
            }
        });

        return rootView;
    }

    @SuppressLint({"ResourceAsColor", "CheckResult"})
    @Override
    public void onStart() {
        super.onStart();

        Log.d("HOME FRAGMENT", "onStart Activity started");

        // Don't display btmNavBar if deviceList is 0
        if (deviceCount == null || deviceCount == 0) {
            btmNavBar.setVisibility(View.INVISIBLE);
        } else {
            btmNavBar.setVisibility(View.VISIBLE);
        }

        btnDevice1 = (Button) rootView.findViewById(R.id.buttonDevice1);
        imageDevice1 = (ImageView) rootView.findViewById(R.id.backgroundDevice1);
        btnDevice2 = (Button) rootView.findViewById(R.id.buttonDevice2);
        imageDevice2 = (ImageView) rootView.findViewById(R.id.backgroundDevice2);
        btnDevice3 = (Button) rootView.findViewById(R.id.buttonDevice3);
        imageDevice3 = (ImageView) rootView.findViewById(R.id.backgroundDevice3);
        btnDevice4 = (Button) rootView.findViewById(R.id.buttonDevice4);
        imageDevice4 = (ImageView) rootView.findViewById(R.id.backgroundDevice4);

        txtReset.setVisibility(View.GONE);
        txtReset2.setVisibility(View.GONE);

        // BLE Scan does not launch a new thread. So the Home Fragment runnables
        // execute on the Main Thread and can directly affect the Screen view.

        // Execute on Start activity
        isComplete.set(false);
        Log.i("DEVICE LIST","BLE SCAN connectionState = " + MainActivity.mBLEService.connectionState);

        if (MainActivity.mBLEService.connectionState == 2) {
            MainActivity.mBLEService.disconnect();
            while (MainActivity.mBLEService.connectionState == 2) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Log.i("DEVICES", "BLE SCAN paused until devices disconnected");
        }
        if (MainActivity.mBLE.scanning) {
            MainActivity.mQueue.addCommand(stopScan);
        }
        MainActivity.mQueue.addCommand(scan);
        MainActivity.mQueue.addCommand(getList);
        MainActivity.mQueue.addCommand(scanResults);
        MainActivity.mQueue.startCommand();


        btnReScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("HOME FRAGMENT","ReScan button pressed");
                txtSearching.setVisibility(View.VISIBLE);
                txtSearching.setText("Searching for solaqua devices...");
                txtReset2.setVisibility(View.GONE);
                btnReScan.setVisibility(View.GONE);
                btnReScan.setClickable(false);
                resetScreen();
                MainActivity.mQueue.clear();
                if (MainActivity.mBLE.scanning) {
                    MainActivity.mQueue.addCommand(stopScan);
                }
                MainActivity.mQueue.addCommand(scan);
                MainActivity.mQueue.addCommand(getList);
                MainActivity.mQueue.addCommand(scanResults);
                MainActivity.mQueue.startCommand();
                txtReset.setVisibility(View.VISIBLE);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String currentTime = sdf.format(new Date());
                txtReset.setText("Scan completed at " + currentTime);
            }
        });
    }

    private void resetScreen() {
        btnDevice1.setClickable(false);
        btnDevice1.setText("");
        imageDevice1.setVisibility(View.INVISIBLE);
        btnDevice2.setClickable(false);
        btnDevice2.setText("");
        imageDevice2.setVisibility(View.INVISIBLE);
        btnDevice3.setClickable(false);
        btnDevice3.setText("");
        imageDevice3.setVisibility(View.INVISIBLE);
        btnDevice4.setClickable(false);
        btnDevice4.setText("");
        imageDevice4.setVisibility(View.INVISIBLE);
        txtReset.setVisibility(View.INVISIBLE);
        txtReset2.setVisibility(View.INVISIBLE);
        btnReScan.setVisibility(View.INVISIBLE);
    }

    private void installScreen() {
        txtTitle.setText("Accessing Device");
        txtInit2.setVisibility(View.VISIBLE);
        txtInit3.setVisibility(View.VISIBLE);
        txtInit4.setVisibility(View.VISIBLE);
        txtInit5.setVisibility(View.VISIBLE);
        spinnerSearching.setVisibility(View.VISIBLE);
        btmNavBar.setVisibility(View.INVISIBLE);
    }

    private void displayDevices(Integer deviceCount) {
        txtSearching.clearAnimation();
        if (deviceCount == 0) {
            Log.i("DEVICE LIST", "Device Count is 0 --> " + deviceCount);
            txtSearching.setText("No solaqua devices found");
        }
        if (deviceCount >= 1) {
            txtSearching.setVisibility(View.INVISIBLE);
            btnReScan.setVisibility(View.GONE);
            btnDevice1.setClickable(true);
            String deviceString = MainActivity.mBLE.deviceList.get(0).toString();
            btnDevice1.setText("solaqua-" + deviceString.substring(deviceString.length() - 2));
            imageDevice1.setVisibility(View.VISIBLE);
        }
        if (deviceCount >= 2) {
            btnDevice2.setClickable(true);
            String deviceString = MainActivity.mBLE.deviceList.get(1).toString();
            btnDevice2.setText("solaqua-" + deviceString.substring(deviceString.length() - 2));
            imageDevice2.setVisibility(View.VISIBLE);
        }
        if (deviceCount >= 3) {
            btnDevice3.setClickable(true);
            String deviceString = MainActivity.mBLE.deviceList.get(2).toString();
            btnDevice3.setText("solaqua-" + deviceString.substring(deviceString.length() - 2));
            imageDevice3.setVisibility(View.VISIBLE);
        }
        if (deviceCount == 4) {
            btnDevice4.setClickable(true);
            String deviceString = MainActivity.mBLE.deviceList.get(3).toString();
            btnDevice4.setText("solaqua-" + deviceString.substring(deviceString.length() - 2));
            imageDevice4.setVisibility(View.VISIBLE);
        }
    }

    private void adjustLayout(Integer deviceCount) {
        ImageView tile = rootView.findViewById(R.id.tile);
        ConstraintLayout.LayoutParams layout = (ConstraintLayout.LayoutParams) tile.getLayoutParams();
        Log.i("DEVICE LIST","Device count --> " + deviceCount);
        if(deviceCount == 0) {
            layout.height = 100;
        } else if(deviceCount == 1) {
            layout.height = 170 + 10;
        } else {
            layout.height = 170 +10 + 240*(deviceCount-1);
        }
        tile.setLayoutParams(layout);
    }

    private void messageCreate() {
        Navigation navigation = Navigation.getInstance();
        Log.i("DEVICE LIST","Navigation = " + navigation.getText().toString());
        //txtReset = (TextView) rootView.findViewById(R.id.textReset);
        //txtReset2 = (TextView) rootView.findViewById(R.id.textReset2);

        txtReset.setVisibility(View.VISIBLE);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        txtReset.setText("Last scan at " + currentTime);
        if (deviceCount == 0) {
            // ToDo: reScan option if no devices found
            txtReset2.setVisibility(View.VISIBLE);
            btnReScan.setVisibility(View.VISIBLE);
            txtReset2.setText("Try again?");
            btnReScan.setClickable(true);
        }
    }

    private void messageStart() {
        Navigation navigation = Navigation.getInstance();
        Log.i("DEVICE LIST","Navigation = " + navigation.getText().toString());
        if(navigation.getText().toString().equals("Details") || navigation.getText().toString().equals("Status22")) {
            Boolean checkResult = checkScan();
            Log.i("DETAILS CHECK","Returned result = " + checkResult);
            if(!checkResult) {
                // Find device
                SelectedDevice selectedDevice = SelectedDevice.getInstance();
                //selectedDevice.setText("29");
                Log.i("DEVICE LIST","STATUS 22 ---> Selected Device = " + selectedDevice.getText().toString());
                txtReset.setVisibility(View.VISIBLE);
                txtReset2.setVisibility(View.VISIBLE);
                btnReScan.setVisibility(View.VISIBLE);
                txtReset.setText("Need to reset solaqua-" + selectedDevice.getText().toString() );
                // set up second reset message
                txtReset2.setText("Try again?");
                btnReScan.setClickable(true);
            } else {
                txtReset.setVisibility(View.GONE);
            }
        } else {
            txtReset.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String currentTime = sdf.format(new Date());
            txtReset.setText("Scan completed at " + currentTime);
            if (deviceCount == 0) {
                // ToDo: reScan option if no devices found
                txtReset2.setVisibility(View.VISIBLE);
                btnReScan.setVisibility(View.VISIBLE);
                txtReset2.setText("Try again?");
                btnReScan.setClickable(true);
            }
        }
    }

    private boolean checkScan() {
        Log.i("DEVICE LIST CHECK","checkScan started");
        Boolean result = false;
        String subAddress;
        SelectedDevice selectedDevice = SelectedDevice.getInstance();
        deviceList = MainActivity.mBLE.deviceList;
        deviceCount = MainActivity.mBLE.deviceList.size();
        Log.i("DEVICE LIST CHECK","deviceCount = " + deviceCount);
        for(int pos=0; pos<=deviceCount-1; pos++){
            macAddress = deviceList.get(pos).toString();
            subAddress = macAddress.substring(macAddress.length() - 2);
            Log.i("DETAILS CHECK","subAddress = " + subAddress + " (" + selectedDevice.getText() + ")");
            if(subAddress.equals(selectedDevice.getText())) {
                result = true;
            }
        }
        return result;
    }

    private void snackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),message,Snackbar.LENGTH_INDEFINITE);
        //BottomNavigationView lowerBarView = getActivity().findViewById(R.id.bottomNavigationView);
        //snackbar.setAnchorView(lowerBarView);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView)snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(20);
        snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
        snackbar.setDuration(15000);
        snackbar.show();
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