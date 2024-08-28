package ble.solaqua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.DisplayMetrics;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {
    public static Activity mActivity;

    public static Context maContext;
    public static BLEMiddleware mBLE;
    public static BLEService mBLEService;
    public static CommandQueue mQueue;
    public static ExecutorService executorServiceBLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Log.i("MAIN ACTIVITY", "SplashScreen completed");
        setContentView(R.layout.activity_main);

        // A reference to the MainActivity's context
        maContext = this.getApplicationContext();
        mActivity = this;

        // Instantiate the Command Queue
        mQueue = new CommandQueue();

        // Executor Services
        executorServiceBLE = Executors.newSingleThreadExecutor();

        // Start Loading Screen intent
        Log.wtf("MAIN ACTIVITY","Start Loading Screen intent");
        Intent loadingScreenIntent = new Intent(MainActivity.this, LoadingScreen.class);
        activityLauncher.launch(loadingScreenIntent);

        // Creates (MainActivity) instance of the BLE Middleware
        mBLE = new BLEMiddleware(this);
        // Creates (MainActivity) instance of the BLE Service
        mBLEService = new BLEService();

    }

    //---------------------------------------------- Functions --------------------------------------------------------
    ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i("MAINACTIVITY", "on Activity Result -> " + result.getResultCode() + " " + RESULT_OK);
                    // Verify result code = RESULT_OK = -1
                    if (result.getResultCode() == RESULT_OK) {
                        Log.wtf("ON ACTIVTY RESULT","View set to activity_main");
                        setContentView(R.layout.activity_main);
                        transitionFragment(new MenuFragment());
                    }
                }
            }
    );

    private void transitionFragment(Fragment fragment) {
        Log.i("VIEWS", "Transition Animation started");
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in,
                R.animator.fade_out);
        // fragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out);
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment, fragment.getClass().getName());
        fragmentTransaction.commit(); // save the changes
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

    @Override
    public void onPause() {
        super.onPause();
        Log.i("MAIN ACTIVITY", "onPAUSE triggered");
        if (MainActivity.mBLEService.connectionState == 2) {
            MainActivity.mBLEService.disconnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MAIN ACTIVITY", "onSTOP triggered");
        if (MainActivity.mBLEService.connectionState == 2) {
            MainActivity.mBLEService.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MAIN ACTIVITY", "onSTOP triggered");
        if (MainActivity.mBLEService.connectionState == 2) {
            MainActivity.mBLEService.disconnect();
        }
    }
}