package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import com.example.tictactoe.game_layout.Single_TTT;

public class singleplayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        Single_TTT single_ttt = findViewById(R.id.single_view);

        boolean start = false;

        if (getIntent().getExtras() != null)
            start = getIntent().getExtras().getBoolean("start");

        single_ttt.init(null, start);
    }
}