package com.android.webag.webag.connect;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2018/5/20.
 */

public class ConnectedThread extends Thread {

    private final BluetoothSocket webagSocket;
    private final InputStream webagInStream;
    private final OutputStream webagOutStream;
    private final Handler webagHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        webagSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        webagHandler = handler;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        webagInStream = tmpIn;
        webagOutStream = tmpOut;
    }


    public void run() {
        byte[] buffer = new byte[1024*15];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                Thread.sleep(100);
                Log.d("GOTMSG", "========================ConnecteddddddddddThread==========================");
                // Read from the InputStream
                bytes = webagInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if( bytes >0) {
                    Message message = webagHandler.obtainMessage(Constant.MSG_GOT_DATA, new String(buffer, 0, bytes, "utf-8"));
                    webagHandler.sendMessage(message);
                }
                Log.d("GOTMSG", "message size" + bytes);
            } catch (Exception e) {
                webagHandler.sendMessage(webagHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            webagOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            webagSocket.close();
        } catch (IOException e) { }
    }

}
