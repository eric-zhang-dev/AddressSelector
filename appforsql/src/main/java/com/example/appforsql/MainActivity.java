package com.example.appforsql;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.appforsql.db.DBManager;
import com.example.appforsql.pojo.Distinct;

import java.util.List;

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
        String sql = "SELECT DistrictNo,DistrictName,ParentDistrictNo,DistrictFullName FROM area 'Distinct' WHERE DistrictFullName like '%北京市%' and DistrictFullName like '%东城区%'";
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

                break;
        }
    }
}
