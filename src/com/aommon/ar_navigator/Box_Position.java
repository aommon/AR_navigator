package com.aommon.ar_navigator;

public class Box_Position {

	public double longitude = 0f;
	public double latitude = 0f;
	public String description;
	public float x, y;
	
	public Box_Position(double lat, double lon, String desc) {
		this.latitude = lat;
		this.longitude = lon;
		this.description = desc;
	}
	
}
