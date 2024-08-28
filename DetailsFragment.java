package ble.solaqua;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.android.material.snackbar.Snackbar;

import ble.solaqua.Adapters.MaxTempAdapter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DetailsFragment extends Fragment {

    View rootView;

    Button btnSave;

    ArrayList deviceList;
    String macAddress;
    String serviceCheck;
    Integer tempMaxInput;
    TextView textTitle;
    TextView textBattery;
    TextView textTemp;
    TextView textTargetTemp;
    TextView textSend;
    TextView textMessage;
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
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        textTitle = rootView.findViewById(R.id.titleDevice);
        textBattery = rootView.findViewById(R.id.textBattery);
        textTemp = rootView.findViewById(R.id.textTemp);
        textTargetTemp = rootView.findViewById(R.id.textTargetTemp);
        listTempMax = rootView.findViewById(R.id.listTempMax);
        btnSave = rootView.findViewById(R.id.buttonSave);
        textSend = rootView.findViewById(R.id.textSend);
        textMessage = rootView.findViewById(R.id.textMessage);

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

        listTempMaxArr = Arrays.asList("MIN", "75\u00B0F", "76\u00B0F", "77\u00B0F", "78\u00B0F", "79\u00B0F", "80\u00B0F",
                "81\u00B0F", "82\u00B0F", "83\u00B0F", "84\u00B0F", "85\u00B0F", "86\u00B0F", "87\u00B0F", "88\u00B0F",
                "89\u00B0F", "90\u00B0F", "91\u00B0F", "92\u00B0F", "93\u00B0F", "94\u00B0F", "95\u00B0F", "MAX", "");
        // Set array adapter for scrollable list of temps
        listTempMaxAdapter = new MaxTempAdapter(MainActivity.maContext, R.layout.max_temp_item, listTempMaxArr);
        listTempMax.setAdapter(listTempMaxAdapter);

        // Display device name in title
        if (macAddress != null) {
            textTitle.setText("solaqua-" + macAddress.substring(macAddress.length() - 2) + " Info");
        }

        return rootView;
    }

    @SuppressLint("CheckResult")
    public void onStart() {
        super.onStart();
        Log.d("DEVICE DETAILS", "onStart started");
        Navigation navigation = Navigation.getInstance();
        Log.wtf("DEVICE DETAILS", "onStart Thread = " + Thread.currentThread().getName());

        btnSave = rootView.findViewById(R.id.buttonSave);

        textBattery = (TextView) rootView.findViewById(R.id.textBattery);
        textTemp = (TextView) rootView.findViewById(R.id.textTemp);
        listTempMax = (ListView) rootView.findViewById(R.id.listTempMax);
        textSend = (TextView) rootView.findViewById(R.id.textSend);
        textMessage = (TextView) rootView.findViewById(R.id.textMessage);

        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setClickable(false);
        textSend.setVisibility(View.INVISIBLE);
        textMessage.setVisibility(View.INVISIBLE);

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
                MainActivity.mBLEService.checkService(macAddress, serviceCheck);
            }
        };
        Runnable readBattery = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "read battery runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.readBattery(macAddress);
            }
        };
        Runnable readTemp = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "readTemps Runnable sarted. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.readTemps(macAddress);
            }
        };
        Runnable readTempMax = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "readTempMax Runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.readTempMax(macAddress);
            }
        };
        Runnable setTempMax = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "setTempMax Runnable started. Thread = " + Thread.currentThread().getName());
                MainActivity.mBLEService.setTempMax(macAddress, tempMaxInput);
            }
        };
        Runnable checkSetTempMax = new Runnable() {
            @Override
            public void run() {
                Log.i("DEVICE DETAILS", "checkSetTempMax Runnable started. Thread = " + Thread.currentThread().getName());
                Navigation navigation = Navigation.getInstance();
                if (navigation.getText().toString().equals("ErrorWriteMaxTemp")) {
                    Log.i("DEVICE DETAILS", "TEMP MAX did not write properly");
                    snackBarMessage("E08 - Connection lost (Cannot write max water temperature)");
                    MainActivity.mQueue.jumpToEnd();
                } else {
                    MainActivity.mQueue.completedCommand();
                }
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
        if (MainActivity.mBLEService.connectionState != 2) {
            MainActivity.mQueue.addCommand(connect);
        }
        MainActivity.mQueue.addCommand(checkConnection);
        serviceCheck = "user_data";
        MainActivity.mQueue.addCommand(checkService);
        MainActivity.mQueue.addCommand(readBattery);
        MainActivity.mQueue.addCommand(readTemp);
        //MainActivity.mQueue.addCommand(readTemp);  // ToDo: try to get both instance ID's
        serviceCheck = "scan_params";
        MainActivity.mQueue.addCommand(checkService);
        MainActivity.mQueue.addCommand(readTempMax);
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
        updateDisplay();

        // Listener for ListView to get / set Max Temp
        listTempMax.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    Log.i("MAX TEMP ADAPTER", "Scroll State Changed " + listTempMax.getLastVisiblePosition());
                    int index = listTempMax.getLastVisiblePosition() - 2;
                    if (listTempMaxArr.get(index) != null) {
                        String visibleMaxTemp = listTempMaxArr.get(index).toString().replace("\u00B0F", "");
                        Log.i("MAX TEMP ADAPTER", "Selected item = " + visibleMaxTemp);
                        listTempMax.setSelectionFromTop(listTempMax.getLastVisiblePosition() - 3, 1);
                        selectedMaxTemp = Integer.parseInt(visibleMaxTemp);
                        Log.i("MAX TEMP ADAPTER", "Selected item (integer) = " + selectedMaxTemp);

                        // Show Save button (or hide)
                        if (!MainActivity.mBLEService.tempMax.equals(selectedMaxTemp)) {
                            Log.i("SCROLL LISTENER", "Temp is different. Activate button and message");
                            btnSave.setVisibility(View.VISIBLE);
                            btnSave.setClickable(true);
                            textSend.setVisibility(View.VISIBLE);
                            textSend.setText("Press to SEND to device");
                        } else {
                            Log.i("SCROLL LISTENER", "Temp is same");
                            btnSave.setVisibility(View.GONE);
                            btnSave.setClickable(false);
                            textSend.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Nothing here
            }
        });

        btnSave.setOnClickListener(rootView -> {
            Log.i("DETAILS SAVE", "Save Button pressed");
            //btnSave.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F2F2")));
            btnSave.setVisibility(View.INVISIBLE);
            btnSave.setClickable(false);
            tempMaxInput = selectedMaxTemp;
            MainActivity.mQueue.clear();
            serviceCheck = "scan_params";
            isComplete.set(false);
            // Check to see if a new tempMax has been entered
            if (!MainActivity.mBLEService.tempMax.equals(tempMaxInput) && (!(selectedMaxTemp == null))) {
                Log.i("DETAILS FRAGMENT", "Save button should be invisible");
                // Initiate BLE Write
                if (MainActivity.mBLEService.connectionState != 2) {
                    Log.i("DETAILS FRAGMENT", "Device is not connected");
                    MainActivity.mQueue.addCommand(connect);
                }
                MainActivity.mQueue.addCommand(checkService);
                MainActivity.mQueue.addCommand(setTempMax);
                MainActivity.mQueue.addCommand(checkSetTempMax);
                MainActivity.mQueue.addCommand(readTempMax);
                MainActivity.mQueue.addCommand(complete);
                MainActivity.mQueue.startCommand();
                //Wait until list complete
                while (!isComplete.get()) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // Check for BLE Service errors
                checkErrors();
            } else {
                Log.i("DEVICES DETAILS", "Temp Max is not a new value");
                snackBarMessage("This temperature setting has already been stored on device");
            }
        });
        BottomNavigationView bottomNavigationView = rootView.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_return) {
                    // Return to previous screen
                    leaveScreen();
                    Log.i("NAVIGATION", "RETURN button pressed");
                    loadFragment(new DeviceListFragment());
                } else if (id == R.id.navigation_refresh) {
                    // Refresh current screen
                    Log.i("NAVIGATION", "REFRESH button pressed");
                    isComplete.set(false);
                    MainActivity.mQueue.clear();
                    if (MainActivity.mBLEService.connectionState != 2) {
                        MainActivity.mQueue.addCommand(connect);
                        MainActivity.mQueue.addCommand(checkConnection);
                    }
                    serviceCheck = "user_data";
                    MainActivity.mQueue.addCommand(checkService);
                    MainActivity.mQueue.addCommand(readBattery);
                    MainActivity.mQueue.addCommand(readTemp);
                    serviceCheck = "scan_params";
                    MainActivity.mQueue.addCommand(checkService);
                    MainActivity.mQueue.addCommand(readTempMax);
                    MainActivity.mQueue.addCommand(complete);
                    Log.w("DEVICE INFO", "COMMAND QUEUE start");
                    MainActivity.mQueue.startCommand();
                    // Wait until complete
                    while (!isComplete.get()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Update display
                    updateDisplay();
                    btnSave.setVisibility(View.INVISIBLE);
                    btnSave.setClickable(false);
                    textSend.setVisibility(View.INVISIBLE);
                    textMessage.setVisibility(View.VISIBLE);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String currentTime = sdf.format(new Date());
                    textMessage.setText("Data refreshed at " + currentTime);
                } else if (id == R.id.navigation_home) {
                    // Return to menu screen
                    leaveScreen();
                    Log.i("NAVIGATION", "HOME button pressed");
                    loadFragment(new MenuFragment());
                }
                return false;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("DEVICE DETAILS","ON STOP method");
        leaveScreen();
    }

    private void leaveScreen() {
        Navigation navigation = Navigation.getInstance();
        if(MainActivity.mBLEService.connectionState == 2) {
            MainActivity.mBLEService.disconnect();
            // Add delay to allow disconnect operation and return result
            while (MainActivity.mBLEService.connectionState == 2) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Log.i("DETAILS RETURN", "connectionState = " + MainActivity.mBLEService.connectionState);
        if (navigation.getText().toString().equals("ErrorDisconnect")) {
            snackBarMessage("E05 - Error disconnecting device");
        }
        Log.i("DEVICES DETAILS", "Return Button - Load Device List Fragment and start SCAN");
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkErrors() {
        Navigation navigation = Navigation.getInstance();
        if (navigation.getText().toString().equals("ErrorService")) {
            snackBarMessage("E03 - Error finding BLE services");
        } else if (navigation.getText().toString().equals("ErrorGatt")) {
            snackBarMessage("E04 - Error encountered during Gatt Callback");
        } else {
            if (navigation.getText().toString().equals("ErrorReadTempMax")) {
                //unable to read max temperature
                Log.i("DEVICE DETAILS", "Save Button update - Max Temperature read ERROR");
                snackBarMessage("E08 - Connection lost (Cannot read max water temperature)");
            } else if (!MainActivity.mBLEService.tempMax.equals(tempMaxInput)) {
                Log.i("DEVICE DETAILS", "Save Button update - ERROR MAX TEMP was not set to " + tempMaxInput + "but set  to " + MainActivity.mBLEService.tempMax);
                snackBarMessage("E09 - Connection lost (Cannot write max water temperature)");
            } else {
                Log.d("DEVICE DETAILS", "Save Button update - MAX Temperature = " + MainActivity.mBLEService.tempMax);
                // Use selectedMaxTemp to set the new MainActivity.mBLEService.tempMax
                //snackBarMessage("Target Temperature updated");
                textMessage.setVisibility(View.VISIBLE);
                textMessage.setText("Target Temp saved");
            }
        }
        //Hide button nd text
        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setClickable(false);
        textSend.setVisibility(View.INVISIBLE);
    }

    private void updateDisplay() {
        Navigation navigation = Navigation.getInstance();
        String tempMaxString = null;
        // Update display
        Log.i("DEVICE DETAILS","Command Queue completed - isComplete = " + isComplete);
        Log.wtf("DEVICE DETAILS","Update Display started. Thread = " + Thread.currentThread().getName());
        // Check for BLE Service errors
        if (navigation.getText().toString().equals("Status22")) {
            loadFragment(new DeviceListFragment());
            //snackBarMessage("solaqua device is not responding and needs to be reset");
        } else if (navigation.getText().toString().equals("ErrorConnect")) {
            snackBarMessage("E02 - Error connecting to device using BLE");
        } else if (navigation.getText().toString().equals("ErrorService")) {
            snackBarMessage("E03 - Error finding BLE services");
        } else if (navigation.getText().toString().equals("ErrorGatt")) {
            snackBarMessage("E04 - Error encountered during Gatt Callback");
        } else if (navigation.getText().toString().equals("BleGattNull")) {
            snackBarMessage("E04 - Error encountered during Gatt Callback");
        } else if (navigation.getText().toString().equals("ErrorReadBattery")) {
            //unable to read battery
            Log.i("DEVICE DETAILS", "Update Display - Battery read ERROR");
            snackBarMessage("E06 - Connection lost (Cannot read battery level)");
        } else if (navigation.getText().toString().equals("ErrorReadTemps")) {
            Log.d("DEVICE DETAILS", "Update Display - Water Temperature = " + MainActivity.mBLEService.tempWater);
            textTemp.setText("Water Temp = " + MainActivity.mBLEService.tempWater + "\u00B0" + "F");
        } else if (navigation.getText().toString().equals("ErrorReadTempMax")) {
            //unable to read max temperature
            Log.i("DEVICE DETAILS", "Update Display - Max Temperature read ERROR");
            snackBarMessage("E08 - Connection lost (Cannot read max water temperature)");
        } else {
            if (MainActivity.mBLEService.batteryLevel == 0) {
                // Show Initialization message
                textBattery.setVisibility(View.INVISIBLE);
                textTemp.setVisibility(View.INVISIBLE);
                textTargetTemp.setVisibility(View.INVISIBLE);
                listTempMax.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
                textMessage.setText("Waiting for device install");
            } else {
                // Refresh display values
                Log.d("DEVICE DETAILS", "Update Display - Battery Level = " + MainActivity.mBLEService.batteryLevel);
                textBattery.setText(MainActivity.mBLEService.batteryLevel.toString() + "%");
                if(MainActivity.mBLEService.batteryLevel < 25) {
                    Log.i("DEVIVE DETAILS", "Value should be orange");
                    textBattery.setTextColor(getResources().getColor(R.color.accent));
                }
                Log.d("DEVICE DETAILS", "Update Display - Water Temperature = " + MainActivity.mBLEService.tempWater);
                textTemp.setText("Water Temp = " + MainActivity.mBLEService.tempWater + "\u00B0" + "F");
                if (MainActivity.mBLEService.tempMax.equals(null)) {
                    Log.i("DETAILS","Update Display - temPmax reads NULL");
                    tempMaxString = "75" + "\u00B0F";
                }
                if ((MainActivity.mBLEService.tempMax < 75) || (MainActivity.mBLEService.tempMax > 95)) {
                    tempMaxString = "75" + "\u00B0F";
                } else {
                    tempMaxString = Integer.toString(MainActivity.mBLEService.tempMax) + "\u00B0F";
                }
                int indexOfCurrentMaxTemp = listTempMaxArr.indexOf(tempMaxString);
                Log.i("DEVICE DETAILS", "Scroll position = " + indexOfCurrentMaxTemp);
                listTempMax.setSelectionFromTop(indexOfCurrentMaxTemp - 1, 1);
                listTempMax.setItemChecked(indexOfCurrentMaxTemp, true);
            }
        }
    }

    private void snackBarMessage(String message) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),message,Snackbar.LENGTH_INDEFINITE);
        //BottomNavigationView lowerBarView = getActivity().findViewById(R.id.bottomNavigationView);
        //snackbar.setAnchorView(lowerBarView);
        View snackbarView = snackbar.getView();
        TextView textView = (TextView)snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(20);
        snackbar.getView().setOnClickListener(view -> snackbar.dismiss());
        snackbar.setDuration(5000);
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
