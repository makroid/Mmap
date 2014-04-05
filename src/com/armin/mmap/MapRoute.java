package com.armin.mmap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import com.google.android.gms.maps.model.LatLng;


public class MapRoute {
	
	private static final String ns = null;
	private MyPolyline polyline;
	private Map<Integer,RouteMarker> routeMarkers;
	
	@SuppressLint("UseSparseArrays")
	MapRoute() {
		polyline = new MyPolyline();
		routeMarkers = new HashMap<Integer,RouteMarker>();
	}
	
	MapRoute(XmlPullParser parser) {
		this();
		
		try {
			readMapRoute(parser);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 private void readMapRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
		 parser.require(XmlPullParser.START_TAG, ns, "maproute");

		 while (parser.next() != XmlPullParser.END_TAG) {
			 if (parser.getEventType() != XmlPullParser.START_TAG) {
				 continue;
			 }
			 String name = parser.getName();
			 if (name.equals("polyline")) {
				 readPolyline(parser);
			 } else if (name.equals("routemarkers")) {
				 readRouteMarkers(parser);
			 } else {
				 MyUtils.skip(parser);
			 }
		 }
	 }
	 
	 private void readPolyline(XmlPullParser parser) throws XmlPullParserException, IOException {
		 parser.require(XmlPullParser.START_TAG, ns, "polyline");

		 while (parser.next() != XmlPullParser.END_TAG) {
			 if (parser.getEventType() != XmlPullParser.START_TAG) {
				 continue;
			 }
			 String name = parser.getName();
			 if (name.equals("ll")) {
				 LatLng ll = readLatLng(parser);
				 polyline.addPosition(ll);
			 } else {
				 MyUtils.skip(parser);
			 }
		 }
	 }
	 
	 private LatLng readLatLng(XmlPullParser parser) throws IOException, XmlPullParserException {
		 parser.require(XmlPullParser.START_TAG, ns, "ll");
		 String ll = MyUtils.readText(parser);
		 parser.require(XmlPullParser.END_TAG, ns, "ll");
		 return MyUtils.stringToPosition(ll);
	 }
	 
	 private void readRouteMarkers(XmlPullParser parser) throws XmlPullParserException, IOException {
		 parser.require(XmlPullParser.START_TAG, ns, "routemarkers");

		 while (parser.next() != XmlPullParser.END_TAG) {
			 if (parser.getEventType() != XmlPullParser.START_TAG) {
				 continue;
			 }
			 String name = parser.getName();
			 if (name.equals("marker")) {
				 readRouteMarker(parser);
			 } else {
				 MyUtils.skip(parser);
			 }
		 }
	 }
	 
	 private void readRouteMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
		 parser.require(XmlPullParser.START_TAG, ns, "marker");

		 LatLng latlng = null;
		 int rank = -1;
		 String markerText = "";
		 
		 while (parser.next() != XmlPullParser.END_TAG) {
			 if (parser.getEventType() != XmlPullParser.START_TAG) {
				 continue;
			 }
			 String name = parser.getName();
			 if (name.equals("rank")) {
				 rank = readRank(parser);
			 } else if (name.equals("latlng")) {
				 latlng = readLatLngMarker(parser);
			 } else if (name.equals("text")) {
				 markerText = readTextMarker(parser);
			 } else {			 
				 MyUtils.skip(parser);
			 }
		 }
		 
		 if (latlng != null) {
			 routeMarkers.put(rank, new RouteMarker(rank, latlng, markerText));
		 }
	 }

	 private int readRank(XmlPullParser parser) throws IOException, XmlPullParserException {
		 parser.require(XmlPullParser.START_TAG, ns, "rank");
		 String rank = MyUtils.readText(parser);
		 parser.require(XmlPullParser.END_TAG, ns, "rank");
		 return Integer.parseInt(rank);		 
	 }
	 
	 private LatLng readLatLngMarker(XmlPullParser parser) throws IOException, XmlPullParserException {
		 parser.require(XmlPullParser.START_TAG, ns, "latlng");
		 String ll = MyUtils.readText(parser);
		 parser.require(XmlPullParser.END_TAG, ns, "latlng");
		 return MyUtils.stringToPosition(ll);
	 }
	 
	 private String readTextMarker(XmlPullParser parser) throws IOException, XmlPullParserException {
		 parser.require(XmlPullParser.START_TAG, ns, "text");
		 String ret = MyUtils.readText(parser);
		 parser.require(XmlPullParser.END_TAG, ns, "text");
		 return ret;
	 }
	 
	 public MyPolyline getPolyline() {
		 return polyline;
	 }
	 
	 public Map<Integer,RouteMarker> getRouteMarkers() {
		 return routeMarkers;
	 }
}

