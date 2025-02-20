package com.example.todolist.Notifications;
import android.app.*;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.Utils.DatabaseHandler;
import java.util.Calendar;
import java.util.List;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "todo_channel";
    private static final int NOTIFICATION_ID = 1;
    private final Handler handler = new Handler();
    private final Runnable taskChecker = new Runnable() {
        @Override
        public void run() {
            checkUpcomingTasks();
            handler.postDelayed(this, 30 * 60 * 1000); // Use `this` instead of `taskChecker`
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createPersistentNotification());
        handler.post(taskChecker);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            stopSelf();
        }).start();
        return START_NOT_STICKY;
    }
    private void checkUpcomingTasks() {
        DatabaseHandler db = new DatabaseHandler(this);
        db.openDatabase();
        List<ToDoModel> tasks = db.getAllTasks();

        Calendar now = Calendar.getInstance();
        Calendar soon = (Calendar) now.clone();
        soon.add(Calendar.HOUR, 1);

        for (ToDoModel task : tasks) {
            Calendar dueDateTime = parseDueDate(task.getDueDate(), task.getDueTime());
            if (dueDateTime != null && dueDateTime.after(now) && dueDateTime.before(soon)) {
                sendNotification(task);
            }
        }
    }

    private Calendar parseDueDate(String dueDate, String dueTime) {
        try {
            String[] dateParts = dueDate.split("/");
            String[] timeParts = dueTime.replaceAll("[^0-9:APM]", "").split(":");

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1;
            int year = Integer.parseInt(dateParts[2]);
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            if (dueTime.toUpperCase().contains("PM") && hour < 12) hour += 12;
            if (dueTime.toUpperCase().contains("AM") && hour == 12) hour = 0;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0);
            return calendar;
        } catch (Exception e) {
            return null;
        }
    }
    private void sendNotification(ToDoModel task) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Task Reminder")
                .setContentText(task.getTask() + " is due soon!")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(task.getId(), notification);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "To-Do Notifications", NotificationManager.IMPORTANCE_HIGH);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private Notification createPersistentNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("To-Do List")
                .setContentText("Checking for upcoming tasks...")
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(taskChecker);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
