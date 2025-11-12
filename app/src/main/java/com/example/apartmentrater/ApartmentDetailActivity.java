package com.example.apartmentrater;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;


public class ApartmentDetailActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private EditText reviewEditText;
    private TextView savedReviewTextView;
    private String apartmentName;
    private String apartmentAddress;
    private String reviewKey;
    private ImageButton favoriteButton;
    private Set<String> favorites;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_detail);

        TextView apartmentNameTextView = findViewById(R.id.tv_apartment_name);
        TextView apartmentAddressTextView = findViewById(R.id.tv_apartment_address);
        reviewEditText = findViewById(R.id.et_review);
        savedReviewTextView = findViewById(R.id.tv_saved_review_display);
        Button saveReviewButton = findViewById(R.id.btn_save_review);
        favoriteButton = findViewById(R.id.btn_favorite);
        Button backButton = findViewById(R.id.btn_back_to_list);

        apartmentName = getIntent().getStringExtra("APARTMENT_NAME");
        apartmentAddress = getIntent().getStringExtra("APARTMENT_ADDRESS");

        apartmentNameTextView.setText(apartmentName);
        apartmentAddressTextView.setText(apartmentAddress);

        prefs = getSharedPreferences("ApartmentRatingsPrefs", MODE_PRIVATE);
        reviewKey = "review_" + apartmentName;

        loadReview();

        favorites = new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));
        updateFavoriteButtonIcon();

        saveReviewButton.setOnClickListener(v -> saveReview());
        favoriteButton.setOnClickListener(v -> toggleFavoriteStatus());
        backButton.setOnClickListener(v -> finish());
    }

    private void toggleFavoriteStatus() {
        if (favorites.contains(apartmentName)) {
            favorites.remove(apartmentName);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(apartmentName);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        prefs.edit().putStringSet("favorites", favorites).apply();
        updateFavoriteButtonIcon();
    }

    private void updateFavoriteButtonIcon() {
        if (favorites.contains(apartmentName)) {
            favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_favorite_placeholder);
        }
    }

    private void loadReview() {
        String savedReview = prefs.getString(reviewKey, "");
        savedReviewTextView.setText(savedReview);
    }

    private void saveReview() {
        SharedPreferences.Editor editor = prefs.edit();
        String reviewText = reviewEditText.getText().toString();
        editor.putString(reviewKey, reviewText);
        editor.apply();
        savedReviewTextView.setText(reviewText);
        reviewEditText.setText("");
        Toast.makeText(this, "Review saved!", Toast.LENGTH_SHORT).show();

        String apartmentId = getIntent().getStringExtra("placeId");


        String token = "Token 3fd4df3cd2917a51144c81c9f3e8ded35b1ad677";  // your real token
        String comment = reviewText.trim();

        Map<String, String> body = new HashMap<>();
        body.put("comment", comment);
        body.put("name", apartmentName);

        ApiService apiService = ApiClient.getService();
        Call<Void> call = apiService.postReview(apartmentId, token, body);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ApartmentDetailActivity.this, "Review uploaded to server!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApartmentDetailActivity.this, "Server rejected: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ApartmentDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}