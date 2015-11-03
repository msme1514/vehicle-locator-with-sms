package com.ubit.fypproject;

import com.ubit.services.FetchPointLocationService;
import com.ubit.sharedpreferences.SharedPref;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
    Button startservice,stopservice,vibrate;
    Context _con;
    SharedPref pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
		checkgps();
		vibrate();
		changeButtonText();
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		boolean oskitkat=false;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
		     oskitkat=true;
		}

		   if(oskitkat){
		        AlertDialog.Builder dialog = new AlertDialog.Builder(_con);
		        dialog.setMessage("If app closes service will stop, are you sure to close the app?");
		        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub
		            	finish();
		                //get gps
		            }
		        });
		        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub

		            }
		        });
		        dialog.show();
		   }
		   else {
			   super.onBackPressed();
		   }
		
		
		
		
	}

	public void changeButtonText(){
		boolean running=pref.getboolean();
		if(running && checkGpsEnabled()) {
			startservice.setBackgroundColor(Color.parseColor("#7cd200"));
			startservice.setText("Service Started.");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void init()
	{
		_con=this;
		pref=new SharedPref(_con);
		startservice=(Button)findViewById(R.id.btn_startservice);
		stopservice=(Button)findViewById(R.id.btn_stopservice);
		vibrate=(Button)findViewById(R.id.btn_stopvibrate);
		startservice.setOnClickListener(this);
		stopservice.setOnClickListener(this);
		vibrate.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id=arg0.getId();
		switch(id)
		{
		case R.id.btn_startservice:
			startservice();
			startservice.setBackgroundColor(Color.parseColor("#7cd200"));
			startservice.setText("Service Started.");
			break;
			
		case R.id.btn_stopservice:	
			stopservice();
			startservice.setBackgroundColor(Color.parseColor("#de4940"));
			startservice.setText("Service Stopped");
			break;
		case R.id.btn_stopvibrate:	
			boolean vib=pref.getvibrate();
			if(vib) {
				vib=false;
				makeDeviceVibate(vib);
			}
			else {
				vib=true;
				makeDeviceVibate(vib);
			}
			
			break;
		}
	}
	
	
	public void startservice() {
		
		
		LocationManager lm = null;
		 boolean gps_enabled = false,network_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) _con.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }
		    catch(Exception e){
		    	
		    }
		if(gps_enabled) {
			pref.saveboolean(true);
		startService(new Intent(MainActivity.this,FetchPointLocationService.class));
		
		}
		else {
			checkgps();
		}
	}
	public void stopservice() {
		pref.saveboolean(false);
		stopService(new Intent(MainActivity.this,FetchPointLocationService.class));
		
	}
	public void vibrate() {
		boolean check=pref.getvibrate();
		if(check) {
			vibrate.setText("Disable Vibration");
		}
		else {
			vibrate.setText("Enable Vibration");
		}
	}
	public void makeDeviceVibate(boolean booleanvibrate){
		pref.savevibrate(booleanvibrate);
		
		if(booleanvibrate) {
			vibrate.setText("Disable Vibration");
		}
		else {
			vibrate.setText("Enable Vibration");
		}
	}
	public void checkgps() {
		LocationManager lm = null;
		 boolean gps_enabled = false,network_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) _con.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }catch(Exception ex){}
		    try{
		    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		    }catch(Exception ex){}

		   if(!gps_enabled && !network_enabled){
		        AlertDialog.Builder dialog = new AlertDialog.Builder(_con);
		        dialog.setMessage("Your Gps is not enable");
		        dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub
		                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                _con.startActivity(myIntent);
		                //get gps
		            }
		        });
		        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub

		            }
		        });
		        dialog.show();
		   }
	}
	public boolean checkGpsEnabled() {
		LocationManager lm = null;
		 boolean gps_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) _con.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }catch(Exception ex){
		    	gps_enabled=false;
		    }
		    return gps_enabled;
	}
	

}
