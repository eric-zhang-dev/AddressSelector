package chihane.jdaddressselector;

import java.util.List;

import chihane.jdaddressselector.model.Brdb;

public interface AddressProvider {
    void provideProvinces(AddressReceiver<Brdb> addressReceiver);
    void provideCitiesWith(String provinceId, AddressReceiver<Brdb> addressReceiver);
    void provideCountiesWith(String cityId, AddressReceiver<Brdb> addressReceiver);
    void provideStreetsWith(String countyId, AddressReceiver<Brdb> addressReceiver);
    void provideBr5With(String br5, AddressReceiver<Brdb> addressReceiver);
    void provideBr6With(String br6, AddressReceiver<Brdb> addressReceiver);
    interface AddressReceiver<T> {
        void send(List<T> data);
    }
}