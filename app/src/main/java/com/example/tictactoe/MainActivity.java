package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImage;
    private TextView mWelcome, mBlinker, mName;
    private Animation blinker_anim, left_to_right_animation, special_animation, right_to_left_animation;
    private SoundPool mSoundPool;
    private int mInteract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImage = findViewById(R.id.pic);
        mWelcome = findViewById(R.id.welcome);
        mBlinker = findViewById(R.id.blinker);
        mName = findViewById(R.id.name);

        special_animation = AnimationUtils.loadAnimation(this, R.anim.sample_anim);
        blinker_anim = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        left_to_right_animation = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        right_to_left_animation = AnimationUtils.loadAnimation(this, R.anim.righttoleft);


        mWelcome.startAnimation(left_to_right_animation);
        mImage.startAnimation(right_to_left_animation);
        mName.startAnimation(special_animation);
        mBlinker.startAnimation(blinker_anim);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }

    public void start(View v) {
        mSoundPool.play(mInteract, 1,1,0,0,1);
        startActivity(new Intent(this, option_page.class));
    }
}
