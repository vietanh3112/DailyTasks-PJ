package com.example.dailytasks_pj;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Nhận username từ Intent
        String username = getIntent().getStringExtra("username");

        // Đặt mục "Nhiệm vụ" làm mục mặc định
        bottomNavigationView.setSelectedItemId(R.id.menu_tasks);

        // Xử lý sự kiện khi chọn mục trên BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int itemId = item.getItemId(); // Lấy ID của item
                if (itemId == R.id.menu_tasks) {
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    intent = new Intent(MainActivity.this, WorkCalendarActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Mặc định mở HomeActivity khi khởi động
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}