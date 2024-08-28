package ble.solaqua;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MenuFragment extends Fragment {
    View rootView;

    Button btnGetStarted;
    Button btnDevices;
    Button btnSupport;
    Button btnPurchase;


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MENU_FRAGMENT", "onCreateView started");

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        Navigation navigation = Navigation.getInstance();
        navigation.setText("Menu");

        btnGetStarted = (Button) rootView.findViewById(R.id.buttonGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("MENU FRAGMENT", "Clicked on Get Started button");
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.solar4pools.com/pages/get-started"));
                startActivity(intent);
            }
        });

        btnDevices = (Button) rootView.findViewById(R.id.buttonDevices);
        btnDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("MENU FRAGMENT", "Clicked on Devices button");
                navigation.setText("Home");
                loadFragment(new DeviceListFragment());
            }
        });

        btnSupport = (Button) rootView.findViewById(R.id.buttonSupport);
        btnSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("MENU FRAGMENT", "Clicked on Support button");
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.solar4pools.com/pages/support"));
                startActivity(intent);
            }
        });

        btnPurchase = (Button) rootView.findViewById(R.id.buttonPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View rootView) {
                Log.i("MENU FRAGMENT", "Clicked on Purchase button");
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.solar4pools.com/collections/all"));
                startActivity(intent);
            }
        });

        return rootView;
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
