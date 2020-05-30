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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

public class option_page extends AppCompatActivity {

    private EditText mPlayer1;
    private EditText mPlayer2;
    private Switch mSwitchMode, mStart;
    private ImageView mSunglass, mScissors;
    private SoundPool mSoundPool;
    private int mInteract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_page);

        mPlayer1 = findViewById(R.id.editText_player1);
        mPlayer2 = findViewById(R.id.editText_player2);
        mSwitchMode = findViewById(R.id.switch_multiplayer);
        mSwitchMode.setChecked(false);
        mStart = findViewById(R.id.switch_start);
        mStart.setChecked(false);
        mScissors = findViewById(R.id.imageView_scissors);
        mSunglass = findViewById(R.id.imageView_sunglasses);

        mSunglass.setVisibility(View.INVISIBLE);
        mScissors.setVisibility(View.INVISIBLE);
        mPlayer1.setVisibility(View.INVISIBLE);
        mPlayer2.setVisibility(View.INVISIBLE);

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

    public void change(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        mSoundPool.play(mInteract, 1,1,0,0,1);
        if(mSwitchMode.isChecked()) {
            mSunglass.setVisibility(View.VISIBLE);
            mScissors.setVisibility(View.VISIBLE);
            mPlayer1.setVisibility(View.VISIBLE);
            mPlayer2.setVisibility(View.VISIBLE);
            mStart.setVisibility(View.INVISIBLE);

            mSunglass.startAnimation(animation);
            mScissors.startAnimation(animation);
            mPlayer1.startAnimation(animation);
            mPlayer2.startAnimation(animation);
            mStart.startAnimation(animation1);
        }
        else {
            mSunglass.setVisibility(View.INVISIBLE);
            mScissors.setVisibility(View.INVISIBLE);
            mPlayer1.setVisibility(View.INVISIBLE);
            mPlayer2.setVisibility(View.INVISIBLE);
            mStart.setVisibility(View.VISIBLE);

            mSunglass.startAnimation(animation1);
            mScissors.startAnimation(animation1);
            mPlayer1.startAnimation(animation1);
            mPlayer2.startAnimation(animation1);
            mStart.startAnimation(animation);
        }
    }

    public void start(View view) {
        String text_pl1 = mPlayer1.getText().toString();
        String text_pl2 = mPlayer2.getText().toString();
        if (mSwitchMode.isChecked()) {
            if (!(text_pl1.equals("") || text_pl2.equals(""))) {
                Intent i = new Intent(this, multiplayer.class);
                mSoundPool.play(mInteract, 1,1,0,0,1);
                i.putExtra("player1", text_pl1);
                i.putExtra("player2", text_pl2);
                startActivity(i);
                finish();
            }
        }
        else {
            mSoundPool.play(mInteract, 1,1,0,0,1);
            Intent i = new Intent(this, singleplayer.class);
            i.putExtra("start", mStart.isChecked());
            startActivity(i);
            finish();
        }
    }

    public void switch_start(View v) {
        if (mSwitchMode.isChecked()) {
            mStart.setChecked(!mStart.isChecked());
        }
        if (mStart.getVisibility() == View.VISIBLE)
            mSoundPool.play(mInteract,1,1,0,0,1);
    }

    public void table(View view) {
        mSoundPool.play(mInteract,1,1,0,0,1);
        startActivity(new Intent(this, result_page.class));
        finish();
    }
}
