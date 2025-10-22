package com.example.apartmentrater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ApartmentDetailActivity extends AppCompatActivity {

    // ===== GPS SENSOR VARIABLES =====
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;

    // ===== EXISTING VARIABLES =====
    private SharedPreferences prefs;
    private EditText reviewEditText;
    private TextView savedReviewTextView;
    private String apartmentName;
    private String reviewKey;
    private ImageButton favoriteButton;
    private Set<String> favorites;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment_detail);

        // ---------- FIND VIEWS ----------
        TextView apartmentNameTextView = findViewById(R.id.tv_apartment_name);
        TextView apartmentAddressTextView = findViewById(R.id.tv_apartment_address);
        reviewEditText = findViewById(R.id.et_review);
        savedReviewTextView = findViewById(R.id.tv_saved_review_display);
        Button saveReviewButton = findViewById(R.id.btn_save_review);
        favoriteButton = findViewById(R.id.btn_favorite);
        Button backButton = findViewById(R.id.btn_back_to_list);
        locationTextView = findViewById(R.id.tv_location); // new location display view

        // ---------- SHARED PREFERENCES ----------
        apartmentName = getIntent().getStringExtra("APARTMENT_NAME");
        apartmentNameTextView.setText(apartmentName);
        prefs = getSharedPreferences("ApartmentRatingsPrefs", MODE_PRIVATE);
        reviewKey = "review_" + apartmentName;

        // ---------- LOAD EXISTING DATA ----------
        loadReview();
        favorites = new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));
        updateFavoriteButtonIcon();

        // ---------- SET ADDRESSES ----------
        if ("Gaslight Village".equals(apartmentName)) {
            apartmentAddressTextView.setText("123 Oak St, East Lansing, MI");
        } else if ("Willoughby Estates".equals(apartmentName)) {
            apartmentAddressTextView.setText("456 Willow Ln, East Lansing, MI");
        } else {
            apartmentAddressTextView.setText("789 Grand River Ave, East Lansing, MI");
        }

        // ---------- BUTTON LISTENERS ----------
        saveReviewButton.setOnClickListener(v -> saveReview());
        favoriteButton.setOnClickListener(v -> toggleFavoriteStatus());
        backButton.setOnClickListener(v -> finish());

        // ---------- GPS SENSOR INTEGRATION ----------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermissionAndFetch();
    }

    // ===== LOCATION PERMISSION & FETCHING =====
    private void requestLocationPermissionAndFetch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            fetchLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        // Always request a fresh, high-accuracy location
        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                String locationText = String.format(Locale.US,
                        "Lat: %.4f, Lon: %.4f",
                        location.getLatitude(),
                        location.getLongitude());
                locationTextView.setText(locationText);
            } else {
                locationTextView.setText("Unable to get location (no GPS fix)");
            }
        }).addOnFailureListener(e -> {
            locationTextView.setText("Error fetching location: " + e.getMessage());
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                locationTextView.setText("Permission denied: GPS unavailable");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-fetch the latest GPS data every time the activity becomes visible again
        fetchLocation();
    }

    // ===== FAVORITE TOGGLE =====
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

    // ===== REVIEW LOGIC WITH LOCATION STORAGE =====
    private void loadReview() {
        String savedReview = prefs.getString(reviewKey, "");
        String savedLocation = prefs.getString(reviewKey + "_location", "");
        if (!savedLocation.isEmpty()) {
            savedReviewTextView.setText(savedReview + "\n\n📍 Last known location: " + savedLocation);
        } else {
            savedReviewTextView.setText(savedReview);
        }
    }

    private void saveReview() {
        SharedPreferences.Editor editor = prefs.edit();
        String reviewText = reviewEditText.getText().toString();
        String currentLocation = locationTextView.getText().toString();

        editor.putString(reviewKey, reviewText);
        editor.putString(reviewKey + "_location", currentLocation);
        editor.apply();

        savedReviewTextView.setText(reviewText + "\n\n📍 Saved from: " + currentLocation);
        reviewEditText.setText("");
        Toast.makeText(this, "Review & location saved!", Toast.LENGTH_SHORT).show();
    }
}
