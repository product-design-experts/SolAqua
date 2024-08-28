package ble.solaqua.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import ble.solaqua.R;

public class MaxTempAdapter extends ArrayAdapter<String>{
    private int resourceLayout;
    private Context maContext;
    public MaxTempAdapter(@NonNull Context context, int resource, List<String> maxTemps) {
        super(context, resource, maxTemps);
        this.resourceLayout = resource;
        this.maContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(maContext);
            v = vi.inflate(resourceLayout, null);
        }

        String maxTemp = getItem(position);

        if (maxTemp != null) {
            TextView tempSelector = v.findViewById(R.id.maxTempItem);
            if (tempSelector != null) {
                tempSelector.setText(maxTemp);
            }
        }

        return v;
    }
}
