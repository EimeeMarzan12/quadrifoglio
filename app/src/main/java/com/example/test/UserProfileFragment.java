package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

public class UserProfileFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Find the Sign Out button
        RelativeLayout signOutButton = view.findViewById(R.id.user_signout_layout);  // 'exit' is the ID of your RelativeLayout

        // Set an OnClickListener for the Sign Out button
        signOutButton.setOnClickListener(v -> {
            // Create an intent to navigate to the Login activity
            Intent intent = new Intent(getActivity(), Login.class);  // Replace 'Login' with the actual login activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();  // Optional: Close the current fragment/activity to prevent back navigation
        });

        return view;
    }
}
