package com.armin.mmap;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

import com.armin.filechooser.*;

public class MainActivity extends FragmentActivity {
	
	final int REQUEST_FILE_CHOOSER = 1;
	
	private GmapObjects gmap;
	private MapRoutesList mapRoutes = null;
	
	private AutoCompleteTextView cmdView;
	
	interface Command {
		void execute(String arg);
	}
	
	private HashMap<String, Command> commands;
	
	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);
		 gmap = new GmapObjects(this);
		 
		 setUpCommands();
		 setUpActionBar();	     
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        gmap.setUpMapIfNeeded();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        gmap.disconnectLocation();
    }
    
    private void setUpActionBar() {
    	ActionBar actionBar = getActionBar();
	     // add the custom view to the action bar
	     actionBar.setCustomView(R.layout.autocomplete);
	     cmdView = (AutoCompleteTextView) actionBar.getCustomView().findViewById(R.id.autocompleteview);
	     
	     configureCmdView();
	     
	     actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
	    	        | ActionBar.DISPLAY_SHOW_HOME);
    }
    
    private void setUpCommands() {
    	commands = new HashMap<String,Command>();
    	commands.put("select", new Command() { public void execute(String arg) {createSelectRouteDialog();}});
    	commands.put("load", new Command() { public void execute(String arg) {showFileDialog();}});
    	commands.put("bound", new Command() { public void execute(String arg) {gmap.boundPolyline();}});
    	commands.put("goto", new Command() { public void execute(String arg) {gmap.gotoMarker(arg);}});
    	commands.put("sat", new Command() { public void execute(String arg) {gmap.showMapAsSatellite();}});
    	commands.put("map", new Command() { public void execute(String arg) {gmap.showMapAsNormal();}});
    	commands.put("gps", new Command() { public void execute(String arg) {gmap.connectLocation();}});
    	commands.put("gpsoff", new Command() { public void execute(String arg) {gmap.disconnectLocation();}});
    }
	
	private void configureCmdView() {
		List<String> cmds = new ArrayList<String>(commands.keySet());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_dropdown_item_1line, cmds);
		cmdView.setAdapter(adapter);
		
	    cmdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_DONE) {
	            	handleActionCommand( cmdView.getText().toString() );
	            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            	imm.hideSoftInputFromWindow(cmdView.getWindowToken(), 0);
	                return true;
	            }
	            // hide keyboard after pressing enter
	            if ( (event.getAction() == KeyEvent.ACTION_DOWN  ) && (actionId == KeyEvent.KEYCODE_ENTER) ) {               	            
	            	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	            	imm.hideSoftInputFromWindow(cmdView.getWindowToken(), 0);
	            	return true;
	            }
	            return false;
	        }
	    });
	    
	    
	    cmdView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if (hasFocus) {
	                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	            }
	        }
	    });
	    cmdView.requestFocus();
	}
	
	private void handleActionCommand(String command) {
		command = command.trim();
		// first: command, second: argument
		Pair<String,String> cmd = MyUtils.splitCommand(command);
		if (commands.containsKey(cmd.first)) {
			commands.get(cmd.first).execute(cmd.second);
		} else if (MyUtils.isInteger(cmd.first)) { // if only a number is given as command: goto marker
			commands.get("goto").execute(cmd.first);
		} else {		
			showShortToast("unkown command " + command);
		}
		cmdView.setText("");
	}
	
	
	private void showFileDialog() {
		Intent intent = new Intent(this, FileChooser.class);
        startActivityForResult(intent, REQUEST_FILE_CHOOSER);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		// See which child activity is calling us back.
		if (requestCode == REQUEST_FILE_CHOOSER){
			if (resultCode == RESULT_OK) { 
				String fileName = data.getStringExtra("GetFileName"); 
				String filePath = data.getStringExtra("GetPath");
				showShortToast(filePath + "/" + fileName);
				
				openTrainingRouteFile(filePath, fileName);
			}
		}
	}
	
	private boolean openTrainingRouteFile(String filePath, String fileName) {
		if ( ! fileName.endsWith("tr")) {
			return false;
		}
		File trFile = new File(filePath, fileName);
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(trFile));
			mapRoutes = new MapRoutesList(in);
			mapRoutes.setActiveRoute(0, gmap);
			gmap.boundPolyline();
		} catch (FileNotFoundException e) {
			showLongToast(filePath + "/" + fileName + " not found!");			
			e.printStackTrace();
		} 
		return true;
	}
	
	public void createSelectRouteDialog() {
		if (mapRoutes == null) return;
		CharSequence[] names = mapRoutes.getRouteNames();
		if (names.length <= 0) {
			showShortToast("No routes selectable.");
			return;
		}
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);	   
	    builder.setTitle(R.string.pick_maproute)
	           .setItems(names, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   mapRoutes.setActiveRoute(which, gmap);
	            	   gmap.boundPolyline();
	           }
	    });
	    builder.show();
	}
		
	
	public void showLongToast(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void showShortToast(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}
}
