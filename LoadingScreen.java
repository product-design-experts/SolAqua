package ble.solaqua;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.CompletableFuture;

public class LoadingScreen extends Activity {

    private BLEMiddleware mBLE;
    public TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        this.loadingText = findViewById(R.id.loading_text);
        Log.wtf("LOADING SCREEN","Set view to loading screen layout");

        CompletableFuture.runAsync(() -> {
            // While loop allows us to wait until user grants permissions before moving forward
            while (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                blePermissions();
                // Add delay so that App doesn't continuously fire off multiple async threads
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            // Finish and start Home Fragment using onActivityResult in Main Activity
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }

    private void blePermissions() {
        Log.i("LOADING_SCREEN", "Permissions...");
        // Creates (LoadingScreen) instance of the BLE Middleware
        mBLE = new BLEMiddleware(this);

        // Check to see if Device has BT caps
        if (mBLE.bleAdapter == null) {
            // Device doesn't support Bluetooth
            toastMessage("BLE is not supported by your device.");
        } else {
            // Request user permission to access Device Bluetooth Connection
            loadingText.setText("Requesting Bluetooth permissions...");
//            toastMessage("Device supports BLE." + MainActivity.mBLE.bleAdapter.isEnabled());
            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    mBLE.getBLELocalizationPermissions(LoadingScreen.this);
                }
            }).thenRun(() -> {
                CompletableFuture.runAsync(() -> {
                    mBLE.requestDeviceBluetoothEnabled(this);
                }).thenRun(() -> {
                    if (!mBLE.bleAdapter.isEnabled()) {
                        toastMessage("The SolAqua App will not run unless bluetooth is enabled.");
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            });
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}