package com.example.telefixmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.telefixmain.Fragment.HistoryFragment;
import com.example.telefixmain.Fragment.HomeFragment;
import com.example.telefixmain.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView botNav;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding with xml
        botNav = findViewById(R.id.bottom_nvg);

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.rl_main,
                new HomeFragment()).commit();
        botNav.setSelectedItemId(R.id.nav_home);

        // set listener for tabs changes
        botNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragmentContainer = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragmentContainer = new HomeFragment();
                    break;

                case R.id.nav_history:
                    fragmentContainer = new HistoryFragment();
                    break;

                case R.id.nav_profile:
                    fragmentContainer = new ProfileFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.rl_main,
                    Objects.requireNonNull(fragmentContainer)).commit();

            return true;
        });
    }
}