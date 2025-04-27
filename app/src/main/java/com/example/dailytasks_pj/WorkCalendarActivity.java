package com.example.dailytasks_pj;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.CalendarView;
import android.widget.Toast;

public class WorkCalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private BottomNavigationView bottomNavigationView;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // Lấy username từ Intent
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            currentUsername = "testuser"; // Giá trị mặc định nếu username null
            Toast.makeText(this, "Không nhận được username, sử dụng giá trị mặc định.", Toast.LENGTH_SHORT).show();
        } else {
            currentUsername = username;
        }

        // Ánh xạ BottomNavigationView và CalendarView
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        calendarView = findViewById(R.id.calendarView);

        // Đặt mục "Lịch" làm mục được chọn
        bottomNavigationView.setSelectedItemId(R.id.menu_calendar);

        // Xử lý sự kiện khi chọn mục trên BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int itemId = item.getItemId();
                if (itemId == R.id.menu_tasks) {
                    intent = new Intent(WorkCalendarActivity.this, HomeActivity.class);
                    intent.putExtra("username", currentUsername);
                    startActivity(intent);
                    finish(); // Đóng activity hiện tại
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    return true; // Đã ở WorkCalendarActivity, không cần mở lại
                } else if (itemId == R.id.menu_profile) {
                    intent = new Intent(WorkCalendarActivity.this, ProfileActivity.class);
                    intent.putExtra("username", currentUsername);
                    startActivity(intent);
                    finish(); // Đóng activity hiện tại
                    return true;
                }
                return false;
            }
        });

        // Xử lý sự kiện khi chọn ngày trên CalendarView
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Truyền year, month, day qua Intent
                Intent intent = new Intent(WorkCalendarActivity.this, TaskOfDate.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month + 1); // month bắt đầu từ 0, cần +1
                intent.putExtra("day", dayOfMonth);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });
    }
}