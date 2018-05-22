package com.android.webag.webag.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

/**
 * Created by: tongcai. Version V1.0
 * Author: yunfengl
 * time: 2017/3/7 14:37
 * Email: liuyunfeng-1231@163.com
 * 基础 activity
 */
public class BaseActivity extends FragmentActivity {

    private FragmentManager fm;

    private FragmentTransaction transaction;

    public int id;

    private long exitTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }


    /**
     * 返回处理
     */
    public void goBack() {
        if (fm.getBackStackEntryCount() > 1) {
            fm.popBackStack();
        } else {
            if (this instanceof MainActivity) {
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                    onResume();
                } else {
                    long time = System.currentTimeMillis();
                    if (time - exitTime > 500) {
                        exitTime = time;
                        Toast.makeText(this, "再次点击返回键退出应用", Toast.LENGTH_SHORT).show();
                    } else {
                        finish();
                    }
                }

            } else {
                finish();
            }

        }
    }


    public void changeFragment(int id, Fragment to) {
        transaction = fm.beginTransaction();
        transaction.replace(id, to);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
