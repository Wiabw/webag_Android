package com.android.webag.webag;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.webag.webag.connect.AcceptThread;
import com.android.webag.webag.connect.ConnectThread;
import com.android.webag.webag.connect.Constant;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private WebagBlueToothController webagBlueToothController = new WebagBlueToothController();
    private static final int REQUEST_CODE = 0;
    private List<BluetoothDevice> webagDeviceList = new ArrayList<>();
    private List<BluetoothDevice> webagBondedDeviceList = new ArrayList<>();
    private Toast webagToast;
    private ListView webagListView;
    private DeviceAdapter webagDeviceAdapter;
    private AcceptThread webagAcceptThread;
    private ConnectThread webagConnectThread;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    showToast("STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
                    showToast("STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    showToast("STATE_TURNING_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    showToast("STATE_TURNING_OFF");
                    break;
                default:
                    showToast("Unkown State");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActionBar();
        setContentView(R.layout.activity_main);
        initUI();

        registerBluetoothReceiver();
        webagBlueToothController.trunOnBlueTooth(this, REQUEST_CODE);
    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        //开始查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        //结束查找
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //查找设备
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //设备扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        registerReceiver(webagReceiver, filter);
    }

    private Handler mUIHandler = new MyHandler();

    private BroadcastReceiver webagReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                setProgressBarIndeterminateVisibility(true);
                // 初始化数据列表
                webagDeviceList.clear();
                webagDeviceAdapter.notifyDataSetChanged();
            } else if( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
            } else if ( BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //找到一个，添加一个
                webagDeviceList.add(device);
                webagDeviceAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    setProgressBarIndeterminateVisibility(true);
                } else {
                    setProgressBarIndeterminateVisibility(false);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,0);
                if (status == BluetoothDevice.BOND_BONDED) {
                    showToast("Bonded " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_BONDING) {
                    showToast("Bonding " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    showToast("Not bond " + remoteDevice.getName());
                }
            }
        }
    };

    private void initUI() {
        webagListView = (ListView) findViewById(R.id.device_list);
        webagDeviceAdapter = new DeviceAdapter(webagDeviceList, this);
        webagListView.setAdapter(webagDeviceAdapter);
        webagListView.setOnItemClickListener(bindDeviceClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( webagAcceptThread != null) {
            webagAcceptThread.cancel();
        }
        if( webagConnectThread != null) {
            webagConnectThread.cancel();
        }
        unregisterReceiver(webagReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.enable_visiblity) {
            webagBlueToothController.enableVisibly(this);
        }
        else if( id == R.id.find_device) {
            //查找设备
            webagDeviceAdapter.refresh(webagDeviceList);
            webagBlueToothController.findDevice();
            webagListView.setOnItemClickListener(bindDeviceClick);
        }
        else if (id == R.id.bonded_device) {
            //查看已绑定设备
            webagBondedDeviceList = webagBlueToothController.getBondedDevicelist();
            webagDeviceAdapter.refresh(webagBondedDeviceList);
            webagListView.setOnItemClickListener(bindedDeviceClick);
        } else if( id == R.id.listening) {
            if( webagAcceptThread != null) {
                webagAcceptThread.cancel();
            }
            webagAcceptThread = new AcceptThread(webagBlueToothController.getAdapter(), mUIHandler);
            webagAcceptThread.start();
        }
        else if( id == R.id.stop_listening) {
            if( webagAcceptThread != null) {
                webagAcceptThread.cancel();
            }
        }
        else if( id == R.id.disconnect) {
            if( webagConnectThread != null) {
                webagConnectThread.cancel();
            }
        }
        else if( id == R.id.say_hello) {
            say("Hello");
        }
        else if( id == R.id.say_hi) {
            say("Hi");
        }
        return super.onOptionsItemSelected(item);
    }

    private void say(String word) {
        if( webagAcceptThread != null) {
            try {
                webagAcceptThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        else if( webagConnectThread != null) {
            try {
                webagConnectThread.sendData(word.getBytes("utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = webagBondedDeviceList.get(i);
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                device.createBond();
            }
        }
    };

    private AdapterView.OnItemClickListener bindedDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BluetoothDevice device = webagBondedDeviceList.get(i);
            if( webagConnectThread != null) {
                webagConnectThread.cancel();
            }
            webagConnectThread = new ConnectThread(device, webagBlueToothController.getAdapter(), mUIHandler);
            webagConnectThread.start();
        }
    };

    private void initActionBar() {
        try {
            requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayUseLogoEnabled(false);
            setProgressBarIndeterminate(true);
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    setProgressBarIndeterminateVisibility(true);
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case Constant.MSG_GOT_DATA:
                    showToast("data: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_ERROR:
                    showToast("error: "+String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    showToast("Connected to Server");
                    break;
                case Constant.MSG_GOT_A_CLINET:
                    showToast("Got a Client");
                    break;
            }
        }
    }

    private void showToast(String text){
        if( webagToast == null ) {
            webagToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            webagToast.setText(text);
        }
        webagToast.show();
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.enable_visiblity) {
            webagBlueToothController.enableVisibly(this);
        } else if (id == R.id.find_device) {
            webagDeviceAdapter.refresh(webagDeviceList);
            webagBlueToothController.findDevice();
            webagListView.setOnItemClickListener(bindDeviceClick);
        } else if (id == R.id.bonded_device) {
            webagBondedDeviceList = webagBlueToothController.getBondedDevicelist();
            webagDeviceAdapter.refresh(webagDeviceList);
            webagListView.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK ) {
            showToast("打开成功");
        } else {
            showToast("打开失败");
        }
    }

    public void isSupportBlueTooth(View view) {
        boolean ret = webagBlueToothController.isSupportBlueTooth();
        showToast("support BlueTooth? " + ret) ;
    }
    public void isBlueToothEnable(View view) {
        boolean ret = webagBlueToothController.getBlueToothStatus();
        showToast("BlueTooth Enable? " + ret) ;
    }
    public void requestTrunOnBlueTooth(View view) {
        webagBlueToothController.trunOnBlueTooth(this, REQUEST_CODE);
    }
    public void rewuestTurnOffBlueTooth(View view) {
        webagBlueToothController.trunOffBlueTooth();
    }
}
