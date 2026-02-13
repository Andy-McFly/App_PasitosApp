package es.studium.pasitosapp;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Localizacion implements LocationListener
{
    private TextView txtLatitudLongitud;
    MainActivity mainActivity;

    public Localizacion(TextView txtLL)
    {
        txtLatitudLongitud = txtLL;
    }
    public void setMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override public void onLocationChanged(Location loc)
    {
        mainActivity.setLocation(loc);
    }

    @Override public void onProviderDisabled(String provider){}
    @Override public void onProviderEnabled(String provider){}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        switch (status)
        {
            case 0: Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case 1: Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
            case 2: Log.d("debug", "LocationProvider.AVAILABLE");
                break;
        }
    }

}
