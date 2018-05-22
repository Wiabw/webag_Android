package com.android.webag.webag.application;

import org.xutils.BuildConfig;
import org.xutils.x;

import android.app.Application;

/**
 *
 * @Copyright: 2017 tongcai. Version V1.0
 * @Author: yunfengl
 * @Date: 2017-2-4上午9:55:11
 * @Email: liuyunfeng-1231@163.com
 * @Function: 初始化网络框架
 *
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志，开启debug会影响�?�?

    }
}
