package com.example.ncs.lifeon.ECT;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class Const {
    public static String name;
    public static int step;
    public static int TIME=5000;

    public static LocationManager locationManager;
    public static Location location;

    public static String intentFilterStep = "step";
    public static final String FIRSTRUN = "FirstRun";
    public static final String TABLE_NAME_STEP = "step";
    public static final String TABLE_NAME_GPS = "gps";
    public static final String TABLE_NAME_PHONE = "phone";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int DEFAULT_ZOOM_LEVEL = 18;
}
