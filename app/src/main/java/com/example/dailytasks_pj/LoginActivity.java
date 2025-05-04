package com.example.dailytasks_pj;

import static com.example.dailytasks_pj.R.id.editTextUsername;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private DatabaseHelper dbHelper;
    private TextView textViewDangKy;
    private TaskScheduler taskScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        dbHelper = new DatabaseHelper(this);
        taskScheduler = new TaskScheduler(this);

        editTextUsername =findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        textViewDangKy = findViewById(R.id.textView3);
        textViewDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Kiểm tra thông tin đăng nhập
                if (validateInput(username, password)) {
                    if (dbHelper.checkUser(username, password)) {
                        SharedPreferences preferences = getSharedPreferences("DailyTasksPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", username);
                        editor.apply();
                        // Lập lịch thông báo cho tất cả nhiệm vụ
                        taskScheduler.scheduleAllTaskNotifications();
                        // Đăng nhập thành công, chuyển đến MainActivity
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            editTextUsername.setError("Vui lòng nhập tên đăng nhập!");
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Vui lòng nhập mật khẩu!");
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Mật khẩu phải dài ít nhất 6 ký tự!");
            return false;
        }
        return true;
    }
}