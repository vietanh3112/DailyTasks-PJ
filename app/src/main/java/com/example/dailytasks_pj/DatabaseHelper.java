package com.example.dailytasks_pj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username"; // Thay email thành username
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    // Bảng nhiệm vụ
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_START_TIME = "start_time";
    private static final String COLUMN_TASK_DATE = "date";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users với cột username thay vì email
        String createTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " + // Thay email thành username
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PHONE + " TEXT)";
        db.execSQL(createTable);

        // Thêm tài khoản mẫu (username: testuser, password: password123)
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, "testuser");
        values.put(COLUMN_PASSWORD, "password123");
        values.put(COLUMN_PHONE, "0123456789");
        db.insert(TABLE_USERS, null, values);

        // Tạo bảng tasks
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_TITLE + " TEXT, " +
                COLUMN_TASK_START_TIME + " TEXT, " +
                COLUMN_TASK_DATE + " TEXT)";
        db.execSQL(createTasksTable);

        // Thêm nhiệm vụ mẫu cho ngày hôm nay (27/04/2025)
        ContentValues taskValues = new ContentValues();
        taskValues.put(COLUMN_TASK_TITLE, "Họp nhóm");
        taskValues.put(COLUMN_TASK_START_TIME, "09:00");
        taskValues.put(COLUMN_TASK_DATE, "2025-04-27");
        db.insert(TABLE_TASKS, null, taskValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
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
        return result != -1; // Trả về true nếu thêm thành công
    }
    public boolean updateUser(String username, String newPhone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, newPhone);
        values.put(COLUMN_PASSWORD, newPassword);
        int result = db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
        db.close();
        return result > 0; // Trả về true nếu cập nhật thành công
    }
    // Thêm nhiệm vụ mới
    public boolean addTask(String title, String startTime, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, title);
        values.put(COLUMN_TASK_START_TIME, startTime);
        values.put(COLUMN_TASK_DATE, date);
        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result != -1;
    }

    // Lấy danh sách nhiệm vụ theo ngày
    public Cursor getTasksByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_TASK_DATE + " = ?";
        return db.rawQuery(query, new String[]{date});
    }
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS;
        return db.rawQuery(query, null);
    }
    // Xóa nhiệm vụ theo ID
    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return result > 0;
    }

    // Cập nhật nhiệm vụ theo ID
    public boolean updateTask(int taskId, String title, String startTime, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, title);
        values.put(COLUMN_TASK_START_TIME, startTime);
        values.put(COLUMN_TASK_DATE, date);

        int result = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return result > 0;
    }
}