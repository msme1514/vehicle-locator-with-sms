package com.ubit.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.AvoidXfermode.Mode;
import android.preference.Preference;

public class SharedPref {
	Context context;
	SharedPreferences pref;
	Editor editor;
	public static String pointlocation="curlocationofpoint";
	public static String servicerunning="servicerunning";
	public static String vibratedevice="vibratedevice";
	public SharedPref(Context con)
	{
		context=con;
		pref=context.getSharedPreferences("fypubit", Context.MODE_PRIVATE); 
		editor=pref.edit();
	}
	
	public void savepointlocation(String resp)
	{
         editor.putString(pointlocation, resp);
         editor.commit();
	}
	public void saveboolean(boolean value)
	{
         editor.putBoolean(servicerunning, value);
         editor.commit();
	}
	
	public void savevibrate(boolean value)
	{
         editor.putBoolean(vibratedevice, value);
         editor.commit();
	}

	public Boolean getvibrate()
	{
		boolean response= pref.getBoolean(vibratedevice,false);
        return response;
	}
	
	public String getpointlocation()
	{
		String response= pref.getString(pointlocation,"nolocation");
        return response;
	}
	
	public boolean getboolean()
	{
		boolean value= pref.getBoolean(servicerunning,false);
        return value;
	}
	
	
}
