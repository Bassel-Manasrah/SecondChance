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

        // change the title of the toolbar and allow back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create Listing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        btn_create.setOnClickListener(this);
        btn_camera.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);

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

        String imageUriString = getIntent().getStringExtra("imageUriString");
        if (imageUriString != null) {
            Log.d("mlog", "detected");
            Uri uri = Uri.parse(imageUriString);
            Log.d("mlog", uri.getPath());
            setImgUri(uri);
        }


        handleCreateButton();
        setupGalleryLauncher();
    }

    public void setupGalleryLauncher() {
        galleryLauncher =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        setImgUri(uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    public void launchGallery() {
        galleryLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void setImgUri(Uri uri) {
        imgUri = uri;
        photoUploadedUIFeedback();
    }

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

        if(imgUri != null) {

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
        }

        else {
            item.put("imgUrl", "");
            uploadItem(item);
        }



//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("items")
//                .add(item)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        String id = documentReference.getId();
//                        if(imgUri != null) {
//
//                            // upload image to firebase storage
//                            FirebaseStorage storage = FirebaseStorage.getInstance();
//                            StorageReference storageRef = storage.getReference().child("images/" + id);
//                            storageRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    Intent intent = new Intent(CreateListingActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                }
//                            });
//                        }
//
//                    }
//                });
    }

    private void loadingStateUIFeedback() {
        container.setAlpha(0.25F);
        progressbar.setVisibility(View.VISIBLE);
    }


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

    public void launchCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void handleCreateButton() {
        String text1 = et_name.getText().toString().trim();
        String text2 = et_phone.getText().toString().trim();
        String text3 = et_desc.getText().toString().trim();
        String text4 = et_price.getText().toString().trim();


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

    public void photoUploadedUIFeedback() {
        tv_upload_photo.setTextColor(getResources().getColor(R.color.green));
        tv_upload_photo.setText("Photo uploaded!");
        btn_gallery.setVisibility(View.INVISIBLE);
        btn_camera.setVisibility(View.INVISIBLE);
    }

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
















