package com.ubit.receivers;





import com.ubit.services.FetchPointLocationService;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
 
// here is the OnRevieve methode which will be called when boot completed
public class BootCompleted extends BroadcastReceiver{
	public static boolean flag=false;
     @Override
     public void onReceive(Context context, Intent intent) {
 //we double check here for only boot complete event
 if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
   {
     //here we start the service             
    Intent serviceIntent = new Intent(context, FetchPointLocationService.class);
     flag=true;
     context.startService(serviceIntent);
   }
 }
}
