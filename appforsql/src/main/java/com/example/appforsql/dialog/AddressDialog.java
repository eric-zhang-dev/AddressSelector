package com.example.appforsql.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appforsql.R;
import com.example.appforsql.adapter.BaseTadapter;
import com.example.appforsql.common.Constant;
import com.example.appforsql.db.DBManager;
import com.example.appforsql.listener.OnAddressSelectedListener;
import com.example.appforsql.pojo.Distinct;
import com.example.appforsql.utils.DensityUtil;
import com.example.appforsql.utils.ListUtils;
import com.example.appforsql.utils.SharedUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddressDialog extends Dialog implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Window dialogWindow;
    private LayoutInflater inflater;
    private View view, indicator;
    private ListView localListView;
    private DBManager<Distinct> dbManager;
    private static final int INDEX_TAB_PROVINCE = 0;
    private static final int INDEX_TAB_CITY = 1;
    private static final int INDEX_TAB_COUNTY = 2;
    private static final int INDEX_TAB_STREET = 3;
    private static final int INDEX_TAB_BR5 = 4;
    private static final int INDEX_TAB_BR6 = 5;
    private static final int INDEX_INVALID = -1;
    private static final int WHAT_PROVINCES_SELECTED = 0;
    private static final int WHAT_CITIES_SELECTED = 1;
    private static final int WHAT_COUNTIES_SELECTED = 2;
    private static final int WHAT_STREETS_SELECTED = 3;
    private static final int WHAT_BR5_SELECTED = 4;
    private static final int WHAT_BR6_SELECTED = 5;
    private List<Distinct> privaceList;
    private List<Distinct> areaList;
    private List<Distinct> shiList;
    private List<Distinct> streetList;
    private List<Distinct> xiangcunList;
    private List<Distinct> guanliList;
    private Local1Adapter local1Adapter;
    private Local2Adapter local2Adapter;
    private Local3Adapter local3Adapter;
    private Local4Adapter local4Adapter;
    private Local5Adapter local5Adapter;
    private Local6Adapter local6Adapter;
    private boolean isNull = false;
    private Activity mContext;
    private TextView mProvince, mCity, mArea, mStreet, mXiangCun, mGuanli,ok,cancel;
    private int provinceIndex = INDEX_INVALID;
    private int cityIndex = INDEX_INVALID;
    private int countyIndex = INDEX_INVALID;
    private int streetIndex = INDEX_INVALID;
    private int br5Index = INDEX_INVALID;
    private int br6Index = INDEX_INVALID;
    private int tabIndex = INDEX_TAB_PROVINCE;
    private String sql;
    private HorizontalScrollView scrollView;
    private OnAddressSelectedListener mAddressSelectedListener;

    public AddressDialog(Activity context, OnAddressSelectedListener addressSelectedListener) {
        super(context, R.style.bottom_dialog);
        this.mContext = context;
        this.mAddressSelectedListener = addressSelectedListener;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogWindow = getWindow();
        view = inflater.inflate(R.layout.activity_select_local_pop, null);
        setContentView(view);
        // settings
        setCanceledOnTouchOutside(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        dialogWindow.setBackgroundDrawable(dw);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DensityUtil.dip2px(mContext, 280);
        dialogWindow.setAttributes(params);
        dialogWindow.setGravity(Gravity.BOTTOM);
        initView();
        initAdapter();
        initData();
    }

    private void initView() {
        localListView =  view.findViewById(R.id.local_list);
        scrollView =  view.findViewById(R.id.scrollView);
        ok =  view.findViewById(R.id.ok);
        cancel =  view.findViewById(R.id.cancel);
        indicator = findViewById(R.id.indicator);
        mProvince =  view.findViewById(R.id.tv_1);
        mCity =  view.findViewById(R.id.tv_2);
        mArea =  view.findViewById(R.id.tv_3);
        mStreet =  view.findViewById(R.id.tv_4);
        mXiangCun =  view.findViewById(R.id.tv_5);
        mGuanli =  view.findViewById(R.id.tv_6);
        mProvince.setOnClickListener(this);
        mCity.setOnClickListener(this);
        mArea.setOnClickListener(this);
        mStreet.setOnClickListener(this);
        mXiangCun.setOnClickListener(this);
        mGuanli.setOnClickListener(this);
        ok.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);
        localListView.setOnItemClickListener(this);
        updateIndicator();
    }

    private void initAdapter() {
        local1Adapter = new Local1Adapter(mContext);
        local2Adapter = new Local2Adapter(mContext);
        local3Adapter = new Local3Adapter(mContext);
        local4Adapter = new Local4Adapter(mContext);
        local5Adapter = new Local5Adapter(mContext);
        local6Adapter = new Local6Adapter(mContext);
    }

    private void initData() {
        if (!TextUtils.isEmpty(Constant.address0)) {
            isNull = true;
        }
        selectPrivace();
    }

    private void selectPrivace() {
        dbManager = new DBManager<Distinct>(Distinct.class);
        dbManager.openDatabase();
        sql = "select * from area where DistrictLevel='0'";
        privaceList = dbManager.getBySql(sql, null, null);
        dbManager.closeDatabase();
        handler.sendMessage(Message.obtain(handler, WHAT_PROVINCES_SELECTED, privaceList));
    }

    private void selectShi(String no) {
        handler.sendMessage(Message.obtain(handler, WHAT_CITIES_SELECTED, getResList(no)));
    }

    private void selectArea(String no) {
        handler.sendMessage(Message.obtain(handler, WHAT_COUNTIES_SELECTED, getResList(no)));
    }

    private void selectStreet(String no) {
        handler.sendMessage(Message.obtain(handler, WHAT_STREETS_SELECTED, getResList(no)));
    }

    private void selectXiang(String no) {
        handler.sendMessage(Message.obtain(handler, WHAT_BR5_SELECTED, getResList(no)));
    }

    private void selectGuanli(String no) {
        handler.sendMessage(Message.obtain(handler, WHAT_BR6_SELECTED, getResList(no)));
    }
    private List<Distinct> getResList(String no){
        List<Distinct> list = new ArrayList<>();
        dbManager = new DBManager<Distinct>(Distinct.class);
        dbManager.openDatabase();
        String sql = "select * from area where ParentDistrictNo  =" + "'" + no + "'";
        list = dbManager.getBySql(sql, null, null);
        dbManager.closeDatabase();
        return list;
    }
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ok:
                    if (mAddressSelectedListener != null) {
                        Distinct address0 = privaceList == null || provinceIndex == INDEX_INVALID ? null : privaceList.get(provinceIndex);
                        Distinct address1 = shiList == null || cityIndex == INDEX_INVALID ? null : shiList.get(cityIndex);
                        Distinct address2 = areaList == null || countyIndex == INDEX_INVALID ? null : areaList.get(countyIndex);
                        Distinct address3 = streetList == null || streetIndex == INDEX_INVALID ? null : streetList.get(streetIndex);
                        Distinct address4 = xiangcunList == null || br5Index == INDEX_INVALID ? null : xiangcunList.get(br5Index);
                        Distinct address5 = guanliList == null || br6Index == INDEX_INVALID ? null : guanliList.get(br6Index);
                        if (address2==null){
                            Toast.makeText(mContext,"亲,至少选择三级地址哦",Toast.LENGTH_LONG).show();
                            return;
                        }
                        mAddressSelectedListener.onFulldAddress(address0, address1, address2, address3, address4, address5);
                        dismiss();}
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_1:
                tabIndex = INDEX_TAB_PROVINCE;
                localListView.setAdapter(local1Adapter);
                if (provinceIndex != INDEX_INVALID) {
                    localListView.setSelection(provinceIndex);
                }
                break;
            case R.id.tv_2:
                tabIndex = INDEX_TAB_CITY;
                localListView.setAdapter(local2Adapter);
                if (cityIndex != INDEX_INVALID) {
                    localListView.setSelection(cityIndex);
                }
                break;
            case R.id.tv_3:
                localListView.setAdapter(local3Adapter);
                tabIndex = INDEX_TAB_COUNTY;
                if (countyIndex != INDEX_INVALID) {
                    localListView.setSelection(countyIndex);
                }
                break;
            case R.id.tv_4:
                localListView.setAdapter(local4Adapter);
                tabIndex = INDEX_TAB_STREET;
                if (streetIndex != INDEX_INVALID) {
                    localListView.setSelection(streetIndex);
                }
                break;
            case R.id.tv_5:
                tabIndex = INDEX_TAB_BR5;
                localListView.setAdapter(local5Adapter);
                if (br5Index != INDEX_INVALID) {
                    localListView.setSelection(br5Index);
                }
                break;
            case R.id.tv_6:
                localListView.setAdapter(local6Adapter);
                tabIndex = INDEX_TAB_BR6;
                if (br6Index != INDEX_INVALID) {
                    localListView.setSelection(br6Index);
                }
                break;
        }
        updateTabsVisibility();
        updateIndicator();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (isNull) {
            isNull = false;
        }
        switch (tabIndex) {
            case INDEX_TAB_PROVINCE:
                proviceItem(position);
                break;

            case INDEX_TAB_CITY:
                cityItem(position);
                break;

            case INDEX_TAB_COUNTY:
                countItem(position);
                break;

            case INDEX_TAB_STREET:
                streetItem(position);
                break;
            case INDEX_TAB_BR5:
                br5Item(position);
                break;
            case INDEX_TAB_BR6:
                br6Item(position);
                break;
        }
        updateTabsVisibility();
        updateIndicator();
    }

    private void updateTabsVisibility() {
        mProvince.setVisibility(ListUtils.notEmpty(privaceList) ? View.VISIBLE : View.GONE);
        mCity.setVisibility(ListUtils.notEmpty(shiList) ? View.VISIBLE : View.GONE);
        mArea.setVisibility(ListUtils.notEmpty(areaList) ? View.VISIBLE : View.GONE);
        mStreet.setVisibility(ListUtils.notEmpty(streetList) ? View.VISIBLE : View.GONE);
        mXiangCun.setVisibility(ListUtils.notEmpty(xiangcunList) ? View.VISIBLE : View.GONE);
        mGuanli.setVisibility(ListUtils.notEmpty(guanliList) ? View.VISIBLE : View.GONE);
        mProvince.setEnabled(tabIndex != INDEX_TAB_PROVINCE);
        mCity.setEnabled(tabIndex != INDEX_TAB_CITY);
        mArea.setEnabled(tabIndex != INDEX_TAB_COUNTY);
        mStreet.setEnabled(tabIndex != INDEX_TAB_STREET);
        mXiangCun.setEnabled(tabIndex != INDEX_TAB_BR5);
        mGuanli.setEnabled(tabIndex != INDEX_TAB_BR6);
    }

    public class Local1Adapter extends BaseTadapter<Distinct> {
        public Local1Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());

            boolean checked = provinceIndex != INDEX_INVALID && dataList.get(provinceIndex).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    public class Local2Adapter extends BaseTadapter<Distinct> {
        public Local2Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());
            boolean checked = cityIndex != INDEX_INVALID && dataList.get(cityIndex).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    public class Local3Adapter extends BaseTadapter<Distinct> {
        public Local3Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());
            boolean checked = countyIndex != INDEX_INVALID && dataList.get(countyIndex).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    public class Local4Adapter extends BaseTadapter<Distinct> {
        public Local4Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());
            boolean checked = streetIndex != INDEX_INVALID && dataList.get(streetIndex).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    public class Local5Adapter extends BaseTadapter<Distinct> {
        public Local5Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());
            boolean checked = br5Index != INDEX_INVALID && dataList.get(br5Index).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    public class Local6Adapter extends BaseTadapter<Distinct> {
        public Local6Adapter(Activity context) {
            super(context);
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.local_list, null);
                viewHolder.mLocalName = (TextView) convertView.findViewById(R.id.name);
                viewHolder.mLocalIs = (ImageView) convertView.findViewById(R.id.is);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Distinct distinct = dataList.get(i);
            viewHolder.mLocalName.setText(distinct.getDistrictName());
            boolean checked = br6Index != INDEX_INVALID && dataList.get(br6Index).getDistrictNo() == distinct.getDistrictNo();
            viewHolder.mLocalName.setEnabled(!checked);
            viewHolder.mLocalIs.setVisibility(checked ? View.VISIBLE : View.GONE);
            return convertView;
        }

        public class ViewHolder {
            public TextView mLocalName;
            public ImageView mLocalIs;
        }
    }

    /**
     * 获取默认项
     *
     * @param province
     * @return
     */
    public int getLocalItem(List<Distinct> list, String province) {
        int size = list.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (province.equals(list.get(i).getDistrictName())) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            provinceIndex = 0;
            return provinceIndex;
        }
        return provinceIndex;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_PROVINCES_SELECTED:
                    privaceList = (List<Distinct>) msg.obj;
                    local1Adapter.setDataList(privaceList);
                    local1Adapter.notifyDataSetChanged();
                    localListView.setAdapter(local1Adapter);
                    ////////////
                    if (isNull) {
                        proviceItem(getLocalItem(privaceList, Constant.address0));
                    }
                    break;

                case WHAT_CITIES_SELECTED:
                    shiList = (List<Distinct>) msg.obj;
                    local2Adapter.notifyDataSetChanged();
                    if (ListUtils.notEmpty(shiList)) {
                        // 以次级内容更新列表
                        local2Adapter.setDataList(shiList);
                        localListView.setAdapter(local2Adapter);
                        // 更新索引为次级
                        tabIndex = INDEX_TAB_CITY;
                    } else {
                        // 次级无内容，回调
                        callbackInternal();
                    }
                    //////////
                    if (isNull) {
                        cityItem(getLocalItem(shiList, Constant.address1));
                    }
                    break;

                case WHAT_COUNTIES_SELECTED:
                    areaList = (List<Distinct>) msg.obj;
                    local3Adapter.notifyDataSetChanged();
                    if (ListUtils.notEmpty(areaList)) {
                        local3Adapter.setDataList(areaList);
                        localListView.setAdapter(local3Adapter);
                        tabIndex = INDEX_TAB_COUNTY;
                    } else {
                        callbackInternal();
                    }
                    ///////////////////
                    if (isNull) {
                        countItem(getLocalItem(areaList, Constant.address2));
                    }
                    break;

                case WHAT_STREETS_SELECTED:
                    streetList = (List<Distinct>) msg.obj;
                    local4Adapter.notifyDataSetChanged();
                    if (ListUtils.notEmpty(streetList)) {
                        local4Adapter.setDataList(streetList);
                        localListView.setAdapter(local4Adapter);
                        tabIndex = INDEX_TAB_STREET;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (ListUtils.notEmpty(streetList) && !TextUtils.isEmpty(Constant.address3)) {
                            streetItem(getLocalItem(streetList, Constant.address3));
                        }
                    }
                    break;
                case WHAT_BR5_SELECTED:
                    xiangcunList = (List<Distinct>) msg.obj;
                    local5Adapter.notifyDataSetChanged();
                    if (ListUtils.notEmpty(xiangcunList)) {
                        local5Adapter.setDataList(xiangcunList);
                        localListView.setAdapter(local5Adapter);
                        tabIndex = INDEX_TAB_BR5;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (ListUtils.notEmpty(xiangcunList) && !TextUtils.isEmpty(Constant.address4)) {
                            br5Item(getLocalItem(xiangcunList, Constant.address4));
                        }
                    }
                    break;
                case WHAT_BR6_SELECTED:
                    guanliList = (List<Distinct>) msg.obj;
                    local6Adapter.notifyDataSetChanged();
                    if (ListUtils.notEmpty(guanliList)) {
                        local6Adapter.setDataList(guanliList);
                        localListView.setAdapter(local6Adapter);
                        tabIndex = INDEX_TAB_BR6;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (ListUtils.notEmpty(guanliList) && !TextUtils.isEmpty(Constant.address5)) {
                            br6Item(getLocalItem(guanliList, Constant.address5));
                        }
                    }
                    break;
            }
            updateTabsVisibility();
            updateIndicator();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100L);

            return true;
        }
    });


    /**
     * 第一级点击事件
     *
     * @param position
     */
    private void proviceItem(int position) {
        Distinct province = (Distinct) local1Adapter.getItem(position);
        // 更新当前级别及子级标签文本
        mProvince.setText(province.getDistrictName());
        mCity.setText("请选择");
        mArea.setText("请选择");
        mStreet.setText("请选择");
        mXiangCun.setText("请选择");
        mGuanli.setText("请选择");
        // 更新选中效果
        local1Adapter.notifyDataSetChanged();
        selectShi(province.getDistrictNo());
        // 更新子级数据
        shiList = null;
        areaList = null;
        streetList = null;
        xiangcunList = null;
        guanliList = null;
        local2Adapter.notifyDataSetChanged();
        local3Adapter.notifyDataSetChanged();
        local4Adapter.notifyDataSetChanged();
        local5Adapter.notifyDataSetChanged();
        local6Adapter.notifyDataSetChanged();
        // 更新选中数据
        this.provinceIndex = position;
        this.cityIndex = INDEX_INVALID;
        this.countyIndex = INDEX_INVALID;
        this.streetIndex = INDEX_INVALID;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第二级点击事件
     *
     * @param position
     */
    private void cityItem(int position) {
        Distinct city = (Distinct) local2Adapter.getItem(position);
        mCity.setText(city.getDistrictName());
        mArea.setText("请选择");
        mStreet.setText("请选择");
        mXiangCun.setText("请选择");
        mGuanli.setText("请选择");
        local2Adapter.notifyDataSetChanged();
        selectArea(city.getDistrictNo());
        areaList = null;
        streetList = null;
        xiangcunList = null;
        guanliList = null;
        local3Adapter.notifyDataSetChanged();
        local4Adapter.notifyDataSetChanged();
        local5Adapter.notifyDataSetChanged();
        local6Adapter.notifyDataSetChanged();
        this.cityIndex = position;
        this.countyIndex = INDEX_INVALID;
        this.streetIndex = INDEX_INVALID;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第三极点击事件
     *
     * @param position
     */
    private void countItem(int position) {
        Distinct county = (Distinct) local3Adapter.getItem(position);
        mArea.setText(county.getDistrictName());
        mStreet.setText("请选择");
        mXiangCun.setText("请选择");
        mGuanli.setText("请选择");
        local3Adapter.notifyDataSetChanged();
        selectStreet(county.getDistrictNo());
        streetList = null;
        xiangcunList = null;
        guanliList = null;
        local4Adapter.notifyDataSetChanged();
        local5Adapter.notifyDataSetChanged();
        local6Adapter.notifyDataSetChanged();
        this.countyIndex = position;
        this.streetIndex = INDEX_INVALID;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第三极点击事件
     *
     * @param position
     */
    private void streetItem(int position) {
        Distinct street = (Distinct) local4Adapter.getItem(position);
        mXiangCun.setText("请选择");
        mGuanli.setText("请选择");
        mStreet.setText(street.getDistrictName());
        local4Adapter.notifyDataSetChanged();
        selectXiang(street.getDistrictNo());
        xiangcunList = null;
        guanliList = null;
        local5Adapter.notifyDataSetChanged();
        local6Adapter.notifyDataSetChanged();
        this.streetIndex = position;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }


    private void br5Item(int position) {
        Distinct br5 = (Distinct) local5Adapter.getItem(position);
        mXiangCun.setText(br5.getDistrictName());
        mGuanli.setText("请选择");
        local5Adapter.notifyDataSetChanged();
        selectGuanli(br5.getDistrictNo());
        guanliList = null;
        local5Adapter.notifyDataSetChanged();
        this.br5Index = position;
        this.br6Index = INDEX_INVALID;
    }

    private void br6Item(int position) {
        Distinct br6 = (Distinct) local6Adapter.getItem(position);
        mGuanli.setText(br6.getDistrictName());
        local6Adapter.notifyDataSetChanged();
        this.br6Index = position;
        callbackInternal();
    }

    private void updateIndicator() {
        view.post(new Runnable() {
            @Override
            public void run() {
                switch (tabIndex) {
                    case INDEX_TAB_PROVINCE:
                        buildIndicatorAnimatorTowards(mProvince).start();
                        break;
                    case INDEX_TAB_CITY:
                        buildIndicatorAnimatorTowards(mCity).start();
                        break;
                    case INDEX_TAB_COUNTY:
                        buildIndicatorAnimatorTowards(mArea).start();
                        break;
                    case INDEX_TAB_STREET:
                        buildIndicatorAnimatorTowards(mStreet).start();
                        break;
                    case INDEX_TAB_BR5:
                        buildIndicatorAnimatorTowards(mXiangCun).start();
                        break;
                    case INDEX_TAB_BR6:
                        buildIndicatorAnimatorTowards(mGuanli).start();
                        break;
                }
            }
        });
    }

    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = indicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            }
        });
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new DecelerateInterpolator());
        set.playTogether(xAnimator, widthAnimator);
        return set;
    }

    /**
     * 先择器回掉函数
     */
    private void callbackInternal() {
        if (!isNull && mAddressSelectedListener != null) {
            Distinct address0 = privaceList == null || provinceIndex == INDEX_INVALID ? null : privaceList.get(provinceIndex);
            Distinct address1 = shiList == null || cityIndex == INDEX_INVALID ? null : shiList.get(cityIndex);
            Distinct address2 = areaList == null || countyIndex == INDEX_INVALID ? null : areaList.get(countyIndex);
            Distinct address3 = streetList == null || streetIndex == INDEX_INVALID ? null : streetList.get(streetIndex);
            Distinct address4 = xiangcunList == null || br5Index == INDEX_INVALID ? null : xiangcunList.get(br5Index);
            Distinct address5 = guanliList == null || br6Index == INDEX_INVALID ? null : guanliList.get(br6Index);
//            String address = null,no = null;
//            if (address0!=null){
//                address = address0.getDistrictFullName();
//                no = address0.getDistrictNo();
//            }
//            mAddressSelectedListener.onAddress(address,no);
            mAddressSelectedListener.onFulldAddress(address0, address1, address2, address3, address4, address5);
            dismiss();
        }
    }
}
