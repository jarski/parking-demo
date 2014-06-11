package com.vaadin.demo.parking.model;

import org.json.JSONObject;


public class ShiftSuggestion {
	
	private static long idRunner = 0;
	
	private long id;
	private String area = "A2";
	private String date = "";
	private Integer start = null;
	private Integer end = null;
	
	public ShiftSuggestion() {
		id = idRunner++;
	}
	
	public ShiftSuggestion(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ShiftSuggestion) {
			ShiftSuggestion value = (ShiftSuggestion) obj;
			return this.id == value.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) this.id;
	}
	
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}

	
}
