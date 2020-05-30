package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class result_page extends AppCompatActivity {

    private String[] mNames;
    private int[] mScores;
    private int counter;
    private int mInteract;
    private SoundPool mSoundPool;
    private final String SHARED_PREF = "sharedPrefs", NAMES = "name_", SCORES = "score_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        RecyclerView table = findViewById(R.id.score_view);

        counter = 0;
        mScores = new int[200];
        mNames = new String[200];

        String text = null;
        if (getIntent().getExtras() != null)
            text = getIntent().getExtras().getString("winner");

        load_data();
        if (text != null) {
            save_data(text);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(7)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            mSoundPool = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);
        }

        mInteract = mSoundPool.load(this, R.raw.button, 1);

        ResultAdapter resultAdapter = new ResultAdapter(mNames, mScores);
        table.setLayoutManager(new LinearLayoutManager(this));
        table.setAdapter(resultAdapter);
    }

    private void load_data() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        counter = sharedPreferences.getInt("count", 0);

        for (int i = 0; i < counter; i++) {
            mScores[i] = sharedPreferences.getInt(SCORES + i, 0);
            mNames[i] = sharedPreferences.getString(NAMES + i, "");
        }
    }

    private void save_data(String text) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int check = linear_search_string(text);
        if (check < 0) {
            mNames[counter] = text;
            mScores[counter]++;
            counter++;
        }
        else {
            mScores[check]++;
        }

        for (int i = 0; i < counter; i++) {
            editor.putString(NAMES + i, mNames[i]);
            editor.putInt(SCORES + i, mScores[i]);
        }
        editor.putInt("count", counter);

        editor.apply();
    }

    public void go_back(View v) {
        mSoundPool.play(mInteract, 1,1,0,0,1);
        startActivity(new Intent(this, option_page.class));
        finish();
    }

    private int linear_search_string(String text) {
        if (mNames == null)
            return -1;
        for (int i = 0; i < mNames.length; i++) {
            if (text.equals(mNames[i]))
                return i;
        }
        return -1;
    }

    public void clear(View v) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
    }
}