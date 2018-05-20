package com.android.webag.webag;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙适配器
 * Created by Administrator on 2018/5/19.
 */

public class WebagBlueToothController {

    private BluetoothAdapter webagBluetoolAdapter;

    public WebagBlueToothController() {
        webagBluetoolAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter() {
        return webagBluetoolAdapter;
    }

    /**
     * 是否支持蓝牙
     * @return
     */
    public boolean isSupportBlueTooth() {
        if(webagBluetoolAdapter != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前蓝牙状态
     * @return
     */
    public boolean getBlueToothStatus() {
        assert (webagBluetoolAdapter != null);
        return webagBluetoolAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     * @param activity
     * @param requestCode
     */
    public void trunOnBlueTooth(Activity activity, int requestCode) {
        Intent initent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(initent, requestCode);
    }

    /**
     * 关闭蓝牙
     */
    public void trunOffBlueTooth() {
        webagBluetoolAdapter.disable();
    }
    /**
     * 打开蓝牙是否可见
     * @param context
     */
    public void enableVisibly(Context context) {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(discoverableIntent);
    }

    /**
     * 查找设备
     */
    public void findDevice() {
        assert (webagBluetoolAdapter != null );
        webagBluetoolAdapter.startDiscovery();
    }
    public List<BluetoothDevice> getBondedDevicelist() {
        return new ArrayList<BluetoothDevice>(webagBluetoolAdapter.getBondedDevices());
    }

}





















