package com.jantomassi.newcccradioapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.setContentView(R.layout.activity_main);
        navSetup();

        Intent intent = new Intent(getApplicationContext(), MediaService.class);
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        startService(intent);
    }

    private void navSetup() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_speaker)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(navView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}