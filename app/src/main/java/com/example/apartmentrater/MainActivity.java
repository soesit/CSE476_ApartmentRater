package com.example.apartmentrater;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLocationResult;
    private Button btnGetLocation;

    // Runtime permission launcher
    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvLocationResult = findViewById(R.id.tv_location_result);
        btnGetLocation = findViewById(R.id.btn_get_location);

        btnGetLocation.setOnClickListener(v -> checkPermissionAndFetchLocation());

        // Apartment cards
        setupApartmentCards();
        // Bottom nav bar
        setupBottomNavigation();
    }

    /** Check for permission before accessing location. */
    private void checkPermissionAndFetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            // Ask user for permission
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /** Fetches the current location using high-accuracy mode. */
    private void fetchCurrentLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        reverseGeocodeAndDisplay(location);
                    } else {
                        Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Location error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    /** Converts GPS coordinates into City, State, and updates the UI. */
    private void reverseGeocodeAndDisplay(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> results = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (results != null && !results.isEmpty()) {
                Address addr = results.get(0);
                String city = addr.getLocality();
                String state = addr.getAdminArea(); // Usually already abbreviated ("MI", "CA", etc.)

                if (city != null && state != null) {
                    String display = "Results for " + city + ", " + state;
                    tvLocationResult.setText(display);
                    tvLocationResult.setVisibility(View.VISIBLE);
                } else {
                    tvLocationResult.setText("Location: " +
                            location.getLatitude() + ", " + location.getLongitude());
                }
            } else {
                tvLocationResult.setText("No address found for this location.");
            }
        } catch (IOException e) {
            tvLocationResult.setText("Geocoder failed: " + e.getMessage());
        }
    }

    /** Sets up navigation between apartment detail screens. */
    private void setupApartmentCards() {
        CardView apartment1 = findViewById(R.id.card_apartment_1);
        CardView apartment2 = findViewById(R.id.card_apartment_2);
        CardView apartment3 = findViewById(R.id.card_apartment_3);

        apartment1.setOnClickListener(v -> openDetails("Gaslight Village"));
        apartment2.setOnClickListener(v -> openDetails("Willoughby Estates"));
        apartment3.setOnClickListener(v -> openDetails("Landmark Apartments"));
    }

    private void openDetails(String apartmentName) {
        Intent intent = new Intent(MainActivity.this, ApartmentDetailActivity.class);
        intent.putExtra("APARTMENT_NAME", apartmentName);
        startActivity(intent);
    }

    /** Handles bottom navigation. */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }
            return false;
        });
    }
}