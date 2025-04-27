package com.example.dailytasks_pj;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {

    private final Context context;
    private final ArrayList<Task> tasks;

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        }

        Task task = tasks.get(position);

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        Button buttonEdit = convertView.findViewById(R.id.buttonEdit);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        textViewTitle.setText(task.getTitle());
        textViewTime.setText(task.getStartTime());

        // Xử lý nút Sửa
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("taskStartTime", task.getStartTime());
            intent.putExtra("taskDate", task.getDate());
            context.startActivity(intent);
        });

        // Xử lý nút Xóa
        buttonDelete.setOnClickListener(v -> {
            if (context instanceof TaskOfDate) {
                ((TaskOfDate) context).deleteTask(task.getId());
            }
            if (context instanceof HomeActivity) {
                ((HomeActivity) context).deleteTask(task.getId());
            }
        });

        return convertView;
    }
}