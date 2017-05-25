package com.xyd.project.android;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyd.project.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/22.
 */

public abstract class BaseActivity extends AppCompatActivity {


    /**
     * 记录处于前台的Activity
     */
    private static BaseActivity mForegroundActivity = null;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initFindViewById();
        initView();
        initData();
        back();
        initEvent();
    }

    public void setTitleStr(String titleStr) {
        TextView titleTv = (TextView) findViewById(R.id.title_tv);
        if (titleTv != null) {
            titleTv.setText(titleStr);
        }
    }

    private void back() {
        ImageView back = (ImageView) findViewById(R.id.back_img);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mForegroundActivity != null) {
                        mForegroundActivity.finish();
                    }
                }
            });
        }
    }

    /**
     * 返回键的隐藏显示
     */
    public void backVisibility(int viewStatus) {
        ImageView back = (ImageView) findViewById(R.id.back_img);
        if (back != null) {
            back.setVisibility(viewStatus);
        }
    }

    @Override
    protected void onResume() {
        mForegroundActivity = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
        super.onPause();
    }

    public abstract void initView();

    public abstract int getContentViewId();

    public abstract void initData();

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<>();
        }

        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }

    /**
     * 是否有启动的Activity
     */
    public static boolean hasActivity() {
        return mActivities.size() > 0;
    }

    /**
     * 获取当前处于前台的activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    protected void initFindViewById() {

    }

    protected void initEvent() {
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        finishAll();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}

