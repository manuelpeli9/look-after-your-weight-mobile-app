package com.example.manuel.layw_app;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import org.json.JSONArray;
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
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class Music extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;

    ArrayList<String> arrayList;
    ArrayList<String> paths;
    ListView listView;
    ArrayAdapter<String> adapter;
    MediaPlayer mediaPlayer;
    int songPosition;
    final int LIMIT_LOW = 128;
    final int LIMIT_HIGH = 168;

    boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Button button = (Button)findViewById(R.id.stop_activity);
        button.setText("Termina Attività");
        checkHeartBeats();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                Uri myUri = Uri.parse(paths.get((++songPosition) % paths.size()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), myUri);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });

        if (ContextCompat.checkSelfPermission(Music.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Music.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(Music.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
            else {
                ActivityCompat.requestPermissions(Music.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            doAction();
        }
    }

    private void checkHeartBeats() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    URL url = null;
                    try {
                        url = new URL("http://layw-server.herokuapp.com/api/v1.0/users/1/heartbeats-real-time");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    while(!stop) {
                        sleep(40000);
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                            JSONObject jsonObject = new JSONObject(readStream(in));
                            JSONArray jsonArray = jsonObject.getJSONArray("heartbeats-real-time");
                            JSONObject tmp = jsonArray.getJSONObject(jsonArray.length() - 1);
                            int current = tmp.getInt("heartBeat");
                            if (current < LIMIT_LOW) {
                                //play intensifica.wav
                                if (!stop) {
                                    MediaPlayer note = MediaPlayer.create(Music.this, R.raw.intensifica);
                                    note.start();
                                }
                            } else if (current > LIMIT_HIGH) {
                                //play diminuisci.wav
                                if (!stop) {
                                    MediaPlayer note = MediaPlayer.create(Music.this, R.raw.diminuisci);
                                    note.start();
                                }
                            } else {
                                //play ottimo.wav
                                if (!stop) {
                                    MediaPlayer note = MediaPlayer.create(Music.this, R.raw.ottimo);
                                    note.start();
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                    }
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

    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                arrayList.add("Title: " + currentTitle + "\nArtist: " + currentArtist);
                String path = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                paths.add(path);
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(Music.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                        doAction();
                    } else {
                        Toast.makeText(this, "No Permission granted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;
                }
            }
        }
    }

    public void doAction() {
        listView = (ListView)findViewById(R.id.music_list);
        arrayList = new ArrayList<>();
        paths = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //play
                songPosition = i;
                Uri myUri = Uri.parse(paths.get(i)); // initialize Uri here

                if(mediaPlayer.isPlaying())
                    mediaPlayer.reset();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), myUri);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }
        });
    }

    public void stopActivity(View view) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        stop = true;
        Toast.makeText(this, "Attività conclusa", Toast.LENGTH_LONG).show();

        startActivity(new Intent(Music.this, MainActivity.class));
    }
}
