package com.ubit.smsreceiver;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.ubit.sharedpreferences.SharedPref;

public class SmsReceiver extends BroadcastReceiver {
	public static String phonenumber = "";
	public static String pointname = "";
	public static String pointid = "";
	Context con;
	SharedPref pref;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		pref = new SharedPref(arg0);
		boolean receive = pref.getboolean();
		if (receive) {
			final Bundle bundle = arg1.getExtras();
			con = arg0;

			try {

				if (bundle != null) {

					final Object[] pdusObj = (Object[]) bundle.get("pdus");

					for (int i = 0; i < pdusObj.length; i++) {

						android.telephony.SmsMessage currentMessage = android.telephony.SmsMessage
								.createFromPdu((byte[]) pdusObj[i]);
						String phoneNumber = currentMessage
								.getDisplayOriginatingAddress();

						String senderNum = phoneNumber;
						String message = currentMessage.getDisplayMessageBody();

						Log.i("SmsReceiver", "senderNum: " + senderNum
								+ "; message: " + message);
						if (message.toLowerCase().startsWith("where are you?")) {
							// do some thing when message received point
							String address = pref.getpointlocation();
							if (!address.equals("nolocation")&& checkGpsEnabled()) {
								replytosender(address, senderNum);
							}
						}
					}
				}

			}

			catch (Exception e) {
				Log.e("SmsReceiver", "Exception smsReceiver" + e);

			}
		}

	}

	public void replytosender(String address,String PhoneNumber) {
       SmsManager sms = SmsManager.getDefault();
       String mainmessage="I am at Near "+address;
       sms.sendTextMessage(PhoneNumber, null, mainmessage, null, null);
//        String SMS_SEND_ACTION = "CTS_SMS_SEND_ACTION";
//       String SMS_DELIVERY_ACTION = "CTS_SMS_DELIVERY_ACTION";
//	   Intent mSendIntent = new Intent(SMS_SEND_ACTION);
//	   Intent mDeliveryIntent = new Intent(SMS_DELIVERY_ACTION);
//
//	   ArrayList<String> parts =sms.divideMessage(mainmessage);
//	   int numParts = parts.size();
//       Toast.makeText(con,"Message Length:"+mainmessage.length(),3000).show();  
//	   ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
//	   ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
//
//	   for (int i = 0; i < numParts; i++) {
//	   sentIntents.add(PendingIntent.getBroadcast(con, i, mSendIntent, 0));
//	   deliveryIntents.add(PendingIntent.getBroadcast(con, i, mDeliveryIntent, 0));
//	   }
//
//	   sms.sendMultipartTextMessage(PhoneNumber, "", parts, sentIntents, deliveryIntents);
	   
	   
	   
	}
	
	
	public boolean checkGpsEnabled() {
		LocationManager lm = null;
		 boolean gps_enabled = false;
		    if(lm==null)
		        lm = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
		    try{
		    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		    }catch(Exception ex){
		    	gps_enabled=false;
		    }
		    return gps_enabled;
	}
	
	
	
}
