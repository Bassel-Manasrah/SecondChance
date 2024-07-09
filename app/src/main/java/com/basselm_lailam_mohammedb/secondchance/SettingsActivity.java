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


        initEditText(edt_min, false);
        initEditText(edt_max, true);

    }

    private void initEditText(EditText edt, boolean isMax) {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {

                // update the color of the edit text drawable
                updateEditTextDrawableColor(edt);

//                // remove non numeric characters
//                String cleanString = editable.toString().replaceAll("[^0-9]", "");
//                if(cleanString.isEmpty()) return;
//
//                int number = Integer.parseInt(cleanString);
//                number = Math.min(number, 10000);
                int number = getNumberFromEditable(editable);

                // format the string properly
                String formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(number);
                if(number == 10000 && isMax)
                    formatted = "+" + formatted;

                // update the edit text
                edt.removeTextChangedListener(this);
                edt.setText(formatted);
                edt.setSelection(formatted.length());
                edt.addTextChangedListener(this);

            }
        });
    }

    private void updateEditTextDrawableColor(EditText edt) {

        // Get the current hint text color
        int hintColor = edt.getCurrentHintTextColor();

        // Get the current text color
        int textColor = edt.getCurrentTextColor();

        // Create a ColorStateList from the appropriate color
        ColorStateList colorStateList = ColorStateList.valueOf(
                edt.getText().toString().isEmpty() ? hintColor : textColor
        );

        // Update the color of the drawable
        edt.setCompoundDrawableTintList(colorStateList);
    }

    private int getNumberFromEditable(Editable editable) {
        String cleanString = editable.toString().replaceAll("[^0-9]", "");
        if(cleanString.isEmpty()) return 0;

        int number = Integer.parseInt(cleanString);
        number = Math.min(number, 10000);
        return number;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("mlog", "onPause: settings");

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("onlyWithImage", switch_only_with_image.isChecked());
        editor.putInt("minPrice", getNumberFromEditable(edt_min.getEditableText()));
        editor.putInt("maxPrice", getNumberFromEditable(edt_max.getEditableText()));

        editor.apply();
    }

    private void loadSettings() {
        SharedPreferences sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int minPrice = sharedPrefs.getInt("minPrice", 0);
        int maxPrice = sharedPrefs.getInt("maxPrice",  Integer.MAX_VALUE);
        boolean onlyWithImage = sharedPrefs.getBoolean("onlyWithImage", false);

        edt_min.setText(String.valueOf(minPrice));
        edt_max.setText(String.valueOf(maxPrice));
        switch_only_with_image.setChecked(onlyWithImage);
    }

    private void commitSettings() {

    }
}