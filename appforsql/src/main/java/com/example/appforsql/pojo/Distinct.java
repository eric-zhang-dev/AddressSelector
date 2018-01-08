package com.example.appforsql.pojo;

import java.io.Serializable;

public class Distinct implements Serializable{
	private static final long serialVersionUID = 1L;
	private String DistrictId;
	private String DistrictNo;
	private String DistrictName;
	private String ParentDistrictNo;
	private String DistrictFullName;
	private String DistrictLevel;
	
	
	
	public String getDistrictId() {
		return DistrictId;
	}



	public void setDistrictId(String districtId) {
		DistrictId = districtId;
	}



	public String getDistrictNo() {
		return DistrictNo;
	}



	public void setDistrictNo(String districtNo) {
		DistrictNo = districtNo;
	}



	public String getDistrictName() {
		return DistrictName;
	}



	public void setDistrictName(String districtName) {
		DistrictName = districtName;
	}



	public String getParentDistrictNo() {
		return ParentDistrictNo;
	}



	public void setParentDistrictNo(String parentDistrictNo) {
		ParentDistrictNo = parentDistrictNo;
	}



	public String getDistrictFullName() {
		return DistrictFullName;
	}



	public void setDistrictFullName(String districtFullName) {
		DistrictFullName = districtFullName;
	}



	public String getDistrictLevel() {
		return DistrictLevel;
	}



	public void setDistrictLevel(String districtLevel) {
		DistrictLevel = districtLevel;
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return DistrictName;
	}

}
