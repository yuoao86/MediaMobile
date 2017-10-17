package com.drawshirt.mediamobile.bean;

import java.util.ArrayList;

public class Conds {

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public ArrayList<ValuesSearch> getValuesArr() {
		return valuesArr;
	}
	public void setValuesArr(ArrayList<ValuesSearch> valuesArr) {
		this.valuesArr = valuesArr;
	}
	private String name;
	private String field;
	private ArrayList<ValuesSearch> valuesArr;
	 
}
