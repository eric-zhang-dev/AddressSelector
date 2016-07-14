package chihane.jdaddressselector.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import chihane.jdaddressselector.global.Database;

/**
 * Created by zhangyue on 2016/7/13.
 */
@Table(database = Database.class)
public class Brdb extends BaseModel {
    @PrimaryKey
    public String id;
    @Column
    public String DistrictNo;
    @Column
    public String DistrictName;
    @Column
    public String ParentDistrictNo;
    @Column
    public String DistrictFullName;
    @Column
    public String DistrictLevel;
}
