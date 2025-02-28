package com.example.inventoryapp;

import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.telephony.SmsManager;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private GridView gridView;
    private InventoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private ArrayList<InventoryItem> inventoryList;
    private Button btnAddNewItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        gridView = findViewById(R.id.inventoryGrid);
        btnAddNewItem = findViewById(R.id.btnAddNewItem);
        dbHelper = new DatabaseHelper(this);
        inventoryList = new ArrayList<>();

        // Request permission when activity starts
        requestSmsPermission();

        insertTestData();
        loadInventory();

        btnAddNewItem.setOnClickListener(v -> {
            dbHelper.insertInventoryItem("New Item " + System.currentTimeMillis(), 1, 1);
            loadInventory();
        });
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertTestData() {
        Cursor cursor = dbHelper.getAllInventoryItems();
        if (cursor.getCount() == 0) {
            Log.d("InventoryActivity", "No inventory items found. Inserting test data...");

            dbHelper.insertInventoryItem("Laptop", 5, 2);
            dbHelper.insertInventoryItem("Mouse", 10, 3);
            dbHelper.insertInventoryItem("Keyboard", 7, 2);
            dbHelper.insertInventoryItem("Monitor", 4, 1);
            dbHelper.insertInventoryItem("USB Drive", 15, 5);
        }
        cursor.close();
    }

    private void loadInventory() {
        inventoryList.clear();
        Cursor cursor = dbHelper.getAllInventoryItems();

        if (cursor == null || cursor.getCount() == 0) {
            Log.e("InventoryActivity", "No inventory items found in database.");
            Toast.makeText(this, "No inventory items found.", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int quantity = cursor.getInt(2);
            int threshold = cursor.getInt(3);

            Log.d("InventoryActivity", "Item Loaded: " + name + " - Qty: " + quantity);
            inventoryList.add(new InventoryItem(id, name, quantity, threshold));
            checkLowInventory(new InventoryItem(id, name, quantity, threshold));
        }
        cursor.close();

        if (adapter == null) {
            adapter = new InventoryAdapter(this, inventoryList);
            gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void checkLowInventory(InventoryItem item) {
        if (item.getQuantity() <= item.getThreshold()) {
            String phoneNumber = "+17609942776";
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                sendSmsAlert(phoneNumber, "Alert! Low inventory for: " + item.getName());
            } else {
                Log.e("InventoryActivity", "Phone number is invalid or missing.");
            }
        }
    }

    private void sendSmsAlert(String phoneNumber, String message) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                if (smsManager != null) {
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Toast.makeText(this, "SMS Alert Sent!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("InventoryActivity", "SmsManager is null. Cannot send SMS.");
                }
            } catch (Exception e) {
                Log.e("InventoryActivity", "SMS Sending Failed: " + e.getMessage());
            }
        }
    }
}
