package com.example.dailytasks_pj;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private ListView listViewTasks;
    private ImageView emptyStateImage;
    private FloatingActionButton buttonAdd;
    private DatabaseHelper dbHelper;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private BottomNavigationView bottomNavigationView;
    private TaskScheduler taskScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        taskScheduler = new TaskScheduler(this);

        // Ánh xạ
        listViewTasks = findViewById(R.id.listViewTasks);
        emptyStateImage = findViewById(R.id.emptyStateImage);
        buttonAdd = findViewById(R.id.buttonAdd);

        // Thiết lập ListView
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(taskAdapter);

        // Lấy ngày hiện tại (định dạng: yyyy-MM-dd)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Hiển thị danh sách nhiệm vụ của ngày hôm nay
        loadTasks(currentDate);

        // Xử lý nút "Thêm"
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
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
                    intent = new Intent(HomeActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    intent = new Intent(HomeActivity.this, WorkCalendarActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
    public void deleteTask(int taskId) {
        boolean success = dbHelper.deleteTask(taskId);
        if (success) {
            Toast.makeText(this, "Xóa nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();
            if (taskScheduler == null) {
                taskScheduler = new TaskScheduler(this);
            }
            else{
                try {
                    taskScheduler.cancelTaskNotification(taskId); // Hủy thông báo
                } catch (Exception e) {
                    Toast.makeText(this, "Không thể hủy thông báo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            loadTasks(currentDate);
        } else {
            Toast.makeText(this, "Xóa nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách nhiệm vụ khi quay lại từ AddTaskActivity
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        loadTasks(currentDate);
    }

    // Hàm tải danh sách nhiệm vụ
    private void loadTasks(String date) {
        taskList.clear();
        Cursor cursor = dbHelper.getTasksByDate(date);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
                String taskDate = cursor.getString(cursor.getColumnIndex("date"));
                Task task = new Task(id, title, startTime, taskDate);
                taskList.add(task);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Hiển thị hình gấu trúc nếu danh sách trống
        if (taskList.isEmpty()) {
            emptyStateImage.setVisibility(View.VISIBLE);
            listViewTasks.setVisibility(View.GONE);
        } else {
            emptyStateImage.setVisibility(View.GONE);
            listViewTasks.setVisibility(View.VISIBLE);
        }

        taskAdapter.notifyDataSetChanged();
    }
}