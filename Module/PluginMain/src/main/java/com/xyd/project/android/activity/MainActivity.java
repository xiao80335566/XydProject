package com.xyd.project.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xyd.project.R;
import com.xyd.project.android.BaseActivity;
import com.xyd.project.android.adapter.MyRecyclerAdapter;
import com.xyd.project.common.callback.ItemClickListener;
import com.xyd.project.common.callback.ItemLongClickListener;

import xydproject.pluginutil.util.ToastUtils;

/**
 * Created by xiaojun on 2017/5/19.
 */

public class MainActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private String[] md = new String[20];
    private MyRecyclerAdapter adapter;

    @Override
    public void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void initFindViewById() {

    }

    @Override
    protected void initEvent() {
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(int position) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, PluginActivity.class);
                        MainActivity.this.startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(MainActivity.this, NetTestActivity.class);
                        MainActivity.this.startActivity(intent2);
                        break;

                }
            }
        });
        adapter.setItemLongClickListener(new ItemLongClickListener() {
            @Override
            public void itemLongClick(int position) {
                ToastUtils.showShort(MainActivity.this, "长按==" + position);
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        md = new String[]{"跳转到插件", "模拟网络请求"};
        setTitleStr("首页");
        adapter = new MyRecyclerAdapter(this, md);
        recyclerView.setAdapter(adapter);
        backVisibility(View.GONE);
    }
}
