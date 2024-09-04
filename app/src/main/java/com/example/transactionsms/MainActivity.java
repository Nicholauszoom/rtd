package com.example.transactionsms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button startBtn;
    private Button viewBtn;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private DrawerLayout drawerLayout;
    private TextView link1TextView, link2TextView, link3TextView;

    private ListView navigationListView;

    private final String[] navigationTitles = {"HOME", "VIEW RECORDS", "SYNCHRONIZE DATA"};
    private final int[] navigationIcons = {R.drawable.icon_link1, R.drawable.icon_link2, R.drawable.icon_link3};

    private LinearLayout imageContainer;
    private int[] imageUrls = {R.drawable.picf1, R.drawable.picf2, R.drawable.picf3, R.drawable.picf4, R.drawable.picf5, R.drawable.picf6};
    private int currentImageIndex = 0;
    private Handler handler;
    private Runnable imageTransitionRunnable;
    private static final int IMAGE_TRANSITION_DELAY = 5000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageContainer = findViewById(R.id.imageContainer);
        handler = new Handler();

        imageTransitionRunnable = new Runnable() {
            @Override
            public void run() {
                transitionToNextImage();
                handler.postDelayed(this, IMAGE_TRANSITION_DELAY);
            }
        };

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.logo_mpesa, 40));
        categoryList.add(new Category(R.drawable.logo_halotel_pesa, 30));
        categoryList.add(new Category(R.drawable.logo_tigo_pesa, 13));
        categoryList.add(new Category(R.drawable.logo_ttcl_pesa, 60));
        categoryList.add(new Category(R.drawable.logo_airtel_money, 100));
        categoryList.add(new Category(R.drawable.logo_zantel_money, 30));
        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(categoryAdapter);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationListView = findViewById(R.id.navigationListView);

        navigationListView.setAdapter(new NavigationAdapter(MainActivity.this, navigationTitles, navigationIcons));
        navigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Handle Link 1 Click
                        handleLink1Click();
                        break;
                    case 1:
                        // Handle Link 2 Click
                        handleLink2Click();
                        break;
                    case 2:
                        // Handle Link 3 Click
                        handleLink3Click();
                        break;
                }

                drawerLayout.closeDrawers();
            }
        });
    }

    // Start image transitions when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();
        startImageTransitions();
    }

    // Stop image transitions when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        stopImageTransitions();
    }

    // Start the image transitions
    private void startImageTransitions() {
        handler.postDelayed(imageTransitionRunnable, IMAGE_TRANSITION_DELAY);
    }

    // Stop the image transitions
    private void stopImageTransitions() {
        handler.removeCallbacks(imageTransitionRunnable);
    }

    // Transition to the next image
    private void transitionToNextImage() {
        currentImageIndex = (currentImageIndex + 1) % imageUrls.length;
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        imageView.setImageResource(imageUrls[currentImageIndex]);
        imageContainer.addView(imageView);

        // Remove the previous image
        if (imageContainer.getChildCount() > 1) {
            imageContainer.removeViewAt(0);
        }
    }

    private void handleLink1Click() {
        // Handle Link 1 click action
        startActivity(new Intent(MainActivity.this, MainActivity.class));
    }

    private void handleLink2Click() {
        // Handle Link 2 click action
        startActivity(new Intent(MainActivity.this, SmsActivity.class));
    }

    private void handleLink3Click() {
        // Handle Link 3 click action
        startActivity(new Intent(MainActivity.this, MainActivity2.class));
    }
}