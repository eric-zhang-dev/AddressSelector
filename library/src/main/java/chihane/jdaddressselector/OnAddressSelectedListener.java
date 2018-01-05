package chihane.jdaddressselector;

import chihane.jdaddressselector.model.Brdb;

public interface OnAddressSelectedListener {
    void onAddressSelected(Brdb province, Brdb city, Brdb county, Brdb street, Brdb br5, Brdb br6);
}
