package com.example.dailytasks_pj;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private TextView textViewProfile;
    private TextView textViewEditInfo;
    private TextView textViewLogout;
    private DatabaseHelper dbHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewProfile = findViewById(R.id.textViewPersonalNumber);
        textViewEditInfo = findViewById(R.id.textViewEditInfo);
        textViewLogout = findViewById(R.id.textViewLogout);

        // Ánh xạ BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Nhận username từ Intent
        String username = getIntent().getStringExtra("username");

        // Đặt mục "Của tôi" làm mục mặc định
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);

        // Xử lý sự kiện khi chọn mục trên BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int itemId = item.getItemId(); // Lấy ID của item
                if (itemId == R.id.menu_tasks) {
                    intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    intent = new Intent(ProfileActivity.this, WorkCalendarActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Hiển thị thông tin người dùng (giả định username đã đăng nhập là "testuser")
        displayUserInfo("testuser");

        // Xử lý sự kiện "Hồ sơ cá nhân"
        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Có thể chuyển đến một màn hình chi tiết hồ sơ (nếu cần)
                // Hiện tại chỉ hiển thị thông báo
                Intent intent = new Intent(ProfileActivity.this, ProfileDetailActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện "Sửa thông tin"
        textViewEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình sửa thông tin
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý đăng xuất
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
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
            textViewUsername.setText(user);
        }

        cursor.close();
        db.close();
    }
}