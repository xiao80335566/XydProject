package com.xyd.project.android;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;

import xydproject.pluginutil.util.SPUtils;


/**
 * Created by xiaojun on 2017/5/19.
 */

public class XydApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //这里必须在super.onCreate方法之后，顺序不能变
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
        SPUtils.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }
}
