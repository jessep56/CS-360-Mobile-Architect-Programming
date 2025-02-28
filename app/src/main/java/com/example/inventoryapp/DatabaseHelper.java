package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory_app.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Inventory Table
    private static final String TABLE_INVENTORY = "inventory";
    private static final String COLUMN_ITEM_ID = "id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_THRESHOLD = "threshold";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT);";

        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_THRESHOLD + " INTEGER NOT NULL DEFAULT 1);";

        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    // Insert new user
    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if user exists
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Insert Inventory Item
    public boolean insertInventoryItem(String itemName, int quantity, int threshold) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_THRESHOLD, threshold);
        long result = db.insert(TABLE_INVENTORY, null, values);
        db.close();
        return result != -1;
    }

    // Update Inventory Item Quantity
    public boolean updateInventoryItem(int itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);
        int rowsAffected = db.update(TABLE_INVENTORY, values, COLUMN_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        db.close();
        return rowsAffected > 0;
    }

    // Delete Inventory Item
    public boolean deleteInventoryItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_INVENTORY, COLUMN_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)});
        db.close();
        return rowsDeleted > 0;
    }

    public boolean updateItemName(int id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item_name", newName);

        int rowsAffected = db.update("inventory", values, "id=?", new String[]{String.valueOf(id)});
        return rowsAffected > 0;
    }


    // Retrieve All Inventory Items
    public Cursor getAllInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INVENTORY, null);
    }
}
