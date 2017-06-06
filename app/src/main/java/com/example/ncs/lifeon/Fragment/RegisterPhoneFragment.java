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
import android.widget.EditText;
import android.widget.ListView;

import com.example.ncs.lifeon.ECT.DatabaseGPS;
import com.example.ncs.lifeon.ECT.DatabaseGPSController;
import com.example.ncs.lifeon.ECT.DatabasePhone;
import com.example.ncs.lifeon.ECT.DatabasePhoneController;
import com.example.ncs.lifeon.ECT.ListViewGPSAdapter;
import com.example.ncs.lifeon.ECT.ListViewPhoneAdapter;
import com.example.ncs.lifeon.R;

import java.util.ArrayList;

import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_GPS;
import static com.example.ncs.lifeon.ECT.Const.TABLE_NAME_PHONE;
import static com.example.ncs.lifeon.ECT.Const.name;

/**
 * Created by PYOJIHYE on 2017-06-06.
 */

public class RegisterPhoneFragment extends Fragment {
    private static RegisterPhoneFragment instance = new RegisterPhoneFragment();

    EditText editTextPersonName;
    EditText editTextPersonPhone;
    ListView listView;
    Button buttonRegister;

    SQLiteDatabase db;
    DatabasePhoneController dbController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_phone, container, false);

        editTextPersonName =(EditText)view.findViewById(R.id.editTextPersonName);
        editTextPersonPhone =(EditText)view.findViewById(R.id.editTextPersonPhone);
        listView = (ListView) view.findViewById(R.id.ListViewPhone);
        buttonRegister = (Button)view.findViewById(R.id.buttonPhoneRegister);

        dbController = new DatabasePhoneController(getContext());
        db = dbController.getWritableDatabase();

        final View header = getActivity().getLayoutInflater().inflate(R.layout.listview_phone_header, null, false);
        listView.addHeaderView(header);

        DatabaseReset();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DatabasePhone item = (DatabasePhone) parent.getItemAtPosition(position);
                if (item.getPersonPhone() != null) {
                    db.delete(TABLE_NAME_PHONE, "personPhone = ?", new String[]{String.valueOf(item.getPersonPhone())});
                    DatabaseReset();
                }
                return false;
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String person = editTextPersonName.getText().toString();
                String phone = editTextPersonPhone.getText().toString();

                if(!person.equals("") && !phone.equals(""))
                db.execSQL("INSERT INTO "+TABLE_NAME_PHONE+" VALUES (null, '" + name + "', '" + person + "', '" + phone + "');");

                editTextPersonName.setText("");
                editTextPersonPhone.setText("");
                DatabaseReset();
            }
        });

        return view;
    }

    public void DatabaseReset() {
        try {
            String query = "SELECT * FROM " + TABLE_NAME_PHONE + ";";
            Cursor cursor = db.rawQuery(query, null);
            ArrayList<DatabasePhone> arrayList = new ArrayList<DatabasePhone>();
            arrayList.clear();

            while (cursor.moveToNext()) {
                DatabasePhone database = new DatabasePhone();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String personName = cursor.getString(cursor.getColumnIndex("personName"));
                String personPhone = cursor.getString(cursor.getColumnIndex("personPhone"));

                database.setName(name);
                database.setPersonName(personName);
                database.setPersonPhone(personPhone);

                arrayList.add(database);
            }
            cursor.close();
            ListViewPhoneAdapter listViewAdapter = new ListViewPhoneAdapter(getContext(), arrayList);
            listView.setAdapter(listViewAdapter);
        } catch (Exception e) {

        }
    }

    public static synchronized RegisterPhoneFragment getInstance() {
        return instance;
    }

}
