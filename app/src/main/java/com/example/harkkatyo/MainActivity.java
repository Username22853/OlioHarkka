package com.example.harkkatyo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView; //sidebar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); //internet access
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.layout);
        navigationView = findViewById(R.id.sidebarView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.frlayout, new LoginFragment()).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.openLogin){
            getSupportFragmentManager().beginTransaction().replace(R.id.frlayout, new LoginFragment()).commit();
        } else if (item.getItemId()==R.id.openArchive){
            getSupportFragmentManager().beginTransaction().replace(R.id.frlayout, new ArchiveFragment()).commit();
        } else if (item.getItemId()==R.id.openCalendar){
            getSupportFragmentManager().beginTransaction().replace(R.id.frlayout, new CalendarFragment()).commit();
        }else if (item.getItemId()==R.id.openDailyMovies){
            getSupportFragmentManager().beginTransaction().replace(R.id.frlayout, new DailyMoviesFragment()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public static void hideKeyboard(Activity activity) { //method for all fragments to keep keyboard shut when necessary
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}