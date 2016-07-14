package chihane.jdaddressselector;

import com.raizlabs.android.dbflow.list.FlowQueryList;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

import chihane.jdaddressselector.model.Brdb;
import chihane.jdaddressselector.model.Brdb_Table;

public class DefaultAddressProvider implements AddressProvider {
    @Override
    public void provideProvinces(final AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> provinceQueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.DistrictLevel.eq("0"))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(provinceQueryList));
    }
    @Override
    public void provideCitiesWith(String provinceId, final AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> cityQueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.ParentDistrictNo.eq(provinceId))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(cityQueryList));
    }

    @Override
    public void provideCountiesWith(String cityId, final AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> countyQueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.ParentDistrictNo.eq(cityId))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(countyQueryList));
    }

    @Override
    public void provideStreetsWith(String countyId, final AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> streetQueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.ParentDistrictNo.eq(countyId))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(streetQueryList));
    }

    @Override
    public void provideBr5With(String br5, AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> br5QueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.ParentDistrictNo.eq(br5))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(br5QueryList));
    }

    @Override
    public void provideBr6With(String br6, AddressReceiver<Brdb> addressReceiver) {
        final FlowQueryList<Brdb> br6QueryList = SQLite.select()
                .from(Brdb.class)
                .where(Brdb_Table.ParentDistrictNo.eq(br6))
                .flowQueryList();
        addressReceiver.send(new ArrayList<>(br6QueryList));
    }
}
