package com.example.ncs.lifeon.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ncs.lifeon.Activity.MainActivity;
import com.example.ncs.lifeon.ECT.DatabaseGPSController;
import com.example.ncs.lifeon.Manifest;
import com.example.ncs.lifeon.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.DEFAULT_ZOOM_LEVEL;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;
import static com.example.ncs.lifeon.ECT.Const.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_GPS;
import static com.example.ncs.lifeon.ECT.Const.TIME;
import static com.example.ncs.lifeon.ECT.Const.location;
import static com.example.ncs.lifeon.ECT.Const.locationManager;
import static com.example.ncs.lifeon.ECT.Const.name;
import static com.example.ncs.lifeon.R.id.map;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    SupportMapFragment mapFragment;

    private TimerTask second;
    private LatLng latLng;
    private final Handler handler = new Handler();
    int timer_sec;
    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints;

    DatabaseGPSController dbController;
    SQLiteDatabase db;
    SharedPreferences settings;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        checkLocationPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        settings = this.getActivity().getSharedPreferences(FIRSTRUN, MODE_PRIVATE);

        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        location = locationManager.getLastKnownLocation(provider);

        arrayPoints = new ArrayList<LatLng>();

        dbController = new DatabaseGPSController(view.getContext());

        try {
            db = dbController.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbController.getReadableDatabase();
        }

        return view;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        testStart();
    }

    public void testStart() {
        timer_sec = 0;

        second = new TimerTask() {
            @Override
            public void run() {
                Log.i("Test", "Timer start");
                Update();
                timer_sec++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(second, 0, TIME);
    }

    protected void Update() {
        Runnable updater = new Runnable() {
            public void run() {
                if(settings.getBoolean("IdentifyActivity",true)){
                    if (latLng == null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        drawPolyLineOnMap(arrayPoints);
                    } else if (!latLng.equals(new LatLng(location.getLatitude(), location.getLongitude()))) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        drawPolyLineOnMap(arrayPoints);
                    }
                }
            }
        };
        handler.post(updater);
    }


    public void drawPolyLineOnMap(List<LatLng> list) {
        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle("Register your favorite places?");
        alertDialog.setMessage("Please enter the name of the place to register.");

        final EditText editText = new EditText(getActivity());
        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString();
                Log.v(TAG, value);
                db.execSQL("INSERT INTO " + TABLE_NAME_GPS + " VALUES (null, '" + name + "', '" + value + "', '" + latLng.toString() + "');");
                dialog.dismiss();

                Toast.makeText(getActivity(), "Register : '" + value + "'", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
