package chihane.jdaddressselector;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import chihane.jdaddressselector.model.Brdb;
import mlxy.utils.Lists;

public class AddressSelector implements AdapterView.OnItemClickListener {
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
    private static final AddressProvider DEFAULT_ADDRESS_PROVIDER = new DefaultAddressProvider();
    private String q1, q2, q3, q4, q5, q6;
    private boolean isNull = false;
    private final Context mContext;
    private final LayoutInflater inflater;
    private OnAddressSelectedListener listener;
    private AddressProvider addressProvider = DEFAULT_ADDRESS_PROVIDER;

    private View view;

    private View indicator;

    private TextView textViewProvince;
    private TextView textViewCity;
    private TextView textViewCounty;
    private TextView textViewStreet;
    private TextView textViewBr5;
    private TextView textViewBr6;
    private ProgressBar progressBar;

    private ListView listView;
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private CountyAdapter countyAdapter;
    private StreetAdapter streetAdapter;
    private Br5Adapter br5Adapter;
    private Br6Adapter br6Adapter;
    private List<Brdb> provinces;
    private List<Brdb> cities;
    private List<Brdb> counties;
    private List<Brdb> streets;
    private List<Brdb> br5;
    private List<Brdb> br6;
    private int provinceIndex = INDEX_INVALID;
    private int cityIndex = INDEX_INVALID;
    private int countyIndex = INDEX_INVALID;
    private int streetIndex = INDEX_INVALID;
    private int br5Index = INDEX_INVALID;
    private int br6Index = INDEX_INVALID;
    private int tabIndex = INDEX_TAB_PROVINCE;
    private HorizontalScrollView scrollView;
    @SuppressWarnings("unchecked")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_PROVINCES_SELECTED:
                    provinces = (List<Brdb>) msg.obj;
                    provinceAdapter.notifyDataSetChanged();
                    listView.setAdapter(provinceAdapter);
                    ////////////
                    if (isNull) {
                        proviceItem(getLocalItem(provinces, q1));
                    }
                    break;

                case WHAT_CITIES_SELECTED:
                    cities = (List<Brdb>) msg.obj;
                    cityAdapter.notifyDataSetChanged();
                    if (Lists.notEmpty(cities)) {
                        // 以次级内容更新列表
                        listView.setAdapter(cityAdapter);
                        // 更新索引为次级
                        tabIndex = INDEX_TAB_CITY;
                    } else {
                        // 次级无内容，回调
                        callbackInternal();
                    }
                    //////////
                    if (isNull) {
                        cityItem(getLocalItem(cities, q2));
                    }
                    break;

                case WHAT_COUNTIES_SELECTED:
                    counties = (List<Brdb>) msg.obj;
                    countyAdapter.notifyDataSetChanged();
                    if (Lists.notEmpty(counties)) {
                        listView.setAdapter(countyAdapter);
                        tabIndex = INDEX_TAB_COUNTY;
                    } else {
                        callbackInternal();
                    }
                    ///////////////////
                    if (isNull) {
                        countItem(getLocalItem(counties, q3));
                    }
                    break;

                case WHAT_STREETS_SELECTED:
                    streets = (List<Brdb>) msg.obj;
                    streetAdapter.notifyDataSetChanged();
                    if (Lists.notEmpty(streets)) {
                        listView.setAdapter(streetAdapter);
                        tabIndex = INDEX_TAB_STREET;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (Lists.notEmpty(streets) && !TextUtils.isEmpty(q4)) {
                            streetItem(getLocalItem(streets, q4));
                        }
                    }
                    break;
                case WHAT_BR5_SELECTED:
                    br5 = (List<Brdb>) msg.obj;
                    br5Adapter.notifyDataSetChanged();
                    if (Lists.notEmpty(br5)) {
                        listView.setAdapter(br5Adapter);
                        tabIndex = INDEX_TAB_BR5;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (Lists.notEmpty(br5) && !TextUtils.isEmpty(q5)) {
                            br5Item(getLocalItem(br5, q5));
                        }
                    }
                    break;
                case WHAT_BR6_SELECTED:
                    br6 = (List<Brdb>) msg.obj;
                    br6Adapter.notifyDataSetChanged();
                    if (Lists.notEmpty(br6)) {
                        listView.setAdapter(br6Adapter);
                        tabIndex = INDEX_TAB_BR6;
                    } else {
                        callbackInternal();
                    }
                    if (isNull) {
                        if (Lists.notEmpty(br6) && !TextUtils.isEmpty(q6)) {
                            br6Item(getLocalItem(br6, q6));
                        }
                    }
                    break;
            }
            updateTabsVisibility();
            updateProgressVisibility();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    private void br6Item(int position) {
        Brdb br6 = br6Adapter.getItem(position);
        textViewBr6.setText(br6.DistrictName);
        br6Adapter.notifyDataSetChanged();
        this.br6Index = position;
        callbackInternal();
    }

    private void br5Item(int position) {
        Brdb br5 = br5Adapter.getItem(position);
        textViewBr5.setText(br5.DistrictName);
        textViewBr6.setText("请选择");
        br5Adapter.notifyDataSetChanged();
        provideBr6With(br5.DistrictNo);
        br6 = null;
        br6Adapter.notifyDataSetChanged();
        this.br5Index = position;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第三极点击事件
     *
     * @param position
     */
    private void streetItem(int position) {
        Brdb street = streetAdapter.getItem(position);
        textViewBr5.setText("请选择");
        textViewBr6.setText("请选择");
        textViewStreet.setText(street.DistrictName);
        streetAdapter.notifyDataSetChanged();
        provideBr5With(street.DistrictNo);
        br5 = null;
        br6 = null;
        br5Adapter.notifyDataSetChanged();
        br6Adapter.notifyDataSetChanged();
        this.streetIndex = position;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第三极点击事件
     *
     * @param position
     */
    private void countItem(int position) {
        Brdb county = countyAdapter.getItem(position);
        textViewCounty.setText(county.DistrictName);
        textViewStreet.setText("请选择");
        textViewBr5.setText("请选择");
        textViewBr6.setText("请选择");
        countyAdapter.notifyDataSetChanged();
        retrieveStreetsWith(county.DistrictNo);
        streets = null;
        br5 = null;
        br6 = null;
        streetAdapter.notifyDataSetChanged();
        br5Adapter.notifyDataSetChanged();
        br6Adapter.notifyDataSetChanged();
        this.countyIndex = position;
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
        Brdb city = cityAdapter.getItem(position);
        textViewCity.setText(city.DistrictName);
        textViewCounty.setText("请选择");
        textViewStreet.setText("请选择");
        textViewBr5.setText("请选择");
        textViewBr6.setText("请选择");
        cityAdapter.notifyDataSetChanged();
        retrieveCountiesWith(city.DistrictNo);
        counties = null;
        streets = null;
        br5 = null;
        br6 = null;
        countyAdapter.notifyDataSetChanged();
        streetAdapter.notifyDataSetChanged();
        br5Adapter.notifyDataSetChanged();
        br6Adapter.notifyDataSetChanged();
        this.cityIndex = position;
        this.countyIndex = INDEX_INVALID;
        this.streetIndex = INDEX_INVALID;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    /**
     * 第一级点击事件
     *
     * @param position
     */
    private void proviceItem(int position) {
        Brdb province = provinceAdapter.getItem(position);
        // 更新当前级别及子级标签文本
        textViewProvince.setText(province.DistrictName);
        textViewCity.setText("请选择");
        textViewCounty.setText("请选择");
        textViewStreet.setText("请选择");
        textViewBr5.setText("请选择");
        textViewBr6.setText("请选择");
        // 更新选中效果
        provinceAdapter.notifyDataSetChanged();
        retrieveCitiesWith(province.DistrictNo);
        // 更新子级数据
        cities = null;
        counties = null;
        streets = null;
        br5 = null;
        br6 = null;
        cityAdapter.notifyDataSetChanged();
        countyAdapter.notifyDataSetChanged();
        streetAdapter.notifyDataSetChanged();
        br5Adapter.notifyDataSetChanged();
        br6Adapter.notifyDataSetChanged();
        // 更新选中数据
        this.provinceIndex = position;
        this.cityIndex = INDEX_INVALID;
        this.countyIndex = INDEX_INVALID;
        this.streetIndex = INDEX_INVALID;
        this.br5Index = INDEX_INVALID;
        this.br6Index = INDEX_INVALID;
    }

    public AddressSelector(Context context, String p1, String p2, String p3, String p4, String p5, String p6) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        if (!TextUtils.isEmpty(p1)) {
            this.q1 = p1;
            this.q2 = p2;
            this.q3 = p3;
            this.q4 = p4;
            this.q5 = p5;
            this.q6 = p6;
            isNull = true;
        }
        FlowManager.init(new FlowConfig.Builder(mContext).build());
        initViews();
        initAdapters();
        initProvince();
    }

    private void initAdapters() {
        provinceAdapter = new ProvinceAdapter();
        cityAdapter = new CityAdapter();
        countyAdapter = new CountyAdapter();
        streetAdapter = new StreetAdapter();
        br5Adapter = new Br5Adapter();
        br6Adapter = new Br6Adapter();
    }

    private void initProvince() {
        retrieveProvinces();
    }

    private void updateTabsVisibility() {
        textViewProvince.setVisibility(Lists.notEmpty(provinces) ? View.VISIBLE : View.GONE);
        textViewCity.setVisibility(Lists.notEmpty(cities) ? View.VISIBLE : View.GONE);
        textViewCounty.setVisibility(Lists.notEmpty(counties) ? View.VISIBLE : View.GONE);
        textViewStreet.setVisibility(Lists.notEmpty(streets) ? View.VISIBLE : View.GONE);
        textViewBr5.setVisibility(Lists.notEmpty(br5) ? View.VISIBLE : View.GONE);
        textViewBr6.setVisibility(Lists.notEmpty(br6) ? View.VISIBLE : View.GONE);
        textViewProvince.setEnabled(tabIndex != INDEX_TAB_PROVINCE);
        textViewCity.setEnabled(tabIndex != INDEX_TAB_CITY);
        textViewCounty.setEnabled(tabIndex != INDEX_TAB_COUNTY);
        textViewStreet.setEnabled(tabIndex != INDEX_TAB_STREET);
        textViewBr5.setEnabled(tabIndex != INDEX_TAB_BR5);
        textViewBr6.setEnabled(tabIndex != INDEX_TAB_BR6);
    }

    private void initViews() {
        view = inflater.inflate(R.layout.address_selector, null);

        this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        this.listView = (ListView) view.findViewById(R.id.listView);
        this.indicator = view.findViewById(R.id.indicator);

        this.textViewProvince = (TextView) view.findViewById(R.id.textViewProvince);
        this.textViewCity = (TextView) view.findViewById(R.id.textViewCity);
        this.textViewCounty = (TextView) view.findViewById(R.id.textViewCounty);
        this.textViewStreet = (TextView) view.findViewById(R.id.textViewStreet);
        this.textViewBr5 = (TextView) view.findViewById(R.id.textViewBr5);
        this.textViewBr6 = (TextView) view.findViewById(R.id.textViewBr6);
        this.scrollView = (HorizontalScrollView) view.findViewById(R.id.scroll_view);
        this.textViewProvince.setOnClickListener(new OnProvinceTabClickListener());
        this.textViewCity.setOnClickListener(new OnCityTabClickListener());
        this.textViewCounty.setOnClickListener(new onCountyTabClickListener());
        this.textViewStreet.setOnClickListener(new OnStreetTabClickListener());
        this.textViewBr5.setOnClickListener(new OnBr5TabClickListener());
        this.textViewBr6.setOnClickListener(new OnBr6TabClickListener());
        this.listView.setOnItemClickListener(this);

        updateIndicator();
    }

    private void updateIndicator() {
        view.post(new Runnable() {
            @Override
            public void run() {
                switch (tabIndex) {
                    case INDEX_TAB_PROVINCE:
                        buildIndicatorAnimatorTowards(textViewProvince).start();
                        break;
                    case INDEX_TAB_CITY:
                        buildIndicatorAnimatorTowards(textViewCity).start();
                        break;
                    case INDEX_TAB_COUNTY:
                        buildIndicatorAnimatorTowards(textViewCounty).start();
                        break;
                    case INDEX_TAB_STREET:
                        buildIndicatorAnimatorTowards(textViewStreet).start();
                        break;
                    case INDEX_TAB_BR5:
                        buildIndicatorAnimatorTowards(textViewBr5).start();
                        break;
                    case INDEX_TAB_BR6:
                        buildIndicatorAnimatorTowards(textViewBr6).start();
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
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        return set;
    }

    class OnProvinceTabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_PROVINCE;
            listView.setAdapter(provinceAdapter);

            if (provinceIndex != INDEX_INVALID) {
                listView.setSelection(provinceIndex);
            }

            updateTabsVisibility();
            updateIndicator();
        }
    }

    class OnCityTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_CITY;
            listView.setAdapter(cityAdapter);

            if (cityIndex != INDEX_INVALID) {
                listView.setSelection(cityIndex);
            }
            updateTabsVisibility();
            updateIndicator();
        }
    }

    class onCountyTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_COUNTY;
            listView.setAdapter(countyAdapter);

            if (countyIndex != INDEX_INVALID) {
                listView.setSelection(countyIndex);
            }

            updateTabsVisibility();
            updateIndicator();
        }
    }

    class OnStreetTabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_STREET;
            listView.setAdapter(streetAdapter);

            if (streetIndex != INDEX_INVALID) {
                listView.setSelection(streetIndex);
            }
            updateTabsVisibility();
            updateIndicator();
        }
    }

    class OnBr5TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_BR5;
            listView.setAdapter(br5Adapter);

            if (br5Index != INDEX_INVALID) {
                listView.setSelection(br5Index);
            }
            updateTabsVisibility();
            updateIndicator();
        }
    }

    class OnBr6TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            tabIndex = INDEX_TAB_BR6;
            listView.setAdapter(br6Adapter);

            if (br6Index != INDEX_INVALID) {
                listView.setSelection(br6Index);
            }
            updateTabsVisibility();
            updateIndicator();
        }
    }


    public View getView() {
        return view;
    }

    private void callbackInternal() {
//        String address = null;
        String id = null;
        if (listener != null) {
            Brdb province = provinces == null || provinceIndex == INDEX_INVALID ? null : provinces.get(provinceIndex);
            Brdb city = cities == null || cityIndex == INDEX_INVALID ? null : cities.get(cityIndex);
            Brdb county = counties == null || countyIndex == INDEX_INVALID ? null : counties.get(countyIndex);
            Brdb street = streets == null || streetIndex == INDEX_INVALID ? null : streets.get(streetIndex);
            Brdb Brdb5 = br5 == null || br5Index == INDEX_INVALID ? null : br5.get(br5Index);
            Brdb Brdb6 = br6 == null || br6Index == INDEX_INVALID ? null : br6.get(br6Index);
            StringBuffer stringBuffer = new StringBuffer();
            if (province != null) {
                stringBuffer.append(province.DistrictName);
                id = province.DistrictNo;
            }
            if (city != null) {
                stringBuffer.append(city.DistrictName);
                id = city.DistrictNo;
            }
            if (county != null) {
                stringBuffer.append(county.DistrictName);
                id = county.DistrictNo;
            }
            if (street != null) {
                stringBuffer.append(street.DistrictName);
                id = street.DistrictNo;
            }
            if (Brdb5 != null) {
                stringBuffer.append(Brdb5.DistrictName);
                id = Brdb5.DistrictNo;
            }
            if (Brdb6 != null) {
                stringBuffer.append(Brdb6.DistrictName);
                id = Brdb6.DistrictNo;
            }
            listener.onAddressSelected(stringBuffer.toString(), id);
        }
    }

    private void updateProgressVisibility() {
        ListAdapter adapter = listView.getAdapter();
        int itemCount = adapter.getCount();
        progressBar.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
    }

    private void retrieveProvinces() {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideProvinces(new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_PROVINCES_SELECTED, data));
            }
        });
    }

    private void retrieveCitiesWith(String provinceId) {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideCitiesWith(provinceId, new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_CITIES_SELECTED, data));
            }
        });
    }

    private void retrieveCountiesWith(String cityId) {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideCountiesWith(cityId, new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_COUNTIES_SELECTED, data));
            }
        });
    }

    private void retrieveStreetsWith(String countyId) {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideStreetsWith(countyId, new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_STREETS_SELECTED, data));
            }
        });
    }

    private void provideBr5With(String br5) {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideBr5With(br5, new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_BR5_SELECTED, data));
            }
        });
    }

    private void provideBr6With(String br6) {
        progressBar.setVisibility(View.VISIBLE);
        addressProvider.provideBr6With(br6, new AddressProvider.AddressReceiver<Brdb>() {
            @Override
            public void send(List<Brdb> data) {
                handler.sendMessage(Message.obtain(handler, WHAT_BR6_SELECTED, data));
            }
        });
    }

    class ProvinceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return provinces == null ? 0 : provinces.size();
        }

        @Override
        public Brdb getItem(int position) {
            return provinces.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);

            boolean checked = provinceIndex != INDEX_INVALID && provinces.get(provinceIndex).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    class CityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cities == null ? 0 : cities.size();
        }

        @Override
        public Brdb getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);

            boolean checked = cityIndex != INDEX_INVALID && cities.get(cityIndex).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    class CountyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return counties == null ? 0 : counties.size();
        }

        @Override
        public Brdb getItem(int position) {
            return counties.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);

            boolean checked = countyIndex != INDEX_INVALID && counties.get(countyIndex).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    class StreetAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return streets == null ? 0 : streets.size();
        }

        @Override
        public Brdb getItem(int position) {
            return streets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);
            boolean checked = streetIndex != INDEX_INVALID && streets.get(streetIndex).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    class Br5Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return br5 == null ? 0 : br5.size();
        }

        @Override
        public Brdb getItem(int position) {
            return br5.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);

            boolean checked = br5Index != INDEX_INVALID && br5.get(br5Index).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    class Br6Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return br6 == null ? 0 : br6.size();
        }

        @Override
        public Brdb getItem(int position) {
            return br6.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.parseLong(getItem(position).DistrictNo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_area, parent, false);

                holder = new Holder();
                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.imageViewCheckMark = (ImageView) convertView.findViewById(R.id.imageViewCheckMark);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            Brdb item = getItem(position);
            holder.textView.setText(item.DistrictName);

            boolean checked = br6Index != INDEX_INVALID && br6.get(br6Index).DistrictNo == item.DistrictNo;
            holder.textView.setEnabled(!checked);
            holder.imageViewCheckMark.setVisibility(checked ? View.VISIBLE : View.GONE);

            return convertView;
        }

        class Holder {
            TextView textView;
            ImageView imageViewCheckMark;
        }
    }

    public OnAddressSelectedListener getOnAddressSelectedListener() {
        return listener;
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    public void setAddressProvider(AddressProvider addressProvider) {
        this.addressProvider = addressProvider;
        if (addressProvider == null) {
            this.addressProvider = DEFAULT_ADDRESS_PROVIDER;
        }
    }

    /**
     * 获取默认项
     *
     * @param province
     * @return
     */
    public int getLocalItem(List<Brdb> list, String province) {
        int size = list.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (province.equals(list.get(i).DistrictName)) {
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
}
