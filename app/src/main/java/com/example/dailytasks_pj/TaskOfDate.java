package com.example.dailytasks_pj;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class TaskOfDate extends AppCompatActivity {

    private Button buttonBack;
    private ListView listViewTasks;
    private ImageView emptyStateImage;
    private FloatingActionButton buttonAdd;
    private DatabaseHelper dbHelper;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private String currentDate;
    private TaskScheduler taskScheduler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_of_date);

        // Nhận ngày từ Intent
        Intent intent = getIntent();
        int year = intent.getIntExtra("year", -1);
        int month = intent.getIntExtra("month", -1);
        int day = intent.getIntExtra("day", -1);

        // Kiểm tra dữ liệu hợp lệ
        if (year == -1 || month == -1 || day == -1) {
            Toast.makeText(this, "Không nhận được ngày hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Định dạng ngày thành yyyy-MM-dd
        currentDate = String.format("%04d-%02d-%02d", year, month, day);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        taskScheduler = new TaskScheduler(this);

        // Ánh xạ
        buttonBack = findViewById(R.id.buttonBack);
        listViewTasks = findViewById(R.id.listViewTasks);
        emptyStateImage = findViewById(R.id.emptyStateImage);
        buttonAdd = findViewById(R.id.buttonAdd);

        // Thiết lập ListView
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        listViewTasks.setAdapter(taskAdapter);

        // Xử lý nút quay lại
        buttonBack.setOnClickListener(v -> finish());

        // Xử lý nút thêm nhiệm vụ
        buttonAdd.setOnClickListener(v -> {
            Intent addIntent = new Intent(TaskOfDate.this, AddTaskActivity.class);
            addIntent.putExtra("date", currentDate); // Truyền ngày đã chọn
            startActivity(addIntent);
        });

        // Tải danh sách nhiệm vụ
        loadTasks(currentDate);
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
            loadTasks(currentDate); // Làm mới danh sách
        } else {
            Toast.makeText(this, "Xóa nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại danh sách nhiệm vụ khi quay lại
        loadTasks(currentDate);
    }

    private void loadTasks(String date) {
        taskList.clear();
        try {
            Cursor cursor = dbHelper.getTasksByDate(date);
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
            } else {
                Toast.makeText(this, "Không có nhiệm vụ nào vào ngày " + date, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi tải nhiệm vụ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Hiển thị hình ảnh nếu danh sách trống
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