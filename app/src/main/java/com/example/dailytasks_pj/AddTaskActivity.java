package com.example.dailytasks_pj;

import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText editTextTaskTitle;
    private TextInputEditText editTextStartTime;
    private Button buttonSaveTask;
    private Button buttonBack;
    private DatabaseHelper dbHelper;
    private TaskScheduler taskScheduler;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Khởi tạo DatabaseHelper và TaskScheduler
        dbHelper = new DatabaseHelper(this);
        taskScheduler = new TaskScheduler(this);

        // Ánh xạ các thành phần từ layout
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);
        buttonBack = findViewById(R.id.buttonBack);

        // Nhận ngày từ Intent (nếu có)
        selectedDate = getIntent().getStringExtra("date");
        if (selectedDate == null) {
            // Nếu không có ngày, đặt mặc định là ngày hiện tại
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            selectedDate = dateFormat.format(Calendar.getInstance().getTime());
        }

        // Xử lý chọn thời gian bằng TimePickerDialog
        editTextStartTime.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int hour = currentTime.get(Calendar.HOUR_OF_DAY);
            int minute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        editTextStartTime.setText(time);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Xử lý nút lưu
        buttonSaveTask.setOnClickListener(v -> {
            String title = editTextTaskTitle.getText().toString().trim();
            String startTime = editTextStartTime.getText().toString().trim();

            // Kiểm tra dữ liệu
            if (title.isEmpty()) {
                editTextTaskTitle.setError("Vui lòng nhập tên nhiệm vụ!");
                return;
            }
            if (startTime.isEmpty()) {
                editTextStartTime.setError("Vui lòng chọn thời gian bắt đầu!");
                return;
            }

            // Lưu nhiệm vụ vào cơ sở dữ liệu
            boolean success = dbHelper.addTask(title, startTime, selectedDate);
            if (success) {
                Toast.makeText(this, "Thêm nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();

                // Lập lịch thông báo cho nhiệm vụ vừa thêm
                Cursor cursor = dbHelper.getTasksByDate(selectedDate);
                if (cursor != null && cursor.moveToLast()) {
                    int taskId = cursor.getInt(cursor.getColumnIndex("id"));
                    taskScheduler.scheduleTaskNotification(taskId, title, startTime, selectedDate);
                    cursor.close();
                }

                // Kết thúc activity
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Thêm nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút quay lại
        buttonBack.setOnClickListener(v -> finish());
    }
}