package com.jb.Android.JBSMS_GP;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class GetGPS
{
    Context context;
    ConnectivityManager connectivityManager;
    LocationManager locationManager;
    //LocationListener locationListener;
    Location Mylocation,UseNetWorklocation;
    private String strLocationProvider = "";
    double[] dGpsPosition = new double[2];
    private Handler hHandler = new Handler();
    private Runnable run = new Runnable()
    {
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			hHandler.postDelayed(run, 1000);	
		}
    	
    };
	/************************************
	 * Constructor: CheckStatusCS
	 * Input: cont:Context
	 * Output: none
	 * Description: Constructor
	 *************************************/
    public GetGPS(Context cont)
    {
        context = cont;
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE); 
    };
    
	/************************************
	 * Method: CheckNetWork
	 * Input: none
	 * Output: true/false
	 * Description: 檢查是否有連網路
	 *************************************/
    public boolean CheckNetWork() 
    {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        
        if(networkInfo != null)
            return true;
        else
            return false;
    }

	/************************************
	 * Method: CheckGPS
	 * Input: none
	 * Output: boolean
	 * Description: Check GPS
	 *************************************/
    public boolean CheckGPS() 
    {
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        
        return gpsStatus;
    }
    
	/************************************
	 * Method: GetGPSCurPosition
	 * Input: none
	 * Output: dGpsPosition01 : double[2]
	 * Description: 取得GPS經緯度
	 *************************************/
    public double[] GetGPSCurPosition()
    {
    	double[] dGpsPosition01 = new double[2];
    
    	try
    	{
	    	Mylocation = getLocationProvider(locationManager);
	    	dGpsPosition01 = getGeoByLocation(Mylocation);
	    	locationManager.requestLocationUpdates(strLocationProvider, 2000, 0, MyLocationListener);
    	}
    	catch(Exception ex)
    	{
    		Log.e("JB_TAG","GetGPSCurPosition()錯誤："+ex.getMessage());
    	}
        return dGpsPosition01;
    }
	/************************************
	 * Method: getGeoByLocation
	 * Input: location : Location 
	 * Output: dGpsPosition : double[2]
	 * Description: Get GeoPoint By Location
	 *************************************/
    public double[] getGeoByLocation(Location location)
    {
    	if(location != null)
        {
        	dGpsPosition[0] = location.getLongitude();//經度
        	dGpsPosition[1] = location.getLatitude();//緯度
        }
        else
        {
        	//GPS無法定位
        	dGpsPosition[0] = 0;
        	dGpsPosition[1] = 0;
        }
        return dGpsPosition;
    }
	/************************************
	 * Method: getLocationProvider
	 * Input: lManager : LocationManager 
	 * Output: lReturn : Location
	 * Description: Get LocationProvider
	 *************************************/
    public Location getLocationProvider(LocationManager lManager)
    {
    	Location lReturn = null;
    	try
    	{
	    	Criteria criteria = new Criteria();
	    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    	criteria.setAltitudeRequired(false);
	    	criteria.setBearingRequired(false);
	    	criteria.setCostAllowed(true);
	    	criteria.setPowerRequirement(Criteria.POWER_LOW);
	    	strLocationProvider = lManager.getBestProvider(criteria, true);
	    	lManager.requestLocationUpdates(strLocationProvider, 2000, 0, MyLocationListener);
	    	if(lManager.getLastKnownLocation("gps") != null)
	    	{
	    		lManager.requestLocationUpdates(strLocationProvider, 2000, 0, MyLocationListener);
	    		lReturn = lManager.getLastKnownLocation(strLocationProvider);
	        }
	    	else if (lManager.getLastKnownLocation("network") != null)
	    	{
	    	      short i=0;
	    	      lReturn = lManager.getLastKnownLocation(strLocationProvider);
	    	      while(lReturn  == null)
	    	      {
	    	    	  lManager.requestLocationUpdates("gps", 2000, 0, MyLocationListener);
	    	    	  lReturn = lManager.getLastKnownLocation(strLocationProvider);
	    	    	  hHandler.postDelayed(run, 1000);
	    	        if(i++ == 30)
	    	        {
	    	        	lReturn = lManager.getLastKnownLocation("network");
	    	            break;
	    	        }
	    	      }
	    	}
	    	else
	    	{
		    	lManager.requestLocationUpdates(strLocationProvider, 2000, 0, MyLocationListener);
		    	lReturn = lManager.getLastKnownLocation(strLocationProvider); 
	    	}

    	}
    	catch(Exception ex)
    	{
    		Log.e("JB_TAG","錯誤："+ex.getMessage());
    	}
    	return lReturn;
    }
	/************************************
	 * Method: stopAllUpdate
	 * Input: none
	 * Output: none
	 * Description: destroy LocationListener
	 *************************************/
	public void stopAllUpdate()
	{
		locationManager.removeUpdates(MyLocationListener);  
	} 

    public final LocationListener MyLocationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location) 
        {
            // TODO Auto-generated method stub
        	Mylocation = location;
        	dGpsPosition = getGeoByLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) 
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onProviderEnabled(String provider) 
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            
        }
    };
}