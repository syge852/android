package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {
    private TextView taskTitle, taskDescription, taskDueDate, taskDueTime;
    public static final String EXTRA_TASK = "task";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_DUE_DATE = "dueDate";
    public static final String EXTRA_DUE_TIME = "dueTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        taskTitle = findViewById(R.id.taskTitle);
        taskDescription = findViewById(R.id.taskDescription);
        taskDueDate = findViewById(R.id.taskDueDate);
        taskDueTime = findViewById(R.id.newTaskDueTime);

        if (getIntent() != null) {
            String title = getIntent().getStringExtra(EXTRA_TASK);
            String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
            String dueDate = getIntent().getStringExtra(EXTRA_DUE_DATE);
            String dueTime = getIntent().getStringExtra(EXTRA_DUE_TIME);

            taskTitle.setText(title != null ? title : "No Title");
            taskDescription.setText(description != null ? description : "No Description");
            taskDueDate.setText(dueDate != null ? dueDate : "No Due Date");
            taskDueTime.setText(dueTime != null ? dueTime : "No Due Time");
        }
    }
    public void onBackPressed(View view) {
        finish();
    }
}
