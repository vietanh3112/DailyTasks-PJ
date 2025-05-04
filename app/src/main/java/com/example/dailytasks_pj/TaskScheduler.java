package com.example.dailytasks_pj;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskScheduler {

    private final Context context;
    private final DatabaseHelper dbHelper;
    private final AlarmManager alarmManager;

    public TaskScheduler(Context context) {
        // Sử dụng Application Context thay vì Activity Context
        this.context = context.getApplicationContext();
        this.dbHelper = new DatabaseHelper(this.context);
        this.alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    }

    // Lập lịch thông báo cho tất cả nhiệm vụ
    public void scheduleAllTaskNotifications() {
        Cursor cursor = dbHelper.getAllTasks();
        if (cursor == null || !cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        do {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            scheduleTaskNotification(id, title, startTime, date);
        } while (cursor.moveToNext());

        cursor.close();
    }

    // Lập lịch thông báo cho một nhiệm vụ
    public void scheduleTaskNotification(int taskId, String taskTitle, String taskStartTime, String taskDate) {
        try {
            // Chuyển đổi date và startTime thành thời gian (milliseconds)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date taskDateTime = dateFormat.parse(taskDate + " " + taskStartTime);

            if (taskDateTime == null) {
                return; // Không hiển thị Toast vì không có activity context
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(taskDateTime);

            // Nếu thời gian đã qua, không lập lịch
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                return;
            }

            // Tạo Intent cho BroadcastReceiver
            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.setAction("com.example.dailytasks_pj.TASK_NOTIFICATION");
            intent.putExtra("taskId", taskId);
            intent.putExtra("taskTitle", taskTitle);
            intent.putExtra("taskStartTime", taskStartTime);
            intent.putExtra("taskDate", taskDate);

            // Tạo PendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );

            // Lập lịch với AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Không hiển thị Toast vì không có activity context
        }
    }

    // Hủy thông báo của một nhiệm vụ
    public void cancelTaskNotification(int taskId) {
        try {
            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.setAction("com.example.dailytasks_pj.TASK_NOTIFICATION");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
            );

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}