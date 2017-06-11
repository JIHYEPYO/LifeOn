package com.example.ncs.lifeon.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ncs.lifeon.ECT.DatabaseGPSController;
import com.example.ncs.lifeon.ECT.DatabasePhone;
import com.example.ncs.lifeon.ECT.DatabasePhoneController;
import com.example.ncs.lifeon.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.example.ncs.lifeon.ECT.Const.FIRSTRUN;
import static com.example.ncs.lifeon.ECT.Const.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.example.ncs.lifeon.ECT.Const.MY_PERMISSIONS_REQUEST_SEND_SMS;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_GPS;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_PHONE;
import static com.example.ncs.lifeon.ECT.Const.TIME;
import static com.example.ncs.lifeon.ECT.Const.latLng;
import static com.example.ncs.lifeon.ECT.Const.location;
import static com.example.ncs.lifeon.ECT.Const.locationManager;
import static com.example.ncs.lifeon.ECT.Const.name;
import static com.example.ncs.lifeon.ECT.Const.smsText;
import static com.example.ncs.lifeon.R.id.map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    SupportMapFragment mapFragment;

    private TimerTask second;
    private final Handler handler = new Handler();
    int timer_sec;
    private PolylineOptions polylineOptions;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        requestPermisson();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkLocationPermission();
    }

    private ArrayList<LatLng> arrayPoints;

    DatabaseGPSController dbController;
    DatabasePhoneController dbController2;
    SQLiteDatabase db;
    SQLiteDatabase db2;
    SharedPreferences settings;
    AsyncTaskCancelTimerTask timerTask;
    AsyncTask task;

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
        dbController2 = new DatabasePhoneController(view.getContext());
        try {
            db = dbController.getWritableDatabase();
            db2 = dbController2.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbController.getReadableDatabase();
            db2 = dbController2.getReadableDatabase();
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
                location=mMap.getMyLocation();
                if (settings.getBoolean("IdentifyActivity", true) && location!=null) {
                    if (latLng == null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        timerTask = new AsyncTaskCancelTimerTask(task, Integer.parseInt(settings.getString("timeRegister", "5000")), 1000, true);
                        drawPolyLineOnMap(arrayPoints);
                    } else if (!latLng.equals(new LatLng(location.getLatitude(), location.getLongitude()))) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        timerTask = new AsyncTaskCancelTimerTask(task, Integer.parseInt(settings.getString("timeRegister", "50000")), Integer.parseInt(settings.getString("timeRegister", "5000")), true);
                        drawPolyLineOnMap(arrayPoints);
                    } else if (latLng.equals(new LatLng(location.getLatitude(), location.getLongitude()))) {
                        if (settings.getBoolean("SendMessage", true)) {
                            if (latLng.equals(new LatLng(location.getLatitude(), location.getLongitude()))) {
                                try {
                                    String query = "SELECT * FROM " + TABLE_NAME_PHONE + " WHERE name = '" + name + "';";
                                    Cursor cursor = db2.rawQuery(query, null);
                                    while (cursor.moveToNext()) {
                                        DatabasePhone database = new DatabasePhone();
                                        String name = cursor.getString(cursor.getColumnIndex("name"));
                                        String personName = cursor.getString(cursor.getColumnIndex("personName"));
                                        String personPhone = cursor.getString(cursor.getColumnIndex("personPhone"));

                                        database.setName(name);
                                        database.setPersonName(personName);
                                        database.setPersonPhone(personPhone);

                                        Toast.makeText(getActivity(), personPhone + "", Toast.LENGTH_SHORT).show();
                                        sendSMS(personPhone);
                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    Log.d("nnnn",e.getMessage());
                                }
                            }
                        }
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
                dbController = new DatabaseGPSController(getContext());
                try {
                    db = dbController.getWritableDatabase();
                } catch (SQLiteException e) {
                    db = dbController.getReadableDatabase();
                }

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

    public void requestPermisson() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    public void sendSMS(String smsNumber) {
        PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_SENT_ACTION"), 0);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "Send!", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getContext(), "Send Error", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getContext(), "No Service Area", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getContext(), "Radio Off", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getContext(), "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT_ACTION"));

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getContext(), "SMS Send Success!", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getContext(), "SMS Send Failed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText + name + "'s location : " + latLng.latitude + ", " + latLng.longitude, sentIntent, deliveredIntent);
    }

    static class AsyncTaskCancelTimerTask extends CountDownTimer {
        private AsyncTask asyncTask;
        private boolean interrupt;

        private AsyncTaskCancelTimerTask(AsyncTask asyncTask, long startTime, long interval) {
            super(startTime, interval);
            this.asyncTask = asyncTask;
        }

        private AsyncTaskCancelTimerTask(AsyncTask asyncTask, long startTime, long interval, boolean interrupt) {
            super(startTime, interval);
            this.asyncTask = asyncTask;
            this.interrupt = interrupt;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (asyncTask == null) {
                this.cancel();
                return;
            }

            if (asyncTask.isCancelled())
                this.cancel();

            if (asyncTask.getStatus() == AsyncTask.Status.FINISHED)
                this.cancel();
        }

        @Override
        public void onFinish() {
            if (asyncTask == null || asyncTask.isCancelled())
                return;

            try {
                if (asyncTask.getStatus() == AsyncTask.Status.FINISHED)
                    return;

                if (asyncTask.getStatus() == AsyncTask.Status.PENDING || asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                    asyncTask.cancel(interrupt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
