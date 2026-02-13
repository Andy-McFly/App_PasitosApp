package es.studium.pasitosapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import es.studium.pasitosapp.controllers.UbicacionController;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener
{
    ImageButton btnBorrar;
    private GoogleMap mapa;
    PolylineOptions polylineOptions;
    private TextView txfLatitudLongitud, txfBateria, txfMarker;
    private UbicacionController ubicacionController;
    Handler handler = new Handler();
    double ultimaLat = 0;
    double ultimaLon = 0;
    boolean ubicacionInicial = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txfLatitudLongitud = findViewById(R.id.tvLatLon);
        txfBateria = findViewById(R.id.tvBateria);
        txfMarker = findViewById(R.id.tvInfoMarker);
        btnBorrar = findViewById(R.id.btnBorrar);
        polylineOptions = new PolylineOptions();
        polylineOptions.width(10);
        polylineOptions.color(Color.rgb(98,61,220));

        ubicacionController = new UbicacionController(this);

        btnBorrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                ubicacionController.borrarDatos();
                mapa.clear();
                txfLatitudLongitud.setText("");
                txfBateria.setText("");
                txfMarker.setText("");
                ubicacionInicial = true;
                polylineOptions = new PolylineOptions();
                polylineOptions.width(10);
                polylineOptions.color(Color.rgb(98,61,220));
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMapa);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    }, 1000);
        }
        else
        {
            locationStart();
        }

        guardarUbicacion();
    }

    private void locationStart()
    {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion(txfLatitudLongitud);
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled)
        {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    }, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                locationStart();
            }
        }
    }

    public void setLocation(Location loc)
    {
        ultimaLat = loc.getLatitude();
        ultimaLon = loc.getLongitude();

        if(ubicacionInicial)
        {
            int bateria = obtenerBateria();
            ubicacionController.insertar(ultimaLat, ultimaLon, bateria);
            nuevoMarker(ultimaLat, ultimaLon, bateria);

            ubicacionInicial = false;
        }
    }

    private void guardarUbicacion()
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if(ultimaLat != 0 && ultimaLon != 0)
                {
                    int bateria = obtenerBateria();
                    ubicacionController.insertar(ultimaLat, ultimaLon, bateria);
                    nuevoMarker(ultimaLat, ultimaLon, bateria);
                }

                handler.postDelayed(this, 60000);
            }
        };

        handler.post(runnable);
    }

    private int obtenerBateria()
    {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int bateria = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return bateria;
    }

    private void nuevoMarker(double lat,double lon,int bateria)
    {
        LatLng pos = new LatLng(lat,lon);

        if(ubicacionInicial)
        {
            mapa.addMarker(new MarkerOptions().position(pos).title(getString(R.string.texto_bateria) + bateria + "%")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.marcador)));
        }
        else
        {
            mapa.addMarker(new MarkerOptions().position(pos).title(getString(R.string.texto_bateria) + bateria + "%")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.marcadorb)));
        }

        polylineOptions.add(pos);
        mapa.addPolyline(polylineOptions);
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,17.5f));

        txfLatitudLongitud.setText(getString(R.string.texto_info)
                + "\n" + getString(R.string.texto_Latitud) + ultimaLat
                + "\n" + getString(R.string.texto_Longitud)+ ultimaLon);
        txfBateria.setText(getString(R.string.texto_bateria) + bateria + "%");
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mapa = googleMap;
        mapa.setOnMarkerClickListener(this);
        mapa.setOnMapClickListener(this);

        Cursor cursor = ubicacionController.obtenerUbicaciones();

        while(cursor.moveToNext())
        {
            nuevoMarker(cursor.getDouble(1),cursor.getDouble(2),cursor.getInt(3));
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        LatLng pos = marker.getPosition();

        txfMarker.setText("Lat: " + pos.latitude + "\nLon: " + pos.longitude);
        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng)
    {
        txfMarker.setText("");
    }
}