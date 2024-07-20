package com.basselm_lailam_mohammedb.secondchance;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private EditText edt_min;
    private EditText edt_max;
    private Switch switch_only_with_image;

    private int setting_minPrice, setting_maxPrice;
    private boolean setting_onlyWithImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // change the title of the toolbar and allow back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edt_min = findViewById(R.id.edt_min);
        edt_max = findViewById(R.id.edt_max);
        switch_only_with_image = findViewById(R.id.switch_only_with_image);

        loadSettings();
    }

    private int getNumberFromEditable(Editable editable) {
        String cleanString = editable.toString().replaceAll("[^0-9]", "");
        if(cleanString.isEmpty()) return 0;

        int number = Integer.parseInt(cleanString);
        return number;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String edt_min_string = edt_min.getText().toString();
        String edt_max_string = edt_max.getText().toString();

        int minPrice = edt_min_string.isEmpty() ? 0 : Integer.valueOf(edt_min_string);
        int maxPrice = edt_max_string.isEmpty() ? 999999999 : Integer.valueOf(edt_max_string);

        editor.putBoolean("onlyWithImage", switch_only_with_image.isChecked());
        editor.putInt("minPrice", minPrice);
        editor.putInt("maxPrice", maxPrice);

        editor.apply();
    }

    private void loadSettings() {
        SharedPreferences sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int minPrice = sharedPrefs.getInt("minPrice", 0);
        int maxPrice = sharedPrefs.getInt("maxPrice",  999999999);
        boolean onlyWithImage = sharedPrefs.getBoolean("onlyWithImage", false);

        edt_min.setText(String.valueOf(minPrice));
        edt_max.setText(String.valueOf(maxPrice));
        switch_only_with_image.setChecked(onlyWithImage);
    }

}