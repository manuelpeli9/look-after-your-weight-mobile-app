package com.example.manuel.layw_app;

/**
 * Created by Manuel on 11/08/2018.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Steps extends Fragment {

    private static TextView twSteps;
    private static TextView twKM;
    private static TextView percentage;
    ProgressBar progressWheel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.steps, container, false);
        twSteps = (TextView) rootView.findViewById(R.id.label_steps);
        twKM = (TextView) rootView.findViewById(R.id.label_km);
        percentage = (TextView) rootView.findViewById(R.id.progressBarinsideTextSteps);
        twSteps.setText(MainActivity.showSteps);
        twKM.setText(MainActivity.showKM);
        progressWheel = (ProgressBar) rootView.findViewById(R.id.progressWheelSteps);
        int perc = 0;
        if(Goals.goalSteps == 0)
            perc = 0;
        else
            perc = (MainActivity.steps > Goals.goalSteps) ? 100 : ((MainActivity.steps * 100) / Goals.goalSteps);
        progressWheel.setSecondaryProgress(perc);
        percentage.setText(perc + "%");
        return rootView;
    }

    public void onResume() {
        super.onResume();
        int perc = 0;
        if(Goals.goalSteps == 0)
            perc = 0;
        else
            perc = (MainActivity.steps > Goals.goalSteps) ? 100 : ((MainActivity.steps * 100) / Goals.goalSteps);
        progressWheel.setSecondaryProgress(perc);
        percentage.setText(perc + "%");
        twSteps.setText(MainActivity.showSteps);
        twKM.setText(MainActivity.showKM);
    }

}
