package com.example.receivingapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String KOKO_AUTH_URL = "koko://auth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login_button);
        TextView userInfo = findViewById(R.id.user_info);

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null) {
            String email = prefs.getString("email", "");
            userInfo.setText("Logged in as: " + username + "\nEmail: " + email);
            loginButton.setText("Logout");
            loginButton.setOnClickListener(v -> logout());
        } else {
            loginButton.setOnClickListener(v -> startKokoLogin());
        }
    }

    private void startKokoLogin() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(KOKO_AUTH_URL));
            startActivity(intent);
        } catch (Exception e) {

            Toast.makeText(this, "Please install K0K0_Link app", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().clear().apply();
        recreate();
    }
}