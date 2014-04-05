package com.armin.mmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import android.util.Pair;
import android.util.Xml;

public class MapRoutesList {
	
	private List<Pair<String,MapRoute>> mapRoutes;
	private String activeRouteName;
	private MapRoute activeRoute = null;
	
	MapRoutesList(InputStream input) {
		
		mapRoutes = new ArrayList<Pair<String,MapRoute>>();
		activeRouteName = "";
		
		try {
			parse(input);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if (mapRoutes.size() > 0) {
			activeRouteName = mapRoutes.get(0).first;
			activeRoute = mapRoutes.get(0).second;
		}
	}
	    
    public int size() {
    	return mapRoutes.size();
    }
	
    public MapRoute getActiveRoute() {
    	return activeRoute;
    }
    
    public boolean hasActiveRoute() {
    	return activeRoute == null;
    }
    
    public void setActiveRoute(int idx, GmapObjects gmo) {
    	if (idx > -1 && idx < mapRoutes.size()) {
    		deactivateRoute(gmo);
    		activeRoute = mapRoutes.get(idx).second;
    		activeRouteName = mapRoutes.get(idx).first;
    		activateRoute(gmo);
    	} else {
    		activeRoute = null;
    		activeRouteName = "";
    		deactivateRoute(gmo);    	
    	}
    }
    
    public CharSequence[] getRouteNames() {
    	CharSequence[] names = new CharSequence[mapRoutes.size()];
    	for (int i=0; i<mapRoutes.size(); i++) {
    		names[i] = mapRoutes.get(i).first;
    	}
    	return names;
    }
	
	private void activateRoute(GmapObjects gmo) {
		gmo.clearPolyline();
		gmo.mPolyline.setPoints( getActiveRoute().getPolyline().getPositions() );
		for (int rank : getActiveRoute().getRouteMarkers().keySet()) {
			RouteMarker rm = getActiveRoute().getRouteMarkers().get(rank);
			gmo.addMarker(rank, rm.getPosition(), rm.getText());
		}
	}
	
	private void deactivateRoute(GmapObjects gmo) {
		//gmo.mPolyline.remove();
		gmo.removeMarkers();
	}
    
	private static final String ns = null;
	   
    private void parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readTrainingData(parser);
        } finally {
            in.close();
        }
    }
    
    private void readTrainingData(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "traininglog");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("weekmap")) {
                readWeekMap(parser);
            } else {
                MyUtils.skip(parser);
            }
        }  
    }
    
    private void readWeekMap(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "weekmap");
        
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("weekschedule")) {
                readWeekSchedule(parser);
            } else {
            	MyUtils.skip(parser);
            }
        }
    }
    
    private void readWeekSchedule(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "weekschedule");
    	
    	while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("dayschedule")) {
                readDaySchedule(parser);
            } else {
            	MyUtils.skip(parser);
            }
        }
    }
    
    private void readDaySchedule(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "dayschedule");
    	
    	while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("date")) {
                String date = readDate(parser);
                activeRouteName = date;
            } else if (name.equals("sessions")) {
            	readSessions(parser);
            } else {
            	MyUtils.skip(parser);
            }
        }
    }
    
    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "date");
        String title = MyUtils.readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "date");
        return title;
    }
    
    private void readSessions(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "sessions");
    	
    	while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("session")) {
                readSession(parser);
            } else {            
            	MyUtils.skip(parser);
            }
        }
    }
    
    private void readSession(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, ns, "session");
    	
    	while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                String sessionName = readName(parser);
                activeRouteName += " - " + sessionName;
            } else if (name.equals("maproute")) {
            	mapRoutes.add(new Pair<String, MapRoute>(activeRouteName, new MapRoute(parser)));
            	activeRouteName = "";
            } else {                        
            	MyUtils.skip(parser);
            }
    	}
    }
    
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String title = MyUtils.readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return title;
    }
    
}
