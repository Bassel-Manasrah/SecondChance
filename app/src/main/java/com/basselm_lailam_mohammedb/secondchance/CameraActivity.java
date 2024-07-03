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

    private PreviewView previewView;
    private Button captureButton;
    private LifecycleCameraController cameraController;
    private boolean shouldRotate;

    private static final String[] REQUIRED_PERMISSIONS = { Manifest.permission.CAMERA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        this.previewView = findViewById(R.id.previewView);
        this.captureButton = findViewById(R.id.captureButton);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 0);
        }

        Log.d("myTag", getExternalFilesDir(null).toString());
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void startCamera() {

        LifecycleCameraController cameraController = new LifecycleCameraController(this);
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);

        this.previewView.setController(cameraController);
        this.captureButton.setOnClickListener(this);
        this.cameraController = cameraController;

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public Uri saveBitmap(Bitmap bmp) {

        File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + "");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return Uri.fromFile(file);
    }

    public boolean isPortraitMode() {
        Configuration configuration = getResources().getConfiguration();
        int orientation = configuration.orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public void onClick(View view) {

        this.cameraController.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);

                Bitmap bmp = image.toBitmap();

                if(isPortraitMode())
                    bmp = rotateBitmap(bmp, 90);

                Uri uri = saveBitmap(bmp);
                Log.d("mlog", uri.getPath());

                Intent intent = new Intent(CameraActivity.this, CreateListingActivity.class);
                intent.putExtra("imageUriString", uri.toString());
                startActivity(intent);
            }
        });
    }
}