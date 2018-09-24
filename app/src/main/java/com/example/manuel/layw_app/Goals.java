package com.example.manuel.layw_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Manuel on 11/08/2018.
 */

public class Goals extends Fragment {

    View rootView;

    public static int goalSteps = 0;
    public static double goalCalories = 0;
    public static double goalWeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.goals, container, false);
        doThread();
        return rootView;
    }

    public void doThread() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //richiesta dati al server
                    URL url = null;
                    try {
                        url = new URL("http://layw-server.herokuapp.com/api/v1.0/users/1/goals-weight?date=10-09-2018");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    callAPI(url, 1);

                    try {
                        url = new URL("http://layw-server.herokuapp.com/api/v1.0/users/1/goals-calories-out?date=10-09-2018");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    callAPI(url, 2);

                    try {
                        url = new URL("http://layw-server.herokuapp.com/api/v1.0/users/1/goals-steps-daily/current");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    callAPI(url, 0);

                    TextView twSteps = (TextView)rootView.findViewById(R.id.goal_step);
                    twSteps.setText("" + goalSteps);
                    TextView twCalories = (TextView)rootView.findViewById(R.id.goal_calories);
                    twCalories.setText("" + goalCalories);
                    TextView twWeight = (TextView)rootView.findViewById(R.id.goal_weight);
                    twWeight.setText("" + goalWeight);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

        thread.start();
    }

    public void callAPI(URL url, int type) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            JSONObject jsonObject = new JSONObject(readStream(in));
            if(type == 0)
                goalSteps = jsonObject.getJSONObject("goals-steps-daily").getInt("goal");
            if(type == 1)
                goalWeight = jsonObject.getJSONObject("goals-weights").getDouble("goal");
            if(type == 2)
                goalCalories = jsonObject.getJSONObject("goals-calories-out").getDouble("goal");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private String readStream(InputStream in) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        return bufferedReader.readLine();
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView twSteps = (TextView)rootView.findViewById(R.id.goal_step);
        twSteps.setText("" + goalSteps);
        TextView twCalories = (TextView)rootView.findViewById(R.id.goal_calories);
        twCalories.setText("" + goalCalories);
        TextView twWeight = (TextView)rootView.findViewById(R.id.goal_weight);
        twWeight.setText("" + goalWeight);
    }
}
