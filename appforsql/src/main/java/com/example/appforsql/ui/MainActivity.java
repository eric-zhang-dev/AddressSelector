package com.example.appforsql.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.appforsql.common.Constant;
import com.example.appforsql.dialog.AddressDialog;
import com.example.appforsql.listener.OnAddressSelectedListener;
import com.example.appforsql.R;
import com.example.appforsql.db.DBManager;
import com.example.appforsql.pojo.Distinct;
import com.example.appforsql.utils.SharedUtils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private DBManager<Distinct> dbManager;
    private List<Distinct> mDistinctList;
    private TextView mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddress = findViewById(R.id.address);
        mAddress.setOnClickListener(this);
        dbManager = new DBManager<Distinct>(Distinct.class);
        dbManager.openDatabase();
        String sql = "SELECT DistrictNo,DistrictName,ParentDistrictNo,DistrictFullName FROM area 'Distinct' WHERE DistrictFullName like '%"+ SharedUtils.getString(this,"address0","北京市")+"%' and DistrictFullName like '%"+ SharedUtils.getString(this,"address2","东城区")+"%'";
        mDistinctList = dbManager.getBySql(sql, null, null);
        dbManager.closeDatabase();
        if (null != mDistinctList && mDistinctList.size() != 0) {
            mAddress.setText(mDistinctList.get(0).getDistrictFullName());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.address:
                AddressDialog dialog = new AddressDialog(this,addressSelectedListener);
                dialog.show();
                break;
        }
    }
    private OnAddressSelectedListener addressSelectedListener = new OnAddressSelectedListener() {
        @Override
        public void onFulldAddress(Distinct arg0, Distinct arg1, Distinct arg2, Distinct arg3, Distinct arg4, Distinct arg5) {
            if (arg0!=null){
                Constant.address0 = arg0.getDistrictName();
                Constant.districtNo = arg0.getDistrictNo();
                Constant.districtFullName = arg0.getDistrictFullName();
            }else {
                Constant.address0 = "";
            }
            if (arg1!=null){
                Constant.address1 = arg1.getDistrictName();
                Constant.districtNo = arg1.getDistrictNo();
                Constant.districtFullName = arg1.getDistrictFullName();
            }else {
                Constant.address1 = "";
            }
            if (arg2!=null){
                Constant.address2 = arg2.getDistrictName();
                Constant.districtNo = arg2.getDistrictNo();
                Constant.districtFullName = arg2.getDistrictFullName();
            }else {
                Constant.address2 = "";
            }
            if (arg3!=null){
                Constant.address3 = arg3.getDistrictName();
                Constant.districtNo = arg3.getDistrictNo();
                Constant.districtFullName = arg3.getDistrictFullName();
            }else {
                Constant.address3 = "";
            }
            if (arg4!=null){
                Constant.address4 = arg4.getDistrictName();
                Constant.districtNo = arg4.getDistrictNo();
                Constant.districtFullName = arg4.getDistrictFullName();
            }else {
                Constant.address4 = "";
            }
            if (arg5!=null){
                Constant.address5 = arg5.getDistrictName();
                Constant.districtNo = arg5.getDistrictNo();
                Constant.districtFullName = arg5.getDistrictFullName();
            }else {
                Constant.address5 = "";
            }
            mAddress.setText(Constant.address0+" "+Constant.address1+" "+Constant.address2+" "+Constant.address3+" "+Constant.address4+" "+Constant.address5);
            SharedUtils.putString(MainActivity.this,"districtNo",Constant.districtNo);
            SharedUtils.putString(MainActivity.this,"districtFullName",Constant.districtFullName);
            SharedUtils.putString(MainActivity.this,"address0",Constant.address0);
            SharedUtils.putString(MainActivity.this,"address1",Constant.address1);
            SharedUtils.putString(MainActivity.this,"address2",Constant.address2);
        }
    };
}
