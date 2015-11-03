package com.ubit.receivers;



import com.ubit.services.FetchPointLocationService;
import com.ubit.sharedpreferences.SharedPref;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceDestroyReceiver extends BroadcastReceiver{
    SharedPref pref;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		pref=new SharedPref(context);
		boolean restart=pref.getboolean();
		if(restart) {
		Intent service = new Intent(context, FetchPointLocationService.class);
		context.startService(service);
		}
	}
	
	

}
