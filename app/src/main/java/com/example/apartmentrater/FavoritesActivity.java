package com.example.apartmentrater;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashSet;
import java.util.Set;

public class FavoritesActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        TextView favoritesListTextView = findViewById(R.id.tv_favorites_list);

        // Load the set of favorite apartment names
        SharedPreferences prefs = getSharedPreferences("ApartmentRatingsPrefs", MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet("favorites", new HashSet<>());

        if (favorites.isEmpty()) {
            favoritesListTextView.setText("You have no favorite apartments yet.");
        } else {
            // Build a string to display the list
            StringBuilder favoritesText = new StringBuilder();
            for (String apartment : favorites) {
                favoritesText.append("- ").append(apartment).append("\n\n");
            }
            favoritesListTextView.setText(favoritesText.toString());
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_favorites); // We are in Favorites, so select it

        bottomNav.setOnItemSelectedListener(item -> {
            // Check which item was clicked
            if (item.getItemId() == R.id.nav_home) {
                // User clicked Home, so start that activity
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            // If Favorites is clicked, do nothing since we are already here
            return false;
        });
    }
    }
