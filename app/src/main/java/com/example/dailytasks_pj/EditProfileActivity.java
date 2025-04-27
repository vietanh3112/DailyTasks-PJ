package com.example.dailytasks_pj;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextView textViewEditUsername;
    private TextInputEditText editTextEditPhone;
    private TextInputEditText editTextEditPassword;
    private Button buttonSave;
    private Button buttonBack;
    private DatabaseHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ
        textViewEditUsername = findViewById(R.id.textViewEditUsername);
        editTextEditPhone = findViewById(R.id.editTextEditPhone);
        editTextEditPassword = findViewById(R.id.editTextEditPassword);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBack);

        // Giả định username đã đăng nhập là "testuser"
        username = "testuser";

        // Hiển thị thông tin hiện tại
        displayUserInfo(username);

        // Xử lý nút lưu
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPhone = editTextEditPhone.getText().toString().trim();
                String newPassword = editTextEditPassword.getText().toString().trim();

                if (validateInput(newPhone, newPassword)) {
                    boolean isUpdated = dbHelper.updateUser(username, newPhone, newPassword);
                    if (isUpdated) {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Xử lý nút quay lại
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String password = cursor.getString(cursor.getColumnIndex("password"));

            textViewEditUsername.setText("Tên đăng nhập: " + user);
            editTextEditPhone.setText(phone);
            editTextEditPassword.setText(password);
        }

        cursor.close();
        db.close();
    }

    // Hàm kiểm tra đầu vào
    private boolean validateInput(String phone, String password) {
        if (phone.isEmpty()) {
            editTextEditPhone.setError("Vui lòng nhập số điện thoại!");
            return false;
        }
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            editTextEditPhone.setError("Số điện thoại không hợp lệ!");
            return false;
        }
        if (password.isEmpty()) {
            editTextEditPassword.setError("Vui lòng nhập mật khẩu!");
            return false;
        }
        if (password.length() < 6) {
            editTextEditPassword.setError("Mật khẩu phải dài ít nhất 6 ký tự!");
            return false;
        }
        return true;
    }
}
