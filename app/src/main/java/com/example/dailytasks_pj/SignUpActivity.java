package com.example.dailytasks_pj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPasswordSignUp;
    private TextInputEditText editTextConfirmPassword;
    private Button buttonCreateAccount;
    private TextInputEditText editTextPhone;
    private DatabaseHelper dbHelper;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ
        editTextUsername = findViewById(R.id.editTextText);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPasswordSignUp = findViewById(R.id.editTextTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextTextPassword2);
        buttonCreateAccount = findViewById(R.id.buttonCreate);
        textViewLogin =findViewById(R.id.textView3);
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Xử lý nút tạo tài khoản
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String password = editTextPasswordSignUp.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                // Kiểm tra đầu vào
                if (validateSignUpInput(username, phone, password, confirmPassword)) {
                    boolean isAdded = dbHelper.addUser(username, password, phone);
                    if (isAdded) {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Đăng ký thất bại! Tên đăng nhập đã tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Hàm kiểm tra đầu vào đăng ký
    private boolean validateSignUpInput(String username,String phone, String password, String confirmPassword) {
        if (username.isEmpty()) {
            editTextUsername.setError("Vui lòng nhập tên đăng nhập!");
            return false;
        }
        if (password.isEmpty()) {
            editTextPasswordSignUp.setError("Vui lòng nhập mật khẩu!");
            return false;
        }
        if (password.length() < 6) {
            editTextPasswordSignUp.setError("Mật khẩu phải dài ít nhất 6 ký tự!");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Mật khẩu không khớp!");
            return false;
        }
        if (!phone.matches("\\d{10}")) {
            editTextPhone.setError("Số điện thoại phải có đúng 10 chữ số!");
            return false;
        }
        return true;
    }
}