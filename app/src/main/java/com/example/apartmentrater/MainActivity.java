package com.example.apartmentrater;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the clickable cards for each apartment
        CardView apartment1 = findViewById(R.id.card_apartment_1);
        CardView apartment2 = findViewById(R.id.card_apartment_2);
        CardView apartment3 = findViewById(R.id.card_apartment_3);

        // Set up click listeners
        apartment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails("Gaslight Village");
            }
        });

        apartment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails("Willoughby Estates");
            }
        });

        apartment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetails("Landmark Apartments");
            }
        });

        setupBottomNavigation();

    }

    /**
     * A helper method to start the ApartmentDetailsActivity.
     * @param apartmentName The name of the apartment to display.
     */
    private void openDetails(String apartmentName) {
        Intent intent = new Intent(MainActivity.this, ApartmentDetailActivity.class);
        // We use putExtra to send data along with the intent
        intent.putExtra("APARTMENT_NAME", apartmentName);

        startActivity(intent);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home); // We are in Home, so select it

        bottomNav.setOnItemSelectedListener(item -> {
            // Check which item was clicked
            if (item.getItemId() == R.id.nav_favorites) {
                // User clicked Favorites, so start that activity
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            }
            else if (item.getItemId() == R.id.nav_map) {
                startActivity(new Intent(this, MapActivity.class));
                return true;
            }
            // If Home is clicked, do nothing since we are already here
            return false;
        });

    }


}


