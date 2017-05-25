package com.xyd.project.android.activity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xyd.project.R;
import com.xyd.project.android.BaseActivity;
import com.xyd.project.common.log.LogUtils;

import xydproject.pluginutil.net.HttpRequest;
import xydproject.pluginutil.net.IHttpCallback;

/**
 * Created by Administrator on 2017/5/24.
 */

public class NetTestActivity extends BaseActivity {

    private Button netGetBtn, netPostBtn;
    private TextView contentTv;

    @Override
    public void initView() {
        netGetBtn = (Button) findViewById(R.id.net_get_btn);
        netPostBtn = (Button) findViewById(R.id.net_post_btn);
        contentTv = (TextView) findViewById(R.id.content_tv);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_net;
    }

    @Override
    public void initData() {
        setTitleStr("网络模拟");
    }

    @Override
    protected void initEvent() {
        netGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.baidu.com/";
                HttpRequest httpRequest = new HttpRequest(url, null, NetTestActivity.this, new IHttpCallback() {
                    @Override
                    public void onResponse(String result) {
                        LogUtils.i(System.currentTimeMillis() + "------initEvent");
                        contentTv.setText("请求结果：" + result);
                    }
                });
                httpRequest.setRequestMode(HttpRequest.HTTP_TYPE.GET);
                httpRequest.execute();
            }
        });
        netPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
