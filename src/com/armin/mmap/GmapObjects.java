package com.armin.mmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Marker;

public class GmapObjects  implements
	ConnectionCallbacks,
	OnConnectionFailedListener,
	OnMyLocationButtonClickListener {
	
	public MainActivity parent;
	public GoogleMap mMap;
	public Polyline mPolyline;
	public Map<Integer, Marker> mMarkers;
	
	private LocationClient mLocationClient;
	
	
	public GmapObjects(MainActivity parent) {
		this.parent = parent;
		
	}
	
	public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) parent.getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();               
            }            
        }
    }
	
	public void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    parent,
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
	
	public void disconnectLocation() { 
	   if (mLocationClient != null) {
           mLocationClient.disconnect();
           mLocationClient = null;
       }
	}
	
	public void connectLocation() {
		setUpLocationClientIfNeeded();
        mLocationClient.connect();
	}
	
	@SuppressLint("UseSparseArrays")
	private void setUpMap() {
    	mPolyline = mMap.addPolyline(new PolylineOptions()
        	.add()
        	.width(5)
        	.color(Color.RED));
    	mMarkers = new HashMap<Integer, Marker>();
    	
    	mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
    }
	
	public void clearPolyline() {
		mPolyline.remove();
		mPolyline = mMap.addPolyline(new PolylineOptions()
    	.add()
    	.width(5)
    	.color(Color.RED));
	}
	
	public void boundPolyline() {
		if (mPolyline.isVisible() && mPolyline.getPoints().size() > 1) {
			List<LatLng> p = mPolyline.getPoints();
			LatLngBounds bounds = new LatLngBounds(p.get(0), p.get(1));
			for (int i=2; i<p.size(); i++) {
				bounds = bounds.including(p.get(i));
			}
			mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
		}
	}
	
	public void addMarker(int rank, LatLng position, String title) {
		mMarkers.put(rank, mMap.addMarker(new MarkerOptions()
			.position(position)
			.title(title)));
	}
	
	public void removeMarkers() {
		for (int rank : mMarkers.keySet()) {
			mMarkers.get(rank).remove();
		}
		mMarkers.clear();
	}
	
	public void gotoMarker(String arg) {
		if (MyUtils.isInteger(arg.trim())) {
			int rank = Integer.parseInt(arg.trim());
			if (mMarkers.containsKey(rank)) {
				gotoPosition(mMarkers.get(rank).getPosition());
				mMarkers.get(rank).showInfoWindow();
			}
		}
	}
	
	public void gotoPosition(LatLng pos) {
		mMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
	}
	
	public void showMapAsSatellite() {
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}
	
	public void showMapAsNormal() {
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
	
	  /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        parent.showShortToast("gps connection estabished!");
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onDisconnected() {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
    
    @Override
    public boolean onMyLocationButtonClick() {
        parent.showShortToast("Goto current location.");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

}
