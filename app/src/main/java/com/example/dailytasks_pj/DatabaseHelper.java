package com.example.dailytasks_pj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 4; // Tăng version để áp dụng nâng cấp
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_START_TIME = "start_time";
    private static final String COLUMN_TASK_DATE = "date";
    private static final String COLUMN_USER_ID = "user_id"; // Thêm cột user_id

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PHONE + " TEXT)";
        db.execSQL(createTable);

        // Thêm tài khoản mẫu
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "testuser");
        values.put(COLUMN_PASSWORD, "password123");
        values.put(COLUMN_PHONE, "0123456789");
        db.insert(TABLE_USERS, null, values);

        // Tạo bảng tasks với cột user_id
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_TITLE + " TEXT, " +
                COLUMN_TASK_START_TIME + " TEXT, " +
                COLUMN_TASK_DATE + " TEXT, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createTasksTable);

        // Thêm nhiệm vụ mẫu cho ngày hôm nay (27/04/2025) với user_id mẫu
        ContentValues taskValues = new ContentValues();
        taskValues.put(COLUMN_TASK_TITLE, "Họp nhóm");
        taskValues.put(COLUMN_TASK_START_TIME, "09:00");
        taskValues.put(COLUMN_TASK_DATE, "2025-04-27");
        taskValues.put(COLUMN_USER_ID, 1); // Gán user_id của "testuser"
        db.insert(TABLE_TASKS, null, taskValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Thêm cột user_id vào bảng tasks nếu chưa có
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + COLUMN_USER_ID + " INTEGER NOT NULL DEFAULT 1");
            // Cập nhật dữ liệu cũ với user_id mặc định (1)
            // (Giả sử tất cả nhiệm vụ cũ thuộc user_id 1, bạn có thể điều chỉnh sau)
        }
    }

    // Hàm kiểm tra đăng nhập
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Hàm thêm người dùng mới
    public boolean addUser(String username, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateUser(String username, String newPhone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, newPhone);
        values.put(COLUMN_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    // Thêm nhiệm vụ mới với user_id
    public boolean addTask(String title, String startTime, String date, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, title);
        values.put(COLUMN_TASK_START_TIME, startTime);
        values.put(COLUMN_TASK_DATE, date);
        values.put(COLUMN_USER_ID, userId);
        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result != -1;
    }

    // Lấy danh sách nhiệm vụ theo ngày và user_id
    public Cursor getTasksByDateAndUser(String date, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_DATE + " = ? AND " + COLUMN_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{date, String.valueOf(userId)});
    }

    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS;
        return db.rawQuery(query, null);
    }

    // Xóa nhiệm vụ theo ID
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return result > 0;
    }

    // Cập nhật nhiệm vụ theo ID với user_id
    public boolean updateTask(int taskId, String title, String startTime, String date, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, title);
        values.put(COLUMN_TASK_START_TIME, startTime);
        values.put(COLUMN_TASK_DATE, date);
        values.put(COLUMN_USER_ID, userId);
        int result = db.update(TABLE_TASKS, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return result > 0;
    }

    // Lấy user_id dựa trên username
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }
    public int getLastTaskId() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COLUMN_TASK_ID + ") FROM " + TABLE_TASKS;
        Cursor cursor = db.rawQuery(query, null);
        int lastId = -1;
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return lastId;
    }
}