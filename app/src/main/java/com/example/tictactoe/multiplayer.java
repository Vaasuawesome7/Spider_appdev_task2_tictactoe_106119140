package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tictactoe.game_layout.Multi_TTT;

public class multiplayer extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        Multi_TTT multi_ttt;
        multi_ttt = findViewById(R.id.myview);

        String mPl1 = null, mPl2 = null;

        if (getIntent().getExtras() != null) {
            mPl1 = getIntent().getExtras().getString("player1");
            mPl2 = getIntent().getExtras().getString("player2");
        }

        multi_ttt.init_string(mPl1, mPl2);
        multi_ttt.init(null);
    }
}