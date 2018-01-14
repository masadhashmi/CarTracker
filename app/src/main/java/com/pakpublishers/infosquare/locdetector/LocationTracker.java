package com.pakpublishers.infosquare.locdetector;

import android.content.Context;
import android.location.Location;

/**
 * Created by M Asad Hashmi on 12/02/2017.
 */

public class LocationTracker {

    public GPSTracker _gpsTracker=null;
    private Context _Context=null;

    public static int CHECK_FINE_LOCATION_PERMISSION=1;
    public static int CHECK_INTERNET_PERMISSION=2;


    public LocationTracker(Context cntxt)
    {
        _Context=cntxt;
        _gpsTracker=new GPSTracker(cntxt);
    }

    public boolean getLocation()
    {
        return _gpsTracker.getLocation();

    }

    public static  boolean hasPermission_ACCESS_FINE_LOCATION(Context cntxt)
    {
        return GPSTracker.hasPermission_ACCESS_FINE_LOCATION(cntxt);
    }
    public static  boolean hasPermission_INTERNET(Context cntxt)
    {
        return GPSTracker.hasPermission_INTERNET(cntxt);
    }

}
