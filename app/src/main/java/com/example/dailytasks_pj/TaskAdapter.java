package com.example.dailytasks_pj;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {

    private Context context;
    private ArrayList<Task> taskList;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        super(context, 0, taskList);
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        }

        Task task = taskList.get(position);

        TextView textViewTaskTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewStartTime = convertView.findViewById(R.id.textViewTime);
        Button buttonEdit = convertView.findViewById(R.id.buttonEdit);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        textViewTaskTitle.setText(task.getTitle());
        textViewStartTime.setText(task.getStartTime());

        buttonEdit.setOnClickListener(v -> {
            if (context instanceof TaskOfDate) {
                ((TaskOfDate) context).startActivityForResult(
                        new android.content.Intent(context, EditTaskActivity.class)
                                .putExtra("taskId", task.getId())
                                .putExtra("taskTitle", task.getTitle())
                                .putExtra("taskStartTime", task.getStartTime())
                                .putExtra("taskDate", task.getDate()),
                        2
                );
            }
            if (context instanceof HomeActivity) {
                ((HomeActivity) context).startActivityForResult(
                        new android.content.Intent(context, EditTaskActivity.class)
                                .putExtra("taskId", task.getId())
                                .putExtra("taskTitle", task.getTitle())
                                .putExtra("taskStartTime", task.getStartTime())
                                .putExtra("taskDate", task.getDate()),
                        2
                );
            }
        });

        buttonDelete.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa nhiệm vụ \"" + task.getTitle() + "\" không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Nếu người dùng chọn "Có", tiến hành xóa
                        if (context instanceof TaskOfDate) {
                            ((TaskOfDate) context).deleteTask(task.getId());
                        }
                        if (context instanceof HomeActivity) {
                            ((HomeActivity) context).deleteTask(task.getId());
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        // Nếu người dùng chọn "Không", đóng hộp thoại
                        dialog.dismiss();
                    })
                    .setCancelable(false) // Không cho phép đóng hộp thoại bằng nút Back
                    .show();
        });

        return convertView;
    }
}