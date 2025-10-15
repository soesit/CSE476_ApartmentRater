package com.example.apartmentrater;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    // A key to identify our saved data
    private static final String KEY_SWITCH_STATE = "switchState";
    private SwitchMaterial notificationsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        notificationsSwitch = findViewById(R.id.switch_notifications);

        // Check if we have a previously saved state
        if (savedInstanceState != null) {
            boolean switchState = savedInstanceState.getBoolean(KEY_SWITCH_STATE);
            notificationsSwitch.setChecked(switchState);
        }
    }

    // This method is called by the system before the activity is destroyed
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current state of the switch
        outState.putBoolean(KEY_SWITCH_STATE, notificationsSwitch.isChecked());
    }
}