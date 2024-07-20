package com.basselm_lailam_mohammedb.secondchance;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // List to store items
    private ArrayList<ItemModel> itemList;
    // Variables to store settings values
    private int setting_minPrice, setting_maxPrice;
    private boolean setting_onlyWithImage;
    // RecyclerView to display items
    private RecyclerView recyclerView;
    // SwipeRefreshLayout for swipe to refresh functionality
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register process lifecycle observer
        // Used to detect when the app is no longer in the foreground to schedule a notification
        // in case the user did not open the app in 2 days
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new AppLifecycleObserver(this));

        // Register the connection status broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new NetworkChangeReceiver(), filter);

        // Load settings from shared preferences
        loadSettings();

        // Change the title of the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");
        }

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Enable the SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSettings();
                fetchItems();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Fetch the items from Firebase
        fetchItems();
    }

    // Method to fetch items from Firebase
    private void fetchItems() {
        itemList = new ArrayList<>();
        ItemAdapter itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ItemModel item = firebaseDocumentToItemModel(document);

                        if (isMatchingItem(item))
                            itemList.add(item);
                    }
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Log.w("mlog", "Error getting documents.", task.getException());
                }
            }
        });
    }

    // Convert Firebase document to ItemModel object
    private ItemModel firebaseDocumentToItemModel(QueryDocumentSnapshot document) {
        String id = document.getId();
        String name = document.getString("name");
        String imgUrl = document.getString("imgUrl");
        Double price = document.getDouble("price");
        ItemModel item = new ItemModel(id, name, imgUrl, price);
        return item;
    }

    // Check if the item matches the filter criteria
    private boolean isMatchingItem(ItemModel item) {
        return item.getPrice() >= setting_minPrice &&
                item.getPrice() <= setting_maxPrice &&
                (!item.getImgUrl().isEmpty() || !setting_onlyWithImage);
    }

    // Load settings from shared preferences
    private void loadSettings() {
        SharedPreferences sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        setting_minPrice = sharedPrefs.getInt("minPrice", 0);
        setting_maxPrice = sharedPrefs.getInt("maxPrice", Integer.MAX_VALUE);
        setting_onlyWithImage = sharedPrefs.getBoolean("onlyWithImage", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.option_create_listing) {
            Intent intent = new Intent(MainActivity.this, CreateListingActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.option_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.option_about) {
            showAboutDialog();
            return true;
        }

        if (id == R.id.option_exit) {
            showExitDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSettings();
        fetchItems();
    }

    // Show the About dialog
    private void showAboutDialog() {
        String appName = getString(R.string.app_name);
        String appId = getPackageName();
        String osDetails = "OS Version: " + Build.VERSION.RELEASE + "\nSDK: " + Build.VERSION.SDK_INT;
        String submissionDate = "Submission Date: 21-07-2024";

        String message = "App Name: " + appName + "\n" +
                "App ID: " + appId + "\n\n" +
                osDetails + "\n\n" +
                "Developed by:" + "\n" +
                "Bassel Manasrah - 325240174" + "\n" +
                "Mohammed Belbisi - 206634131" + "\n" +
                "Layla Mahameed - 322799313" + "\n\n" +
                submissionDate;

        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Show the Exit dialog
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Application")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
