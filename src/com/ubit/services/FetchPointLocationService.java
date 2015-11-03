package com.ubit.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ubit.sharedpreferences.SharedPref;



public class FetchPointLocationService extends Service {

	public Vibrator vibrator;
	boolean notify, ring, vibrate, showalert;
	int interval, maxpercent;
	int prevpercent = 0;
	String API_KEY="AIzaSyAz5yqjUbQ0PYZ0FH8NqKrO9YGqTpq4Wbw";
	int status = 0;
	int level = 0;
	String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    String secondurl="&radius=300&types=establishment&key="+API_KEY;
	Context context;
	String completeurl;
	Location loc;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		context = this;
		checkgps();
		
		
		
		new fetchdataviaapi().execute();
		
		
	
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
		super.onTaskRemoved(rootIntent);
		Intent in = new Intent();
		in.setAction("YouWillNeverKillMe");
		sendBroadcast(in);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Intent in = new Intent();
		in.setAction("YouWillNeverKillMe");
		sendBroadcast(in);

	}

	public void startVibrate(View v) {
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(1000);
		// vibrator.cancel();

	}

	
	public void checkgps() {
		LocationManager lm = null;
		 boolean gps_enabled = false,network_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }catch(Exception ex){}
		    try{
		    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		    }catch(Exception ex){}

		   if(!gps_enabled && !network_enabled){
		        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		        dialog.setMessage("Your Gps is not enable");
		        dialog.setPositiveButton("Enable", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		                // TODO Auto-generated method stub
		                Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                context.startActivity(myIntent);
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
	
	
	
	
	public class fetchdataviaapi extends AsyncTask<Void, Void, String> {
         @Override
        protected void onPreExecute() {
        	// TODO Auto-generated method stub
        	super.onPreExecute();
        	GPSTracker gps=new GPSTracker(context);
        	loc=gps.getLocation();
        	if(loc!=null) {
    		completeurl=url+loc.getLatitude()+","+loc.getLongitude()+secondurl;
        	}
        	
        	
        	
        }
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String responseString = null;
			if(isNetworkAvailable() && checkGpsEnabled()) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
			  try {
				  HttpClient httpclient = new DefaultHttpClient();
			        HttpResponse response;
			        
				  
		            response = httpclient.execute(new HttpGet(completeurl));
		            StatusLine statusLine = response.getStatusLine();
		            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
		                ByteArrayOutputStream out = new ByteArrayOutputStream();
		                response.getEntity().writeTo(out);
		                out.close();
		                responseString = out.toString();
		            } else{
		                //Closes the connection.
		                response.getEntity().getContent().close();
		                throw new IOException(statusLine.getReasonPhrase());
		            }
		        } catch (ClientProtocolException e) {
		            //TODO Handle problems..
		        } catch (IOException e) {
		            //TODO Handle problems..
		        }
			}
			return responseString;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			SharedPref pref=new SharedPref(context);
			if(result!=null) {
			String address="";
			try {
				JSONObject j=new JSONObject(result);
				JSONArray jarr=j.getJSONArray("results");
				JSONObject jobj=jarr.getJSONObject(0);
				address+=jobj.getString("name");
				address+=jobj.getString("vicinity");
				if(jarr.length()>1) {
					address+="&";
					JSONObject jobj1=jarr.getJSONObject(1);
					address+=jobj1.getString("name");
					address+=jobj1.getString("vicinity");
				
				}
				Log.e("Response Finally.", "API Response");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(address.length()<160) {
			pref.savepointlocation(address);
			if(pref.getvibrate()) {
			vibrateDevice();
			}
			Toast.makeText(context,address,Toast.LENGTH_LONG).show();
			}
			else {
				pref.savepointlocation(address.substring(0, 159));
			}
			}
			if(pref.getboolean()) {
				
			new fetchdataviaapi().execute();
			}
		}
		
	}
	public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	
	public void vibrateDevice(){
		Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 500 milliseconds
		 v.vibrate(1000);
	}
	
	public boolean checkGpsEnabled() {
		LocationManager lm = null;
		 boolean gps_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }catch(Exception ex){
		    	gps_enabled=false;
		    }
		    return gps_enabled;
	}
	
}
