package com.example.ncs.lifeon.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ncs.lifeon.ECT.DatabaseGPS;
import com.example.ncs.lifeon.ECT.DatabaseGPSController;
import com.example.ncs.lifeon.ECT.ListViewGPSAdapter;
import com.example.ncs.lifeon.R;

import java.util.ArrayList;

import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_GPS;
import static com.example.ncs.lifeon.ECT.Const.name;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class RegisterGPSFragment extends Fragment {

    ListView listView;

    SQLiteDatabase db;
    DatabaseGPSController dbController;

    private static RegisterGPSFragment instance = new RegisterGPSFragment();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_gps, container, false);

        if(view !=null){
            view.setEnabled(false);
            view.setSaveFromParentEnabled(false);
        }

        listView = (ListView) view.findViewById(R.id.ListViewGPS);

        dbController = new DatabaseGPSController(getContext());
        db = dbController.getWritableDatabase();

        final View header = getActivity().getLayoutInflater().inflate(R.layout.listview_gps_header, null, false);
        listView.addHeaderView(header);

        DatabaseReset();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseGPS item = (DatabaseGPS) parent.getItemAtPosition(position);
                if (item.getLocation() != null) {
                    db.delete(TABLE_NAME_GPS, "location = ?", new String[]{String.valueOf(item.getLocation())});
                    DatabaseReset();
                }
                return false;
            }
        });
        return view;
    }

    public void DatabaseReset() {
        try {
            String query = "SELECT * FROM " + TABLE_NAME_GPS + " WHERE name='" + name + "';";
            Cursor cursor = db.rawQuery(query, null);
            ArrayList<DatabaseGPS> arrayList = new ArrayList<DatabaseGPS>();
            arrayList.clear();

            while (cursor.moveToNext()) {
                DatabaseGPS database = new DatabaseGPS();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String locationName = cursor.getString(cursor.getColumnIndex("locationName"));
                String location = cursor.getString(cursor.getColumnIndex("location"));

                database.setName(name);
                database.setLocationName(locationName);
                database.setLocation(location);

                arrayList.add(database);
            }
            cursor.close();
            ListViewGPSAdapter listViewAdapter = new ListViewGPSAdapter(getContext(), arrayList);
            listView.setAdapter(listViewAdapter);
        } catch (Exception e) {

        }
    }

    public static synchronized RegisterGPSFragment getInstance() {
        return instance;
    }
}