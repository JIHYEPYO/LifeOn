package com.example.ncs.lifeon.Activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.ncs.lifeon.Fragment.CalendarFragment;
import com.example.ncs.lifeon.Fragment.ExerciseFragment;
import com.example.ncs.lifeon.Fragment.MapFragment;
import com.example.ncs.lifeon.Fragment.PedometerFragment;
import com.example.ncs.lifeon.Fragment.RegisterFragment;
import com.example.ncs.lifeon.Fragment.SettingFragment;
import com.example.ncs.lifeon.R;

import static com.example.ncs.lifeon.Const.email;
import static com.example.ncs.lifeon.Const.name;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.ncs.lifeon.R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(com.example.ncs.lifeon.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.example.ncs.lifeon.R.string.navigation_drawer_open, com.example.ncs.lifeon.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(com.example.ncs.lifeon.R.id.nav_view);
        View viewNavigation = navigationView.getHeaderView(0);
        TextView textViewNavName = (TextView) viewNavigation.findViewById(R.id.textViewNavName);
        TextView textViewNavEmail = (TextView) viewNavigation.findViewById(R.id.textViewNavEmail);
        textViewNavName.setText(name);
        textViewNavEmail.setText(email);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.example.ncs.lifeon.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_main) {
            fragment = new PedometerFragment();
            title = "Pedometer";
        } else if (id == R.id.nav_exercise) {
            fragment = new ExerciseFragment();
            title = "Exercise";
        } else if (id == R.id.nav_calendar) {
            fragment = new CalendarFragment();
            title = "Calendar";
        } else if (id == R.id.nav_map) {
            fragment = new MapFragment();
            title = "Map";
        } else if (id == R.id.nav_register) {
            fragment = new RegisterFragment();
            title = "Register";
        } else if (id == R.id.nav_setting) {
            fragment = new SettingFragment();
            title = "Setting";
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_main, fragment);
            fragmentTransaction.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
