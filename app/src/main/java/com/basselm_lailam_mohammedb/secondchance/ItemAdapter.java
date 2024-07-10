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

    private ArrayList<ItemModel> itemList;
    private Context context;

    public ItemAdapter(ArrayList<ItemModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_price;
        ImageView iv_img;
        Button btn_showInfo;

        public ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_price = view.findViewById(R.id.tv_price);
            iv_img = view.findViewById(R.id.iv_img);
            btn_showInfo = view.findViewById(R.id.btn_showInfo);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemModel item = itemList.get(position);

        holder.tv_name.setText(item.getName());
        holder.tv_price.setText(String.valueOf(item.getPrice()));
        Glide.with(holder.itemView.getContext())
                        .load(item.getImgUrl().isEmpty() ? R.drawable.default_product_image : item.getImgUrl())
                        .into(holder.iv_img);

        holder.btn_showInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAndShowPopup(item.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private void CreateAndShowPopup(String itemId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(itemId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    String desc = document.getString("desc");
                    String phone = document.getString("phone");
                    showPopup(desc, phone);
                } else {
                    Log.w("ItemAdapter", "Error getting document.", task.getException());
                }
            }
        });
    }

    private void showPopup(String description, String phone_Number ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("More Information");
        builder.setMessage("Description: " + description + "\nPhone Number: " + phone_Number);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
