package com.armin.mmap;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class MyPolyline {
	
	private List<LatLng> positions;
	
	MyPolyline() {
		positions = new ArrayList<LatLng>();
	}
	
	public void addPosition(LatLng p) {
		positions.add(p);
	}
	
	public List<LatLng> getPositions() {
		return positions;
	}
}
