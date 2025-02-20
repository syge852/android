package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;
import java.util.Calendar;
import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";
    private static final String LOG_TAG = "AddNewTask";
    private EditText newTaskText, newTaskDescription, newTaskDueDate, newTaskDueTime;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow())
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        newTaskDueDate.setOnClickListener(v -> showDatePickerDialog());
        newTaskDueTime.setOnClickListener(v -> showTimePickerDialog());
        boolean isUpdate = loadTaskDetails();
        db = new DatabaseHandler(getActivity());
        db.openDatabase();
        setupSaveButtonLogic(isUpdate);
    }
    private void initializeViews(View view) {
        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskDescription = view.findViewById(R.id.newTaskDescription);
        newTaskDueDate = view.findViewById(R.id.newTaskDueDate);
        newTaskDueTime = view.findViewById(R.id.newTaskDueTime);
        newTaskSaveButton = view.findViewById(R.id.newTaskButton);
    }

    private boolean loadTaskDetails() {
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            String description = bundle.getString("description");
            String dueDate = bundle.getString("dueDate");
            String dueTime = bundle.getString("dueTime");
            newTaskText.setText(task);
            newTaskDescription.setText(description);
            newTaskDueDate.setText(dueDate);
            newTaskDueTime.setText(dueTime);
            if (task != null && !task.isEmpty()) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
            }
        }
        return isUpdate;
    }

    private void setupSaveButtonLogic(boolean isUpdate) {
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(v -> saveTask(finalIsUpdate));
    }
    private void saveTask(boolean isUpdate) {
        String text = newTaskText.getText().toString();
        String description = newTaskDescription.getText().toString();
        String dueDate = newTaskDueDate.getText().toString();
        String dueTime = newTaskDueTime.getText().toString();

        if (text.isEmpty() || dueDate.isEmpty() || dueTime.isEmpty()) {
            Toast.makeText(getContext(), "Task title, due date, and time are required", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (isUpdate) {
                db.updateTask(getArguments().getInt("id"), text, description, dueDate, dueTime);
                Log.d(LOG_TAG, "Task updated: " + text);
            } else {
                ToDoModel task = new ToDoModel();
                task.setTask(text);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setDueTime(dueTime);
                task.setStatus(0);
                db.insertTask(task);
                Log.d(LOG_TAG, "Task inserted: " + text);
            }
            dismiss();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error saving task", e);
            Toast.makeText(getContext(), "Error saving task", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDismiss( DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String dueDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    newTaskDueDate.setText(dueDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    String dueTime = String.format("%02d:%02d", hourOfDay, minute);
                    newTaskDueTime.setText(dueTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }
}