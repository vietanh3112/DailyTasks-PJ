package com.example.dailytasks_pj;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileDetailActivity extends AppCompatActivity {

    private TextView textViewDetailUsername;
    private TextView textViewDetailPhone;
    private TextView textViewDetailPassword;
    private Button buttonBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ
        textViewDetailUsername = findViewById(R.id.textViewDetailUsername);
        textViewDetailPhone = findViewById(R.id.textViewDetailPhone);
        textViewDetailPassword = findViewById(R.id.textViewDetailPassword);
        buttonBack = findViewById(R.id.buttonBack);

        // Hiển thị thông tin người dùng (giả định username đã đăng nhập là "testuser")
        displayUserInfo("testuser");

        // Xử lý nút quay lại
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Hàm hiển thị thông tin người dùng từ SQLite
    private void displayUserInfo(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            String user = cursor.getString(cursor.getColumnIndex("username"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String password = cursor.getString(cursor.getColumnIndex("password"));

            textViewDetailUsername.setText("Tên đăng nhập: " + user);
            textViewDetailPhone.setText("Số điện thoại: " + phone);
            textViewDetailPassword.setText("Mật khẩu: " + password);
        }

        cursor.close();
        db.close();
    }
}
