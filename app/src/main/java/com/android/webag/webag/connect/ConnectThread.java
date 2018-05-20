package com.android.webag.webag.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/5/20.
 */

public class ConnectThread extends Thread {

    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothSocket webagSocket;
    private final BluetoothDevice webagDevice;
    private BluetoothAdapter webagBluetoothAdapter;
    private final Handler webagHandler;
    private ConnectedThread webagConnectedThread;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        // Use a temporary object that is later assigned to webagSocket,
        // because webagSocket is final
        BluetoothSocket tmp = null;
        webagDevice = device;
        webagBluetoothAdapter = adapter;
        webagHandler = handler;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        webagSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        webagBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            webagSocket.connect();
        } catch (Exception connectException) {
            webagHandler.sendMessage(webagHandler.obtainMessage(Constant.MSG_ERROR, connectException));
            // Unable to connect; close the socket and get out
            try {
                webagSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(webagSocket);
    }

    private void manageConnectedSocket(BluetoothSocket webagSocket) {
        webagHandler.sendEmptyMessage(Constant.MSG_CONNECTED_TO_SERVER);
        webagConnectedThread = new ConnectedThread(webagSocket, webagHandler);
        webagConnectedThread.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            webagSocket.close();
        } catch (IOException e) { }
    }

    public void sendData(byte[] data) {
        if( webagConnectedThread!=null){
            Log.d("GOTMSG", "========================ConnectThread==========================data...." + data);
            webagConnectedThread.write(data);
        }
    }

}
