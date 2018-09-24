package com.example.manuel.layw_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Manuel on 11/08/2018.
 */

public class Calories extends Fragment {

    private static TextView textView;
    private static TextView percentage;
    ProgressBar progressWheel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calories, container, false);
        textView = (TextView) rootView.findViewById(R.id.label_calories);
        textView.setText(MainActivity.showCalories);
        percentage = (TextView) rootView.findViewById(R.id.progressBarinsideTextCalories);
        progressWheel = (ProgressBar) rootView.findViewById(R.id.progressWheelCalories);
        int perc = 0;
        if(Goals.goalCalories == 0)
            perc = 0;
        else
            perc = (MainActivity.calories > Goals.goalCalories) ? 100 : ((MainActivity.calories * 100) / (int)Goals.goalCalories);

        progressWheel.setSecondaryProgress(perc);
        percentage.setText(perc + "%");
        return rootView;
    }

    public void onResume() {
        super.onResume();
        textView.setText(MainActivity.showCalories);
        int perc = 0;
        if(Goals.goalCalories == 0)
            perc = 0;
        else
            perc = (MainActivity.calories > Goals.goalCalories) ? 100 : ((MainActivity.calories * 100) / (int)Goals.goalCalories);
        progressWheel.setSecondaryProgress(perc);
        percentage.setText(perc + "%");
    }
}
