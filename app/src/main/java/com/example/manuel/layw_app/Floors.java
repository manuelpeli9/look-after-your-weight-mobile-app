package com.example.manuel.layw_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Manuel on 11/08/2018.
 */

public class Floors extends Fragment {

    private static TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.floors, container, false);
        textView = (TextView) rootView.findViewById(R.id.label_floors);
        textView.setText(MainActivity.showFloors);
        return rootView;
    }

    public void onResume() {
        super.onResume();
        textView.setText(MainActivity.showFloors);
    }
}
