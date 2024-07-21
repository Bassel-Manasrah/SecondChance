package com.basselm_lailam_mohammedb.secondchance;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    // EditText for minimum value input
    private EditText edt_min;
    // EditText for maximum value input
    private EditText edt_max;
    // Switch for toggling the "only with image" option
    private Switch switch_only_with_image;
    // TextView for error
    private TextView tv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Change the title of the toolbar and allow back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        tv_error = findViewById(R.id.tv_error);
        edt_min = findViewById(R.id.edt_min);
        edt_max = findViewById(R.id.edt_max);
        switch_only_with_image = findViewById(R.id.switch_only_with_image);

        initEditText(edt_max);
        initEditText(edt_min);

        // Load settings from shared preferences
        loadSettings();
    }

    private void initEditText(EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                tv_error.setVisibility(isInputValid() ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    private boolean isInputValid() {
        // Get the text from the EditTexts
        String edt_min_string = edt_min.getText().toString();
        String edt_max_string = edt_max.getText().toString();

        // Parse the text to integers with default values if empty
        int minPrice = edt_min_string.isEmpty() ? 0 : Integer.valueOf(edt_min_string);
        int maxPrice = edt_max_string.isEmpty() ? 999999999 : Integer.valueOf(edt_max_string);

        return minPrice <= maxPrice;
    }

    // Method to extract a number from an Editable object
    private int getNumberFromEditable(Editable editable) {
        // Remove all non-numeric characters from the string
        String cleanString = editable.toString().replaceAll("[^0-9]", "");
        if(cleanString.isEmpty()) return 0;

        // Parse the cleaned string into an integer
        int number = Integer.parseInt(cleanString);
        return number;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save the values in shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the text from the EditTexts
        String edt_min_string = edt_min.getText().toString();
        String edt_max_string = edt_max.getText().toString();

        // Parse the text to integers with default values if empty
        int minPrice = edt_min_string.isEmpty() ? 0 : Integer.valueOf(edt_min_string);
        int maxPrice = edt_max_string.isEmpty() ? 999999999 : Integer.valueOf(edt_max_string);

        // Save the values to shared preferences
        editor.putBoolean("onlyWithImage", switch_only_with_image.isChecked());
        editor.putInt("minPrice", minPrice);
        editor.putInt("maxPrice", maxPrice);

        // Apply the changes
        editor.apply();
    }

    // Method to load settings from shared preferences
    private void loadSettings() {
        SharedPreferences sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int minPrice = sharedPrefs.getInt("minPrice", 0);
        int maxPrice = sharedPrefs.getInt("maxPrice",  999999999);
        boolean onlyWithImage = sharedPrefs.getBoolean("onlyWithImage", false);

        // Set the values to the views
        edt_min.setText(String.valueOf(minPrice));
        edt_max.setText(String.valueOf(maxPrice));
        switch_only_with_image.setChecked(onlyWithImage);
    }

}
