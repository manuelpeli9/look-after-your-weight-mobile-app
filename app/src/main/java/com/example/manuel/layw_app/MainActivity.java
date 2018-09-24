package com.example.manuel.layw_app;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    public static String showSteps = "PASSI: ";
    public static String showCalories = "CALORIE\nBRUCIATE: ";
    public static String showFloors = "PIANI\nPERCORSI: ";
    public static String showKM = "0 km";

    public static int steps = 0;
    public static int calories = 0;
    public static int floors = 0;

    private final int NUM_PAGES = 4;
    private final double CONVERT_TO_KM = 0.0007619994;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doThread();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Chat.class));
            }
        });
    }

    public void onResume() {
        doThread();
        super.onResume();
    }

    public void doThread() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //richiesta dati al server
                    URL url = null;
                    try {
                        url = new URL("http://layw-server.herokuapp.com/api/v1.0/users/1/activity-summary?date=15-08-2018");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                        JSONObject jsonObject = new JSONObject(readStream(in));
                        steps = jsonObject.getJSONObject("activity-summary").getInt("steps");
                        floors = jsonObject.getJSONObject("activity-summary").getInt("floors");
                        calories = jsonObject.getJSONObject("activity-summary").getJSONObject("caloriesCategory").getInt("outCalories");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        urlConnection.disconnect();
                    }
                    showSteps = "PASSI: " + steps;
                    showCalories = "CALORIE\nBRUCIATE: " + calories;
                    showFloors = "PIANI\nPERCORSI: " + floors;

                    double km = steps*CONVERT_TO_KM;
                    NumberFormat formatter = new DecimalFormat("#0.000");
                    showKM = formatter.format(km) + " km";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private String readStream(InputStream in) throws IOException {
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                return bufferedReader.readLine();
            }
        });

        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Steps();
                case 1:
                    return new Calories();
                case 2:
                    return new Goals();
                case 3:
                    return new StartActivity();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show total pages.
            return NUM_PAGES;
        }
    }
}
