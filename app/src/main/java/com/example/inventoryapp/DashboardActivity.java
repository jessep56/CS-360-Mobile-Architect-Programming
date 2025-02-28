package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {
    private Button logoutButton, inventoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard); // Link to XML layout

        // Initialize buttons
        logoutButton = findViewById(R.id.logout_button);
        inventoryButton = findViewById(R.id.inventory_button); // New button

        // Logout and return to login screen
        logoutButton.setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish(); // Close DashboardActivity
        });

        // Open InventoryActivity
        inventoryButton.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, InventoryActivity.class);
            startActivity(intent);
        });
    }
}
