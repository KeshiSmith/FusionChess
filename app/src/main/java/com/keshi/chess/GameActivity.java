package com.keshi.chess;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import com.jjtx.bluetoothutil.util.BluetoothHelper;

import java.io.UnsupportedEncodingException;

public class GameActivity extends Activity {
    private ChessView chessView;
    private Dialog pvp_dialog=null;
    private ProgressDialog wait_dialog=null;// A dialog for waiting for link.
    private Dialog join_dialog=null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private BluetoothHelper bluetoothHelper;
    private MyBluetoothListener myBluetoothListener;
    private ListView list_bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Keep screen ON.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        chessView=(ChessView)findViewById(R.id.game_view);
        // Get Button of HelpActivity
        Button button_new=(Button)findViewById(R.id.game_new);
        Button button_undo=(Button)findViewById(R.id.game_undo);
        final Button button_admit=(Button)findViewById(R.id.game_admit);
        Button button_pvp=(Button)findViewById(R.id.game_pvp);
        Button button_settings=(Button)findViewById(R.id.game_settings);
        Button button_back=(Button)findViewById(R.id.game_back);
        // Set listener of controls
        button_new.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                NewDialog();// New Game
            }
        });
        button_undo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(chessView.isCanUndo()) {
                    if (bluetoothHelper.isConnected()) {
                        sendMessage("undo");
                        showMessage(getString(R.string.message_wait_agree));
                    }
                    else {
                        chessView.undoPiece(true);// Undo Piece.
                    }
                }
            }
        });
        button_admit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                admitGame(true);// Give up.
            }
        });
        button_pvp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                PvpDialog();//PVP
            }
        });
        button_settings.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                SettingsDialog();
            }
        });
        button_back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();// Close this activity.
            }
        });
        // About bluetooth.
        myBluetoothListener = MyBluetoothListener.getMyBluetoothListener();
        myBluetoothListener.setContext(this);
        myBluetoothListener.setGameActivity(this);
        bluetoothHelper=BluetoothHelper.getBluetoothHelper(myBluetoothListener,this,null);
        // Set SharedPreferences and get values from SharedPreferences file.
        preferences=getSharedPreferences("FusionChess",MODE_PRIVATE);
        editor=preferences.edit();
        boolean showPossibleMoves=preferences.getBoolean("SHOW_POSSIBLE_MOVES",true);
        chessView.setShowPossibleMoves(showPossibleMoves);
        chessView.setActivity(this); //Necessary.
        chessView.setBluetoothHelper(bluetoothHelper);
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
        Intent intent = new Intent("com.keshi.MUSIC");
        intent.putExtra("count", -1);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Write it in file.
        editor.putBoolean("SHOW_POSSIBLE_MOVES", chessView.getShowPossibleMoves());
        editor.commit();// Commit this change.
    }

    // Start new game.
    public void newGame(boolean myTypeIsRed){
        chessView.newGame(myTypeIsRed);
    }

    // Give up.
    public void admitGame(boolean whoIsMe){
        chessView.admitGame(whoIsMe);
        if(whoIsMe&&bluetoothHelper.isConnected()){
            sendMessage("admit");// Send message.
        }
    }

    public void undoPiece(boolean whoIsMe){// Run when receive "undo"
        if(whoIsMe){
            chessView.undoPiece(true);
        }
        else if(bluetoothHelper.isConnected()){
            AlertDialog undo_dialog=new AlertDialog.Builder(GameActivity.this)
                    .setMessage(R.string.dialog_undo_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_undo_agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendMessage("undoOK");
                            chessView.undoPiece(false);
                        }
                    })
                    .setNegativeButton(R.string.dialog_undo_not_agree, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendMessage("undoNot");
                        }
                    })
                    .create();
            undo_dialog.setCanceledOnTouchOutside(false);
            undo_dialog.show();// Show dialog.
        }
    }

    // A dialog for begin a new game.
    public void NewDialog(){
        AlertDialog.Builder new_builder=new AlertDialog.Builder(GameActivity.this);
        View mView= LayoutInflater.from(new_builder.getContext()).inflate(R.layout.view_dialog_new, null);
        // Get controls of the View.
        final Button button=(Button)mView.findViewById(R.id.button_create);
        // Set values of those controls.
        new_builder.setView(mView);
        new_builder.setIcon(R.mipmap.ic_launcher);
        new_builder.setTitle(R.string.dialog_new_title);
        // Set listener of controls
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String red=getString(R.string.dialog_new_red);
                button.setText(button.getText().equals(red)?
                        R.string.dialog_new_blue:R.string.dialog_new_red);
            }
        });
        new_builder.setPositiveButton(R.string.dialog_new_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String red=getString(R.string.dialog_new_red);
                boolean myTypeIsRed=button.getText().equals(red);
                newGame(myTypeIsRed);// Start new game.
                if(bluetoothHelper.isConnected()){
                    sendMessage(myTypeIsRed? "red":"blue");
                }
            }
        });
        new_builder.setNegativeButton(R.string.dialog_new_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(bluetoothHelper.isConnected()){
                    String red=getString(R.string.dialog_new_red);
                    boolean myTypeIsRed=button.getText().equals(red);
                    newGame(myTypeIsRed);// Start new game.
                    sendMessage(myTypeIsRed? "red":"blue");
                }
            }
        });
        new_builder.setCancelable(false);
        Dialog new_dialog=new_builder.create();// Crate dialog.
        new_dialog.setCanceledOnTouchOutside(false);
        new_dialog.show();// Show this dialog.
    }

    // A dialog for PVP.
    private void PvpDialog(){
        AlertDialog.Builder pvp_builder=new AlertDialog.Builder(GameActivity.this);
        View mView= LayoutInflater.from(pvp_builder.getContext()).inflate(R.layout.view_dialog_pvp, null);
        // Get controls of the View.
        Button button_create=(Button)mView.findViewById(R.id.button_create);
        Button button_join=(Button)mView.findViewById(R.id.button_join);
        // Set values of those controls.
        pvp_builder.setView(mView);
        pvp_builder.setIcon(R.mipmap.ic_launcher);
        pvp_builder.setTitle(R.string.dialog_pvp_title);
        // Set listener of controls
        button_create.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothHelper.isBluetoothEnable()) {// Start Bluetooth.
                    bluetoothHelper.bluetoothEnable();
                }
                if (!bluetoothHelper.isDiscovering()) {
                    bluetoothHelper.setBluetoothVisible(GameActivity.this);
                }
                WaitDialog();//Create game.
            }
        });
        button_join.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothHelper.isBluetoothEnable()) {// Start Bluetooth.
                    bluetoothHelper.bluetoothEnable();
                }
                JoinDialog();//Join game.
            }
        });
        pvp_dialog=pvp_builder.create();
        pvp_dialog.show();// Show this dialog.
    }

    private void WaitDialog(){
        wait_dialog=new ProgressDialog(this);
        wait_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        wait_dialog.setMessage(getString(R.string.dialog_wait_massage));
        wait_dialog.setIndeterminate(true);
        wait_dialog.setCancelable(false);
        wait_dialog.setButton( DialogInterface.BUTTON_NEGATIVE,
                getString(R.string.dialog_wait_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        wait_dialog.show();
    }

    //A dialog for join.
    private void JoinDialog(){
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        View mView= layoutInflater.inflate(R.layout.view_dialog_join, null);
        join_dialog=new AlertDialog.Builder(GameActivity.this)
                .setTitle(R.string.dialog_join_title)
                .setIcon(R.mipmap.ic_launcher)
                .setView(mView) .create();
        // Get controls of the View.
        list_bluetooth=(ListView)mView.findViewById(R.id.list_bluetooth);
        Button button_search=(Button)mView.findViewById(R.id.button_search);
        Button button_cancel=(Button)mView.findViewById(R.id.button_cancel);
        // Set listener of controls
        myBluetoothListener.setListView(list_bluetooth);
        button_search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!bluetoothHelper.isDiscovering()){
                    bluetoothHelper.startDiscover();// Start Discover devices.
                }
            }
        });
        button_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                join_dialog.cancel();
            }
        });
        join_dialog.setCanceledOnTouchOutside(false);
        join_dialog.show();// Show this dialog.
        if(!bluetoothHelper.isDiscovering()){
            bluetoothHelper.startDiscover();// Start Discover devices.
        }
    }

    // A dialog for options.
    private void SettingsDialog(){
        AlertDialog.Builder settings_builder=new AlertDialog.Builder(GameActivity.this);
        View mView= LayoutInflater.from(settings_builder.getContext()).inflate(R.layout.view_dialog_settings, null);
        // Get controls of the View.
        Switch switch_show_possible_moves=(Switch)mView.findViewById(R.id.switch_show_possible_moves);
        switch_show_possible_moves.setChecked(chessView.getShowPossibleMoves());
        // Set values of those controls.
        settings_builder.setView(mView);
        settings_builder.setIcon(R.mipmap.ic_launcher);
        settings_builder.setTitle(R.string.dialog_settings_title);
        // Set listener of controls
        switch_show_possible_moves.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                chessView.setShowPossibleMoves(b);
            }
        });
        settings_builder.setPositiveButton(R.string.dialog_settings_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Choose nothing.
            }
        });
        settings_builder.create().show();// Show this dialog.
    }

    public void showMessage(String message){// Show toasts.
        Toast toast=Toast.makeText(this,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public void movePiece(Point from,Point to,byte choosePiece){
        chessView.movePiece(from,to,choosePiece);
    }

    public void playSound(int resid){// Play Sounds.
        if(!MusicService.state_mute) {
            Intent intent = new Intent("com.keshi.MUSIC");
            intent.putExtra("playSound", resid);
            sendBroadcast(intent);
        }
    }

    public void closeDialog(){
        if(pvp_dialog!=null){
            pvp_dialog.cancel();//关闭等待窗口
            pvp_dialog=null;
        }
        if(wait_dialog!=null){
            wait_dialog.cancel();//关闭等待窗口
            pvp_dialog=null;
        }
        if(join_dialog!=null){
            join_dialog.cancel();//关闭加入窗口
            join_dialog=null;
        }
    }

    public void sendMessage(String message){
        if(bluetoothHelper.isConnected()){
            byte[] bytes= null;
            try {
                bytes = message.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            bluetoothHelper.send(bytes,0,bytes.length);
        }
    }

    public void closeConnect(){
        if(bluetoothHelper.isConnected()){
            bluetoothHelper.closeConnect();
        }
    }
}
