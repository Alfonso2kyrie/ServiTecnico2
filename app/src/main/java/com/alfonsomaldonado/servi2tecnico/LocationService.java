package com.alfonsomaldonado.servi2tecnico;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {

    public static Location loc = null;

    private LocationManager locationManager = null;


    private class LocationListener implements android.location.LocationListener {

        Location myLocation;

        public LocationListener(String provider) {
            myLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                loc = location;
                System.out.println("Latitud " + location.getLatitude());
                System.out.println("Longitud " + location.getLongitude());
            } else {
                System.out.println("Mi ubicación es null");
            }
            myLocation.set(location);
        }


    }

    LocationListener[] mLocationListener = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocationManager();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10,
                mLocationListener[1]
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                2000,
                10,
                mLocationListener[0]
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            for(int i = 0; i < mLocationListener.length; i++){
                locationManager.removeUpdates(mLocationListener[i]);
            }
        }
    }

    private void initLocationManager(){
        if(locationManager == null){
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void onProviderEnabled(@NonNull String provider) {

    }
    public void onProviderDisabled(@NonNull String provider) {

    }
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
