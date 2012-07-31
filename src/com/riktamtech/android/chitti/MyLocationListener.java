package com.riktamtech.android.chitti;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener
{

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		String text = "Your Current Location is: Latitutde="+location.getLatitude()+"	\n"+"Longitude="+location.getLongitude()+"	";
//		Toast tst = Toast.makeText(Main.this, text, Toast.LENGTH_SHORT);
//		tst.show();
//		Log.d("Location", text);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
//		Toast tst = Toast.makeText(Main.this, "Network Provider Disabled", Toast.LENGTH_SHORT);
//		tst.show();
		
		String text = "Network Provider Disabled";
//		Log.d("Location", text);
	
		
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
//		Toast tst = Toast.makeText(Main.this, "Network Provider Enabled", Toast.LENGTH_SHORT);
//		tst.show();
		
		String text = "Network Provider Enabled";
		
//		Log.d("Location",text);
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}