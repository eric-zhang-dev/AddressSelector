package com.example.appforsql.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric.zhang on 2018/1/8.
 */

public abstract class BaseTadapter<T> extends BaseAdapter {

    protected List<T> dataList;
    protected LayoutInflater inflater;
    protected Activity context;
    protected Fragment fragment;
//    protected ImageLoader imageLoader;

    public BaseTadapter(Activity context) {
        super();
        this.context = context;
        init();
    }

    public BaseTadapter(Fragment fragment) {
        super();
        this.fragment = fragment;
        this.context = fragment.getActivity();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        dataList = new ArrayList<T>();
        inflater = LayoutInflater.from(context);
//        imageLoader = ImageLoader.getInstance(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<T> getDataList() {
        return dataList;
    }
}
