package ble.solaqua;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer delayTime = 2000;
        Log.i("SPLASH SCREEN ACTIVITY","Splash screen started");

        // Start MainActivity after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,
                        MainActivity.class));
                Log.i("SPLASH SCREEN ACTIVITY","Splash screen stopped. Main Activity to start");
            }
        }, delayTime);
    }

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
}