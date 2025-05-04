package com.example.dailytasks_pj;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private String currentUsername;
    private int currentUserId;

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

        currentUsername = getIntent().getStringExtra("username");

        // Nếu username không được truyền, lấy từ SharedPreferences
        if (currentUsername == null) {
            SharedPreferences preferences = getSharedPreferences("DailyTasksPrefs", MODE_PRIVATE);
            currentUsername = preferences.getString("username", null);
            if (currentUsername == null) {
                Toast.makeText(this, "Không tìm thấy thông tin đăng nhập, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(this, MainActivity.class);
                startActivity(loginIntent);
                finish();
                return;
            }
        }

        // Lấy user_id từ DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        currentUserId = dbHelper.getUserIdByUsername(currentUsername);
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy user_id, vui lòng kiểm tra dữ liệu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
                    intent.putExtra("username", currentUsername);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_calendar) {
                    intent = new Intent(HomeActivity.this, WorkCalendarActivity.class);
                    intent.putExtra("username", currentUsername);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_profile) {
                    intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("username", currentUsername);
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
            loadTasks(currentDate); // Làm mới danh sách
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
        try {
            Cursor cursor = dbHelper.getTasksByDateAndUser(date,currentUserId);
            if (cursor != null && cursor.moveToFirst()) {
                // Kiểm tra các cột có tồn tại không
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int startTimeIndex = cursor.getColumnIndex("start_time");
                int dateIndex = cursor.getColumnIndex("date");

                if (idIndex == -1 || titleIndex == -1 || startTimeIndex == -1 || dateIndex == -1) {
                    Toast.makeText(this, "Lỗi: Cột không tồn tại trong cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    cursor.close();
                    return;
                }

                do {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String taskDate = cursor.getString(dateIndex);
                    Task task = new Task(id, title, startTime, taskDate);
                    taskList.add(task);
                } while (cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải nhiệm vụ: " + e.getMessage(), Toast.LENGTH_LONG).show();
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