package com.example.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.todolist.Model.ToDoModel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";
    private static final int VERSION = 3;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String DUE_DATE = "due_date";
    private static final String DUE_TIME = "due_time";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + DESCRIPTION + " TEXT, "
            + DUE_DATE + " TEXT, "
            + DUE_TIME + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TODO_TABLE);
            Log.d(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE " + TODO_TABLE + " ADD COLUMN " + DESCRIPTION + " TEXT");
                db.execSQL("ALTER TABLE " + TODO_TABLE + " ADD COLUMN " + DUE_DATE + " TEXT");
            }
            if (oldVersion < 3) { // Upgrade to version 3
                db.execSQL("ALTER TABLE " + TODO_TABLE + " ADD COLUMN " + DUE_TIME + " TEXT");
                Log.d(TAG, "Database upgraded: Added due_time column");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
        }
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
        Log.d(TAG, "Database opened");
    }

    public void insertTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(TASK, task.getTask());
            cv.put(DESCRIPTION, task.getDescription());
            cv.put(DUE_DATE, task.getDueDate());
            cv.put(DUE_TIME, task.getDueTime()); // Insert due_time
            cv.put(STATUS, 0);
            db.insert(TODO_TABLE, null, cv);
            Log.d(TAG, "Task inserted: " + task.getTask());
        } catch (Exception e) {
            Log.e(TAG, "Error inserting task", e);
        } finally {
            db.close();
        }
    }

    public void updateTask(int id, String task, String description, String dueDate, String dueTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(TASK, task);
            values.put(DESCRIPTION, description);
            values.put(DUE_DATE, dueDate);
            values.put(DUE_TIME, dueTime);
            db.update(TODO_TABLE, values, ID + " = ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Task updated: ID=" + id);
        } catch (Exception e) {
            Log.e(TAG, "Error updating task", e);
        } finally {
            db.close();
        }
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TODO_TABLE, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                    task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(TASK)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                    task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE)));
                    task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(DUE_TIME)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(STATUS)));
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving tasks", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put(STATUS, status);
            db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Status updated for task ID=" + id);
        } catch (Exception e) {
            Log.e(TAG, "Error updating status", e);
        } finally {
            db.close();
        }
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
            Log.d(TAG, "Task deleted: ID=" + id);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting task", e);
        } finally {
            db.close();
        }
    }
}
