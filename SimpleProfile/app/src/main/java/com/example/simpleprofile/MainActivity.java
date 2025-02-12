package com.example.simpleprofile;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView profileUsername, profileFirstname, profileLastname, profileEmail;
    Button logoutButton;
    DatabaseHelper databaseHelper;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileUsername = findViewById(R.id.profile_username);
        profileFirstname = findViewById(R.id.profile_firstname);
        profileLastname = findViewById(R.id.profile_lastname);
        profileEmail = findViewById(R.id.profile_email);
        logoutButton = findViewById(R.id.logout_button);
        databaseHelper = new DatabaseHelper(this);

        userEmail = getIntent().getStringExtra("EMAIL");

        if (userEmail != null) {
            loadUserProfile(userEmail);
        } else {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show();
        }

        logoutButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserProfile(String email) {
        Cursor cursor = databaseHelper.getUser(email);
        if (cursor.moveToFirst()) {
            profileUsername.setText(cursor.getString(1));
            profileFirstname.setText(cursor.getString(2));
            profileLastname.setText(cursor.getString(3));
            profileEmail.setText(email);
        } else {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}
