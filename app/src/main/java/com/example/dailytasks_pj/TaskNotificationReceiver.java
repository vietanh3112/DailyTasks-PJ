package com.example.dailytasks_pj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "TaskReminderChannel";
    private static final String CHANNEL_NAME = "Task Reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Lấy thông tin nhiệm vụ từ Intent
        String taskTitle = intent.getStringExtra("taskTitle");
        String taskStartTime = intent.getStringExtra("taskStartTime");
        String taskDate = intent.getStringExtra("taskDate");
        int taskId = intent.getIntExtra("taskId", 0);

        // Tạo thông báo
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo kênh thông báo (yêu cầu cho Android 8.0 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for task reminders");
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo nội dung thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Nhiệm vụ: " + taskTitle)
                .setContentText("Thời gian: " + taskStartTime + " ngày " + taskDate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Hiển thị thông báo
        notificationManager.notify(taskId, builder.build());
    }
}