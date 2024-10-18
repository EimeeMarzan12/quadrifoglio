package com.example.test;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private ImageView log_btn;
    private ImageView dashboard_ic;
    private TextView dashboard_text;
    private TextView fragment_title;
    private ImageView profile_ic;
    private TextView profile_text;
    private ImageView inventory_ic;
    private TextView inventory_text;
    private RelativeLayout dashboard_btn;
    private RelativeLayout inventory_btn;
    private RelativeLayout profile_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log_btn = findViewById(R.id.logInventory_btn);
        fragment_title = findViewById(R.id.fragment_title);
        dashboard_ic = findViewById(R.id.block_dashboard_ic);
        dashboard_text = findViewById(R.id.block_dashboard_text);
        profile_ic = findViewById(R.id.block_profile_ic);
        profile_text = findViewById(R.id.block_profile_text);
        inventory_ic = findViewById(R.id.block_inventory_ic);
        inventory_text = findViewById(R.id.block_inventory_text);
        dashboard_btn = findViewById(R.id.block_dashboard);
        inventory_btn = findViewById(R.id.block_inventory);
        profile_btn = findViewById(R.id.block_profile);
        useFragment(new DashboardFragment(), R.id.main_fragment);

        log_btn.setClickable(true);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });

        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard_ic.setImageResource(R.drawable.ic_home_grayo);
                dashboard_text.setTextColor(Color.parseColor("#4D4D4D"));


                profile_ic.setImageResource(R.drawable.ic_profile_grayo);
                profile_text.setTextColor(Color.parseColor("#4D4D4D"));


                inventory_ic.setImageResource(R.drawable.ic_inventory_grayo);
                inventory_text.setTextColor(Color.parseColor("#4D4D4D"));

                fragment_title.setText("Scanning");

                useFragment(new CameraFragment(), R.id.main_fragment);
            }
        });

        dashboard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard_ic.setImageResource(R.drawable.ic_home_bluef);
                dashboard_text.setTextColor(Color.parseColor("#5075E8"));


                profile_ic.setImageResource(R.drawable.ic_profile_grayo);
                profile_text.setTextColor(Color.parseColor("#4D4D4D"));


                inventory_ic.setImageResource(R.drawable.ic_inventory_grayo);
                inventory_text.setTextColor(Color.parseColor("#4D4D4D"));

                fragment_title.setText("Dashboard");

                useFragment(new DashboardFragment(), R.id.main_fragment);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard_ic.setImageResource(R.drawable.ic_home_grayo);
                dashboard_text.setTextColor(Color.parseColor("#4D4D4D"));


                profile_ic.setImageResource(R.drawable.ic_profile_bluef);
                profile_text.setTextColor(Color.parseColor("#5075E8"));


                inventory_ic.setImageResource(R.drawable.ic_inventory_grayo);
                inventory_text.setTextColor(Color.parseColor("#4D4D4D"));



                useFragment(new UserProfileFragment(),  R.id.main_fragment);
            }
        });

        inventory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboard_ic.setImageResource(R.drawable.ic_home_grayo);
                dashboard_text.setTextColor(Color.parseColor("#4D4D4D"));


                profile_ic.setImageResource(R.drawable.ic_profile_grayo);
                profile_text.setTextColor(Color.parseColor("#4D4D4D"));


                inventory_ic.setImageResource(R.drawable.ic_inventory_bluef);
                inventory_text.setTextColor(Color.parseColor("#5075E8"));

                useFragment(new EditInventoryFragment(), R.id.main_fragment);
            }
        });
    }

    public void useFragment(Fragment fragment, int fragId){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragId, fragment);
        fragmentTransaction.commit();
    }
}