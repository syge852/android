package com.example.registerloginprofile;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Success extends AppCompatActivity {
    private TextView tvUserName, tvUserEmail, tvFName , tvLName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.success);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvFName = findViewById(R.id.tvFName);
        tvLName = findViewById(R.id.tvLName);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USERNAME");
        String userEmail = intent.getStringExtra("EMAIL");
        String fName = intent.getStringExtra("FNAME");
        String lName = intent.getStringExtra("LNAME");

        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);
        tvFName.setText(fName);
        tvLName.setText(lName);
    }

    public void logout(View view) {
        Intent intent = new Intent(Success.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
