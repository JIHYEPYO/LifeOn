package com.example.ncs.lifeon.ECT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ncs.lifeon.R;

import java.util.ArrayList;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class ListViewGPSAdapter extends BaseAdapter {

    Context context;
    ArrayList<DatabaseGPS> arrayList;

    public ListViewGPSAdapter(Context context, ArrayList<DatabaseGPS> list) {
        this.context = context;
        arrayList = list;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        DatabaseGPS database = arrayList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_gps_contents, null);
        }

        TextView textViewLocationName = (TextView) convertView.findViewById(R.id.TableRowLocationName);
        textViewLocationName.setText(database.getLocationName());

        TextView textViewLocation = (TextView) convertView.findViewById(R.id.TableRowLocation);
        textViewLocation.setText(database.getLocation());

        return convertView;
    }
}