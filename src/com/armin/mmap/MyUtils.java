package com.armin.mmap;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;


public class MyUtils {
	
	public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}


	public static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
	
	public static LatLng stringToPosition(String ll) {
		String[] sep = ll.split(",");
		if (sep.length == 2) {
			return new LatLng( Double.parseDouble(sep[0]), Double.parseDouble(sep[1]) );
		}
		return new LatLng(0.0,0.0);
	}
	
	@SuppressLint("DefaultLocale")
	public static Pair<String,String> splitCommand(String str) {
		String[] split = str.split("\\s+");
		if (split.length == 1) {
			return new Pair<String,String> (split[0].toLowerCase(), "");
		} else {
			String arg = "";			
			for (int i=1; i<split.length; i++) {
				arg += split[i] + " ";
			}
			return new Pair<String,String> (split[0].toLowerCase(), arg); 
		}
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
