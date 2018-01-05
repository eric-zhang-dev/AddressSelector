package chihane.jdaddressselector.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.BottomDialog;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.Brdb;
import mlxy.utils.T;

public class MainActivity extends AppCompatActivity implements OnAddressSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        AddressSelector selector = new AddressSelector(this,null,null,null,null,null,null);
        selector.setOnAddressSelectedListener(this);
//        selector.setAddressProvider(new DefaultAddressProvider());

        assert frameLayout != null;
        frameLayout.addView(selector.getView());

        Button buttonBottomDialog = (Button) findViewById(R.id.buttonBottomDialog);
        assert buttonBottomDialog != null;
        buttonBottomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BottomDialog.show(MainActivity.this, MainActivity.this);
                BottomDialog dialog = new BottomDialog(MainActivity.this,"江苏省","苏州市","吴中区","越溪街道","越溪管理区","木林社区");
                dialog.setOnAddressSelectedListener(MainActivity.this);
                dialog.show();
            }
        });
    }

    @Override
    public void onAddressSelected(Brdb br1, Brdb br2, Brdb br3, Brdb br4, Brdb br5, Brdb br6) {
        Log.i("onAddressSelected", "onAddressSelected: "+(br1 == null ? "" : br1.DistrictName) +
                (br2 == null ? "" : "" + br2.DistrictName) +
                (br3 == null ? "" : "" + br3.DistrictName) +
                (br4 == null ? "" : "" + br4.DistrictName) +
                (br5 == null ? "" : "" + br5.DistrictName) +
                (br6 == null ? "" : "" + br6.DistrictName));
        String s =
                (br1 == null ? "" : br1.DistrictName) +
                        (br2 == null ? "" : "" + br2.DistrictName) +
                        (br3 == null ? "" : "" + br3.DistrictName) +
                        (br4 == null ? "" : "" + br4.DistrictName) +
                        (br5 == null ? "" : "" + br5.DistrictName) +
                        (br6 == null ? "" : "" + br6.DistrictName);
        T.showShort(this,s);
    }
}
