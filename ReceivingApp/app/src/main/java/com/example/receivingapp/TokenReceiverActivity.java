package com.example.receivingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class TokenReceiverActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://84.247.132.220:8000/";
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null && "koko".equals(data.getScheme()) && "auth".equals(data.getHost())) {
            String token = data.getQueryParameter("token");
            if (token != null) {
                saveToken(token);
                fetchUserData(token);
            } else {
                showError("No token received");
                finish();
            }
        } else {
            showError("Invalid deep link");
            finish();
        }
    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }

    private void fetchUserData(String token) {
        try {
            JSONObject json = new JSONObject();
            json.put("token", token);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "profile.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        showError("Network error: " + e.getMessage());
                        finish();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);

                        if (response.isSuccessful()) {
                            JSONObject user = jsonResponse.getJSONObject("user");
                            String userId = user.getString("id");
                            String username = user.getString("username");
                            String email = user.optString("email", "");

                            saveUserData(userId, username, email);

                            runOnUiThread(() -> {
                                Toast.makeText(TokenReceiverActivity.this,
                                        "Welcome " + username, Toast.LENGTH_SHORT).show();
                                navigateToMainActivity();
                            });
                        } else {
                            String errorMsg = jsonResponse.optString("message", "Request failed");
                            runOnUiThread(() -> {
                                showError(errorMsg);
                                finish();
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            showError("Error processing data");
                            finish();
                        });
                    }
                }
            });
        } catch (JSONException e) {
            showError("Error creating request");
            finish();
        }
    }

    private void saveUserData(String userId, String username, String email) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit()
                .putString("user_id", userId)
                .putString("username", username)
                .putString("email", email)
                .apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}