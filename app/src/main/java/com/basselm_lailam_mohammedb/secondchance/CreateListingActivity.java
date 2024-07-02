package com.basselm_lailam_mohammedb.secondchance;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

public class CreateListingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {


    Button btn_create;
    TextInputEditText et_name, et_desc, et_phone, et_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        // change the title of the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Listing");
        }

        btn_create = findViewById(R.id.btn_create);
        et_name = findViewById(R.id.et_name);
        et_desc = findViewById(R.id.et_desc);
        et_phone = findViewById(R.id.et_phone);
        et_price = findViewById(R.id.et_price);

        btn_create.setOnClickListener(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleCreateButton();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        et_name.addTextChangedListener(textWatcher);
        et_price.addTextChangedListener(textWatcher);
        et_phone.addTextChangedListener(textWatcher);
        et_desc.addTextChangedListener(textWatcher);

        et_name.setOnFocusChangeListener(this);
        et_price.setOnFocusChangeListener(this);
        et_phone.setOnFocusChangeListener(this);
        et_desc.setOnFocusChangeListener(this);

        handleCreateButton();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_create) {

        }
    }

    public void handleCreateButton() {
        String text1 = et_name.getText().toString().trim();
        String text2 = et_phone.getText().toString().trim();
        String text3 = et_desc.getText().toString().trim();
        String text4 = et_price.getText().toString().trim();

        Log.d("mlog", "onEditTextUpdate: " + (!text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty()));

        boolean enabled = !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty();
        btn_create.setEnabled(enabled);

        if(enabled) {
            btn_create.getBackground().setColorFilter(null);
        }
        else {
            btn_create.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        TextInputEditText et = (TextInputEditText) view;

        if (!hasFocus && et.getText().toString().isEmpty()) {
            et.setError("Field cannot be empty");
        }
    }
}
















