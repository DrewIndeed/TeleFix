package com.example.telefixmain.Activity.Customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.telefixmain.Fragment.HistoryFragment;
import com.example.telefixmain.Fragment.HomeFragment;
import com.example.telefixmain.Fragment.ProfileFragment;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView botNav;
    RelativeLayout rlMain;

    // keep track of fragments orders
    int prevFragment = 1;
    int currentFragment;

    // intent data receivers
    User userTracker;
    ArrayList<HashMap<String, String>> vehiclesHashMapList = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get data from intent sent from Login Activity
        Intent intent = getIntent();
        userTracker = (User) intent.getSerializableExtra("loggedInUser");
        vehiclesHashMapList = (ArrayList<HashMap<String, String>>)
                intent.getSerializableExtra("vehiclesHashMapList");

        // main content fade in
        rlMain = findViewById(R.id.rl_main);
        rlMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // bottom navigation - binding with xml
        botNav = findViewById(R.id.bottom_nvg);

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.rl_main,
                new HomeFragment(userTracker, vehiclesHashMapList)).commit();
        botNav.setSelectedItemId(R.id.nav_home);

        // on nav bar item selected listener
        botNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragmentContainer = null;
            prevFragment = botNav.getSelectedItemId();
            currentFragment = item.getItemId();

            // return fragment instances according to item's id
            switch (item.getItemId()) {
                case R.id.nav_home:
                    fragmentContainer = new HomeFragment(userTracker, vehiclesHashMapList);
                    break;

                case R.id.nav_history:
                    fragmentContainer = new HistoryFragment();
                    break;

                case R.id.nav_profile:
                    fragmentContainer = new ProfileFragment();
                    break;
            }

            // generate animations according to fragment's order
            if (prevFragment < currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right
                        )
                        .replace(R.id.rl_main, Objects.requireNonNull(fragmentContainer))
                        .commit();
            } else if (prevFragment > currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.enter_from_left,
                                R.anim.exit_to_right,
                                R.anim.enter_from_right,
                                R.anim.exit_to_left
                        )
                        .replace(R.id.rl_main, Objects.requireNonNull(fragmentContainer))
                        .commit();
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        // make it empty to prevent going back using the device's "Back" button
    }
}