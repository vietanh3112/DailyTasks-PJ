package com.example.dailytasks_pj;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private TextInputEditText editTextTaskTitle;
    private TextInputEditText editTextStartTime;
    private Button buttonSaveTask;
    private Button buttonBack;
    private DatabaseHelper dbHelper;
    private TaskScheduler taskScheduler;
    private int taskId;
    private String taskDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // Khởi tạo DatabaseHelper và TaskScheduler
        dbHelper = new DatabaseHelper(this);
        taskScheduler = new TaskScheduler(this);

        // Ánh xạ các thành phần
        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);
        buttonBack = findViewById(R.id.buttonBack);

        // Nhận thông tin nhiệm vụ từ Intent
        taskId = getIntent().getIntExtra("taskId", -1);
        String taskTitle = getIntent().getStringExtra("taskTitle");
        String taskStartTime = getIntent().getStringExtra("taskStartTime");
        taskDate = getIntent().getStringExtra("taskDate");

        if (taskId == -1 || taskDate == null) {
            Toast.makeText(this, "Không nhận được thông tin nhiệm vụ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Điền thông tin nhiệm vụ vào các trường
        editTextTaskTitle.setText(taskTitle);
        editTextStartTime.setText(taskStartTime);

        // Xử lý chọn thời gian
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

            // Cập nhật nhiệm vụ vào cơ sở dữ liệu
            boolean success = dbHelper.updateTask(taskId, title, startTime, taskDate);
            if (success) {
                Toast.makeText(this, "Cập nhật nhiệm vụ thành công!", Toast.LENGTH_SHORT).show();

                // Lập lịch lại thông báo
                taskScheduler.cancelTaskNotification(taskId); // Hủy thông báo cũ
                taskScheduler.scheduleTaskNotification(taskId, title, startTime, taskDate); // Lập lịch thông báo mới

                // Kết thúc activity
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật nhiệm vụ thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút quay lại
        buttonBack.setOnClickListener(v -> finish());
    }
}