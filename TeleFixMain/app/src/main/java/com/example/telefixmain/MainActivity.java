package com.example.telefixmain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.telefixmain.Fragment.HistoryFragment;
import com.example.telefixmain.Fragment.HomeFragment;
import com.example.telefixmain.Fragment.ProfileFragment;
import com.example.telefixmain.Model.User;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView botNav;
    RelativeLayout rlMain;

    TextView userName;

    // keep track of fragments orders
    int prevFragment = 1;
    int currentFragment;

    // Get current user
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Global Arraylist to store result
    private ArrayList<User> userResult = new ArrayList<>();


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.tv_name_home);

        if (mUser != null) {
            DatabaseHandler.getSingleUser(db,MainActivity.this, mUser.getUid(), userResult);
            new Handler().postDelayed(() -> {
//                userName.setText(userResult.get(0).getName());
                System.out.println(userResult.get(0).getName());
            }, 2000);
        }

        // main content fade in
        rlMain = findViewById(R.id.rl_main);
        rlMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        // bottom navigation - binding with xml
        botNav = findViewById(R.id.bottom_nvg);

        // set default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.rl_main,
                new HomeFragment()).commit();
        botNav.setSelectedItemId(R.id.nav_home);

        // on nav bar item selected listener
        botNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragmentContainer = null;
            prevFragment = botNav.getSelectedItemId();
            currentFragment = item.getItemId();

            // return fragment instances according to item's id
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