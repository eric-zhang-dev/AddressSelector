package com.example.appforsql.listener;


import com.example.appforsql.pojo.Distinct;

/**
 * Created by zhangyue on 2016/7/14.
 */
public interface OnAddressSelectedListener {
    //    interface onAddress extends OnAddressSelectedListener {
//        void onAddress(String address, String id);
//    }
//
//    interface onFullAddress extends OnAddressSelectedListener {
//    void onAddress(String address, String id);

    void onFulldAddress(Distinct arg0, Distinct arg1, Distinct arg2, Distinct arg3, Distinct arg4, Distinct arg5);

//    void onFulldAddress(Distinct... arg0);
//    }
}
