package com.basselm_lailam_mohammedb.secondchance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    // View for displaying the camera preview
    private PreviewView previewView;
    // Button for capturing the image
    private Button captureButton;
    // Controller for handling camera operations
    private LifecycleCameraController cameraController;
    // Flag to indicate if the image should be rotated
    private boolean shouldRotate;

    // Required permissions for the camera
    private static final String[] REQUIRED_PERMISSIONS = { Manifest.permission.CAMERA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize the preview view and capture button
        this.previewView = findViewById(R.id.previewView);
        this.captureButton = findViewById(R.id.captureButton);

        // Check if all permissions are granted, if so start the camera, otherwise request permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 0);
        }
    }

    // Method to rotate a bitmap by a specified angle
    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // Method to start the camera
    private void startCamera() {
        // Initialize the camera controller
        LifecycleCameraController cameraController = new LifecycleCameraController(this);
        // Bind the camera controller to the lifecycle of this activity
        cameraController.bindToLifecycle(this);
        // Set the camera to use the back camera by default
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);

        // Set the preview view to use the camera controller
        this.previewView.setController(cameraController);
        // Set the click listener for the capture button
        this.captureButton.setOnClickListener(this);
        // Assign the camera controller to the instance variable
        this.cameraController = cameraController;
    }

    // Method to check if all required permissions are granted
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Method to save a bitmap as a JPEG file and return its URI
    public Uri saveBitmap(Bitmap bmp) {
        // Create a new file in the external files directory with a unique name
        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + "");

        try {
            // Open a file output stream and compress the bitmap to JPEG format
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // Return the URI of the saved file
        return Uri.fromFile(file);
    }

    // Method to check if the device is in portrait mode
    public boolean isPortraitMode() {
        Configuration configuration = getResources().getConfiguration();
        int orientation = configuration.orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void onClick(View view) {
        // Capture an image using the camera controller
        this.cameraController.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);

                // Convert the captured image to a bitmap
                Bitmap bmp = image.toBitmap();

                // Rotate the bitmap if the device is in portrait mode
                if (isPortraitMode())
                    bmp = rotateBitmap(bmp, 90);

                // Save the bitmap and get its URI
                Uri uri = saveBitmap(bmp);

                // Start the CreateListingActivity and pass the image URI
                Intent intent = new Intent(CameraActivity.this, CreateListingActivity.class);
                intent.putExtra("imageUriString", uri.toString());
                startActivity(intent);
            }
        });
    }
}
