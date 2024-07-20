package com.basselm_lailam_mohammedb.secondchance;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<ItemModel> itemList; // List of items to display
    private Context context; // Context to create dialogs

    // Constructor to initialize the item list and context
    public ItemAdapter(ArrayList<ItemModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    // ViewHolder class to hold references to the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price;
        ImageView iv_img;
        Button btn_showInfo;

        public ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name); // Name text view
            tv_price = view.findViewById(R.id.tv_price); // Price text view
            iv_img = view.findViewById(R.id.iv_img); // Image view
            btn_showInfo = view.findViewById(R.id.btn_showInfo); // Button to show more info
        }
    }

    // Inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false); // Inflate the item layout
        return new ViewHolder(view);
    }

    // Bind data to the views in each ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = itemList.get(position); // Get the current item

        holder.tv_name.setText(item.getName()); // Set the item name
        holder.tv_price.setText(String.valueOf(item.getPrice())); // Set the item price

        // Load the item image using Glide, or set a default image if the URL is empty
        Glide.with(holder.itemView.getContext())
                .load(item.getImgUrl().isEmpty() ? R.drawable.default_product_image : item.getImgUrl())
                .into(holder.iv_img);

        // Set click listener for the show info button
        holder.btn_showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAndShowPopup(item.getId()); // Show popup with item details
            }
        });
    }

    // Return the size of the item list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Fetch item details from Firestore and show a popup
    private void CreateAndShowPopup(String itemId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    String desc = document.getString("desc"); // Get item description
                    String phone = document.getString("phone"); // Get item phone number
                    showPopup(desc, phone); // Show popup with item details
                } else {
                    Log.w("ItemAdapter", "Error getting document.", task.getException()); // Log error
                }
            }
        });
    }

    // Show a popup with item description and phone number
    private void showPopup(String description, String phone_Number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("More Information");
        builder.setMessage("Description: " + description + "\nPhone Number: " + phone_Number);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
