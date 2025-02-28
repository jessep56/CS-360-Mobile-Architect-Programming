package com.example.inventoryapp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class InventoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<InventoryItem> inventoryList;
    private DatabaseHelper dbHelper;

    public InventoryAdapter(Context context, ArrayList<InventoryItem> inventoryList) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return inventoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return inventoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
        }

        InventoryItem item = inventoryList.get(position);

        EditText etItemName = convertView.findViewById(R.id.etItemName);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        Button btnIncrease = convertView.findViewById(R.id.btnIncrease);
        Button btnDecrease = convertView.findViewById(R.id.btnDecrease);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        etItemName.setText(item.getName());
        tvQuantity.setText("Qty: " + item.getQuantity());

        // Enable item name editing
        etItemName.setOnClickListener(v -> showEditNameDialog(item, etItemName));

        // Increase quantity
        btnIncrease.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            if (dbHelper.updateInventoryItem(item.getId(), newQty)) {
                item.setQuantity(newQty);
                tvQuantity.setText("Qty: " + newQty);
            }
        });

        // Decrease quantity
        btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                int newQty = item.getQuantity() - 1;
                if (dbHelper.updateInventoryItem(item.getId(), newQty)) {
                    item.setQuantity(newQty);
                    tvQuantity.setText("Qty: " + newQty);
                }
            } else {
                Toast.makeText(context, "Cannot reduce below 0", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete item
        btnDelete.setOnClickListener(v -> {
            if (dbHelper.deleteInventoryItem(item.getId())) {
                inventoryList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    // Show a dialog to edit item name
    private void showEditNameDialog(InventoryItem item, EditText etItemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Item Name");

        final EditText input = new EditText(context);
        input.setText(item.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && dbHelper.updateItemName(item.getId(), newName)) {
                item.setName(newName);
                etItemName.setText(newName);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
