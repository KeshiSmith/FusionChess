package com.keshi.chess.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.keshi.chess.service.MusicService;
import com.keshi.chess.R;

public class MainMenuActivity extends AppCompatActivity {
    private Intent intent_music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        // Keep screen ON.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Get (Button)[chess,help,exit] from MainActivity.
        Button button_chess=(Button)findViewById(R.id.menu_chess);
        Button button_help=(Button)findViewById(R.id.menu_help);
        Button button_options=(Button)findViewById(R.id.menu_options);
        Button button_exit=(Button)findViewById(R.id.menu_exit);
        // Set Listener of Button.
        button_chess.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent chess_intent=new Intent(MainMenuActivity.this,GameActivity.class);
                startActivity(chess_intent);
                playSound(R.raw.new_game);//Play sound for chess start.
            }
        });
        button_help.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent help_intent=new Intent(MainMenuActivity.this,HelpActivity.class);
                startActivity(help_intent);
            }
        });
        button_options.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                OptionsDialog(MusicService.state_mute,MusicService.volume_music,MusicService.volume_sound);
            }
        });
        button_exit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                ConfirmExitDialog();
            }
        });
        // Let's Start to listening music LOL.
        intent_music=new Intent(this,MusicService.class);
        startService(intent_music);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Open music
        Intent intent =new Intent("com.keshi.MUSIC");
        intent.putExtra("count",1);
        sendBroadcast(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Pause music
        Intent intent =new Intent("com.keshi.MUSIC");
        intent.putExtra("count",-1);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop service
        stopService(intent_music);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0) {
            ConfirmExitDialog();    // Key_Back for exit.
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // A dialog for options.
    private void OptionsDialog(boolean state_mute,int volume_music,int volume_sound){
        AlertDialog.Builder options_builder =new AlertDialog.Builder(this);
        View mView= LayoutInflater.from(options_builder.getContext()).inflate(R.layout.view_dialog_options, null);
        // Get controls of the View.
        Switch switch_mute=(Switch)mView.findViewById(R.id.switch_mute);
        final SeekBar seekBar_music=(SeekBar)mView.findViewById(R.id.seekbar_music);
        final SeekBar seekBar_sound=(SeekBar)mView.findViewById(R.id.seekbar_sound);
        // Set values of those controls.
        switch_mute.setChecked(state_mute);
        seekBar_music.setProgress(volume_music);
        seekBar_sound.setProgress(volume_sound);
        seekBar_music.setEnabled(!state_mute);
        seekBar_sound.setEnabled(!state_mute);
        // Set dialog attributes.
        options_builder.setView(mView);
        options_builder.setIcon(R.mipmap.ic_launcher);
        options_builder.setTitle(R.string.dialog_options_title);
        // Set positive Button
        options_builder.setPositiveButton(R.string.dialog_options_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        // Set listener of controls
        switch_mute.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    seekBar_music.setEnabled(false);
                    seekBar_sound.setEnabled(false);
                }
                else{
                    seekBar_music.setEnabled(true);
                    seekBar_sound.setEnabled(true);
                }
                Intent intent =new Intent("com.keshi.MUSIC");
                intent.putExtra("mute",b);
                sendBroadcast(intent);
            }
        });
        seekBar_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int volume_music;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volume_music=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                volume_music=seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent =new Intent("com.keshi.MUSIC");
                intent.putExtra("music",volume_music);
                sendBroadcast(intent);
            }
        });
        seekBar_sound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int volume_sound;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                volume_sound=i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                volume_sound=seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent =new Intent("com.keshi.MUSIC");
                intent.putExtra("sound",volume_sound);
                intent.putExtra("playSound",R.raw.pick);
                sendBroadcast(intent);
            }
        });
        options_builder.create().show();// Show the dialog.
    }
    // A dialog for confirming exit.
    private void ConfirmExitDialog(){
        AlertDialog exit_dialog =new AlertDialog.Builder(MainMenuActivity.this)
                .setTitle(R.string.dialog_exit_title)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.dialog_exit_massage)
                .setPositiveButton(R.string.dialog_exit_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();  // Finish this activity to exit.
                    }
                })
                .setNegativeButton(R.string.dialog_exit_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Choose nothing.
                    }
                }).create();// Create dialog.
        exit_dialog.show();    // Show the dialog.
    }
    // Play Sounds.
    public void playSound(int resid){
        if(!MusicService.state_mute) {
            Intent intent = new Intent("com.keshi.MUSIC");
            intent.putExtra("playSound", resid);
            sendBroadcast(intent);
        }
    }
}
