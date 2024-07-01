package com.basselm_lailam_mohammedb.secondchance;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ItemModel> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemList = new ArrayList<>();
        fillItemList();

        RecyclerView recyclerView = findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ItemAdapter(itemList));
    }

    private void fillItemList() {
        String url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTUaFLID0yOqt2KY16nNSmTWhu0y10-aeBq2g&s";
        ItemModel t1 = new ItemModel("a", 10, url);
        ItemModel t2 = new ItemModel("b", 20, url);
        ItemModel t3 = new ItemModel("c", 30, url);
        itemList.add(t1);
        itemList.add(t2);
        itemList.add(t3);
    }
}