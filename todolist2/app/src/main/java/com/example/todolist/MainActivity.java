package com.example.todolist;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.todolist.Adapters.ToDoAdapter;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    private DatabaseHandler db;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = new DatabaseHandler(this);
        try {
            db.getWritableDatabase();
        } catch (Exception e) {
            return;
        }

        taskList = new ArrayList<>();
        setupRecyclerView();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));
    }

    private void setupRecyclerView() {
        RecyclerView tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksAdapter = new ToDoAdapter(db, this);
        tasksRecyclerView.setAdapter(tasksAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
        loadTasks();
    }

    private void loadTasks() {
        try {
            List<ToDoModel> loadedTasks = db.getAllTasks();
            if (loadedTasks != null && !loadedTasks.isEmpty()) {
                taskList.clear();
                taskList.addAll(loadedTasks);
                Collections.reverse(taskList);
                tasksAdapter.setTasks(taskList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        try {
            List<ToDoModel> updatedTasks = db.getAllTasks();
            if (updatedTasks != null && !updatedTasks.isEmpty()) {
                taskList.clear();
                taskList.addAll(updatedTasks);
                Collections.reverse(taskList);
                tasksAdapter.setTasks(taskList);
                tasksAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
