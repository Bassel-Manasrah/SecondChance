package com.basselm_lailam_mohammedb.secondchance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CreateListingActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    // UI components
    Button btn_create, btn_gallery, btn_camera;
    TextInputEditText et_name, et_desc, et_phone, et_price;
    LinearLayout container;
    ProgressBar progressbar;
    Uri imgUri;
    TextView tv_upload_photo;
    ActivityResultLauncher<PickVisualMediaRequest> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        // Change the title of the toolbar and allow back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Listing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        btn_create = findViewById(R.id.btn_create);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        et_name = findViewById(R.id.et_name);
        et_desc = findViewById(R.id.et_desc);
        et_phone = findViewById(R.id.et_phone);
        et_price = findViewById(R.id.et_price);
        tv_upload_photo = findViewById(R.id.tv_upload_photo);
        container = findViewById(R.id.container);
        progressbar = findViewById(R.id.progressbar);

        // Set click listeners
        btn_create.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);

        // Set text change listeners for input fields
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

        // Set focus change listeners for input fields
        et_name.setOnFocusChangeListener(this);
        et_price.setOnFocusChangeListener(this);
        et_phone.setOnFocusChangeListener(this);
        et_desc.setOnFocusChangeListener(this);

        // Retrieve image URI if passed from the previous activity
        String imageUriString = getIntent().getStringExtra("imageUriString");
        if (imageUriString != null) {
            Log.d("mlog", "detected");
            Uri uri = Uri.parse(imageUriString);
            Log.d("mlog", uri.getPath());
            setImgUri(uri);
        }

        // Handle the state of the create button
        handleCreateButton();
        setupGalleryLauncher();
    }

    // Setup gallery launcher for picking images
    public void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                setImgUri(uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });
    }

    // Launch the gallery for picking images
    public void launchGallery() {
        galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    // Set the image URI and update the UI feedback
    public void setImgUri(Uri uri) {
        imgUri = uri;
        photoUploadedUIFeedback();
    }

    // Upload the item details to Firestore
    private void uploadItem(Map<String, Object> item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent = new Intent(CreateListingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Create a new listing
    public void createListing() {
        loadingStateUIFeedback();

        String name = et_name.getText().toString().trim();
        String desc = et_desc.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        double price = Double.parseDouble(et_price.getText().toString());

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("desc", desc);
        item.put("phone", phone);
        item.put("price", price);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (imgUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
            StorageReference imgRef = storageRef.child(String.valueOf(System.currentTimeMillis()));

            imgRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.put("imgUrl", uri.toString());
                            uploadItem(item);
                        }
                    });
                }
            });
        } else {
            item.put("imgUrl", "");
            uploadItem(item);
        }
    }

    // Provide feedback for loading state
    private void loadingStateUIFeedback() {
        container.setAlpha(0.25F);
        progressbar.setVisibility(View.VISIBLE);
    }

    // Handle click events
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_create) {
            createListing();
        }

        if (view.getId() == R.id.btn_camera) {
            launchCamera();
        }

        if (view.getId() == R.id.btn_gallery) {
            launchGallery();
        }
    }

    // Launch the camera activity
    public void launchCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    // Enable or disable the create button based on input field states
    public void handleCreateButton() {
        String text1 = et_name.getText().toString().trim();
        String text2 = et_phone.getText().toString().trim();
        String text3 = et_desc.getText().toString().trim();
        String text4 = et_price.getText().toString().trim();

        boolean enabled = !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty();
        btn_create.setEnabled(enabled);

        if (enabled) {
            btn_create.getBackground().setColorFilter(null);
        } else {
            btn_create.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    // Handle focus change events
    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        TextInputEditText et = (TextInputEditText) view;

        if (!hasFocus && et.getText().toString().isEmpty()) {
            et.setError("Field cannot be empty");
        }
    }

    // Provide feedback when a photo is uploaded
    public void photoUploadedUIFeedback() {
        tv_upload_photo.setTextColor(getResources().getColor(R.color.green));
        tv_upload_photo.setText("Photo uploaded!");
        btn_gallery.setVisibility(View.INVISIBLE);
        btn_camera.setVisibility(View.INVISIBLE);
    }

    // Save the state of input fields when the activity stops
    @Override
    protected void onStop() {
        super.onStop();

        String name = et_name.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String desc = et_desc.getText().toString().trim();
        String price = et_price.getText().toString().trim();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("desc", desc);
        editor.putString("price", price);
        editor.apply();
    }

    // Restore the state of input fields when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String name = sharedPrefs.getString("name", "");
        String phone = sharedPrefs.getString("phone", "");
        String desc = sharedPrefs.getString("desc", "");
        String price = sharedPrefs.getString("price", "");

        et_name.setText(name);
        et_phone.setText(phone);
        et_desc.setText(desc);
        et_price.setText(price);
    }
}
















