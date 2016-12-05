package com.keshi.chess.eventListener;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keshi.chess.jjtx.bluetoothutil.util.BluetoothHelper;
import com.keshi.chess.jjtx.bluetoothutil.util.BluetoothHelperListener;
import com.keshi.chess.R;
import com.keshi.chess.activity.GameActivity;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class MyBluetoothListener implements BluetoothHelperListener {
    public static final int LINK_SUCCESS=1;
    public static final int RECEIVED_MESSAGE = 2;

    private GameActivity gameActivity;
    private Context context;
    private ListView listView;


    public void setGameActivity(GameActivity gameActivity){
        this.gameActivity=gameActivity;
    }

    private static MyBluetoothListener myBluetoothListener = null;

    public static MyBluetoothListener getMyBluetoothListener() {
        if (myBluetoothListener == null) {
            myBluetoothListener = new MyBluetoothListener();
        }
        return myBluetoothListener;
    }

    private MyBluetoothListener() {

    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    private void showToast(int msg) {
        Toast toast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    @Override
    public void onFoundDevices(List<BluetoothDevice> devices) {
        listView.setAdapter(new MyAdapter(devices, context));
    }

    @Override
    public void onGetSocketConnect() {// Connect success.
        gameActivity.closeDialog();// Close Dialog.
        handler.sendEmptyMessage(LINK_SUCCESS);
    }

    @Override
    public void onGetDeviceConnect(BluetoothDevice device) {
    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onBeginReceivedMsg() {

    }

    @Override
    public void onReceivedMsg(byte[] bytes, int length) {// Receive message.
        String msg = null;
        try {
            msg = new String(bytes, 0, length, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Message message = new Message();
        message.what = RECEIVED_MESSAGE;
        message.obj = msg;

        handler.sendMessage(message);
    }

    @Override
    public void onEndReceivedMsg() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<BluetoothDevice> devices;

        public MyAdapter(List<BluetoothDevice> devices, Context context) {
            this.devices = devices;
            this.context = context;
        }


        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_join_item, null);
            final BluetoothDevice device = devices.get(position);
            TextView deviceTv = (TextView) view.findViewById(R.id.device_name);
            deviceTv.setText(device.getName());
            deviceTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showToast(R.string.message_linking);
                    BluetoothHelper helper = BluetoothHelper.getBluetoothHelper();
                    helper.startConnect(device.getAddress());
                    gameActivity.closeDialog();//关闭窗口
                }
            });
            return view;
        }
    }

    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LINK_SUCCESS:// Link success.
                    gameActivity.sendMessage("success");
                    showToast(R.string.message_linked_success);
                    gameActivity.NewDialog();//选则棋子类型
                    break;
                case RECEIVED_MESSAGE:// Have been linked success.
                    String message=(String)msg.obj;
                    if(message.contains(" ")){// Move
                        String[] list=message.split(" ");
                        int fromX=Integer.valueOf(list[0]);
                        int fromY=Integer.valueOf(list[1]);
                        int toX=Integer.valueOf(list[2]);
                        int toY=Integer.valueOf(list[3]);
                        byte choosePiece=Byte.valueOf(list[4]);
                        gameActivity.movePiece(new Point(fromX,fromY),new Point(toX,toY),choosePiece);
                    }
                    else if(message.equals("undo")){//对方要求悔棋
                        gameActivity.undoPiece(false);
                    }
                    else if(message.equals("undoOK")){//撤销成功
                        showToast(R.string.message_undo_agree);
                        gameActivity.undoPiece(true);
                    }
                    else if(message.equals("undoNot")){//撤销失败
                        showToast(R.string.message_undo_disagree);
                    }
                    else if(message.equals("success")){
                        showToast(R.string.message_link_success);
                    }
                    else if(message.equals("red")){//房主选择红色
                        gameActivity.newGame(false);
                    }
                    else if(message.equals("blue")){//房主选择蓝色
                        gameActivity.newGame(true);
                    }
                    else if(message.equals("disconnect")){//失去连接
                        showToast(R.string.message_disconnect);
                        gameActivity.closeConnect();//断开连接
                    }
                    else if(message.equals("admit")){
                        gameActivity.admitGame(false);//对方认输
                    }
                    break;
            }
        }
    };
}
