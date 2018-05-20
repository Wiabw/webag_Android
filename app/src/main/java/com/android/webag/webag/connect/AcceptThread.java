package com.android.webag.webag.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/5/20.
 */

public class AcceptThread extends Thread {

    private static final String NAME = "BlueToothClass";
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);

    private final BluetoothServerSocket webagServerSocket;
    private final BluetoothAdapter webagBluetoothAdapter;
    private final Handler webagHandler;
    private ConnectedThread webagConnectedThread;

    public AcceptThread(BluetoothAdapter adapter, Handler handler) {
        // Use a temporary object that is later assigned to webagServerSocket,
        // because webagServerSocket is final
        webagBluetoothAdapter = adapter;
        webagHandler = handler;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = webagBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        webagServerSocket = tmp;
    }
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                webagHandler.sendEmptyMessage(Constant.MSG_START_LISTENING);
                socket = webagServerSocket.accept();
            } catch (IOException e) {
                webagHandler.sendMessage(webagHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
                    webagServerSocket.close();
                    webagHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        //只支持同时处理一个连接
        if( webagConnectedThread != null) {
            webagConnectedThread.cancel();
        }
        webagHandler.sendEmptyMessage(Constant.MSG_GOT_A_CLINET);
        webagConnectedThread = new ConnectedThread(socket, webagHandler);
        webagConnectedThread.start();
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            webagServerSocket.close();
            webagHandler.sendEmptyMessage(Constant.MSG_FINISH_LISTENING);
        } catch (IOException e) { }
    }

    public void sendData(byte[] data) {
        if( webagConnectedThread!=null){
            webagConnectedThread.write(data);
        }
    }

}
