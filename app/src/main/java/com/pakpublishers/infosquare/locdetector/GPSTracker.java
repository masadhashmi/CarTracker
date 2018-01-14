package com.pakpublishers.infosquare.locdetector;

/**
 * Created by M Asad Hashmi on 12/02/2017.
 */

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Create this Class from tutorial :
 * http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial
 *
 * For Geocoder read this : http://stackoverflow.com/questions/472313/android-reverse-geocoding-getfromlocation
 *
 */

public class GPSTracker implements LocationListener {

    // Get Class Name
    private static String TAG = GPSTracker.class.getName();

    private final Context mContext;

    // flag for GPS Status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;
    boolean isPassiveEnabled = false;

    // flag for GPS Tracking is enabled
    boolean isGPSTrackingEnabled = false;
    public String ErrorMessage="";

    Location location;
    double latitude;
    double longitude;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;


    public static boolean hasPermission_ACCESS_FINE_LOCATION(Context cntx)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cntx.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && cntx.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               // ErrorMessage="Permission not granted.";
                return false;
            }
        }
        return true;
    }
    public static boolean hasPermission_INTERNET(Context cntx)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cntx.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                    ) {
                // ErrorMessage="Permission not granted.";
                return false;
            }
        }
        return true;
    }

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private String provider_info;

    public GPSTracker(Context context) {
        this.mContext = context;
       // getLocation();
    }


    /**
     * Try to get my current location by GPS or Network Provider
     */



    public boolean getLocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);


            if (isPassiveEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                provider_info = LocationManager.PASSIVE_PROVIDER;

            }else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true;

                provider_info = LocationManager.NETWORK_PROVIDER;

            } else if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true;

                provider_info = LocationManager.GPS_PROVIDER;

            }
            else
            {
                ErrorMessage="Not Network Found for location.";
                return false;
            }

            // Application can use GPS or Network Provider
            if (!provider_info.isEmpty()) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ErrorMessage="Permission not granted.";
                        return false;
                    }
                }
                locationManager.requestLocationUpdates(
                        provider_info,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                );

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(provider_info);
                    updateGPSCoordinates();
                    return  true;
                }
                else
                {
                    ErrorMessage="Location Manager is not found.";
                    return false;

                }
            }
            else
            {
                ErrorMessage="Provider info not found. it is empty.";
                return false;
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            String msg="Impossible to connect to LocationManager"+e;
            //Log.e(TAG, "Impossible to connect to LocationManager", e);
            ErrorMessage=msg;
            return  false;
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
    public boolean getIsGPSTrackingEnabled() {

        return this.isGPSTrackingEnabled;
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */

    public void stopUsingGPS() {
        try {
            if (locationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                locationManager.removeUpdates(GPSTracker.this);

            }
        }catch (Exception er){}
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        //Setting Dialog Title
//        alertDialog.setTitle(R.string.GPSAlertDialogTitle);
//
//        //Setting Dialog Message
//        alertDialog.setMessage(R.string.GPSAlertDialogMessage);
//
//        //On Pressing Setting button
//        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//
//        //On pressing cancel button
//        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.cancel();
//            }
//        });
//
//        alertDialog.show();
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);

                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


}