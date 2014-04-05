package com.armin.mmap;

import com.google.android.gms.maps.model.LatLng;

public class RouteMarker {
	private int rank;
	private LatLng position;
	private String text;
	
	public RouteMarker(int rank, LatLng position, String text) {
		this.rank     = rank;
		this.position = position;
		this.text     = text;
	}
	
	public int getRank() {
		return rank;
	}
	
	public LatLng getPosition() {
		return position;
	}
	
	public String getText() {
		return text;
	}
}
