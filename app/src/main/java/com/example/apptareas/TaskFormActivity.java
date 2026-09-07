package com.example.apptareas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class TaskFormActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate, etAssignedUser;
    private Spinner spinnerStatus, spinnerPriority; // Agregado spinnerPriority
    private Button btnSave, btnCancel, btnPickDate;
    private TextView tvFormTitle;
    private TaskDatabase db;
    private Task task;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_form);

        db = new TaskDatabase(this);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDueDate = findViewById(R.id.etDueDate);
        etAssignedUser = findViewById(R.id.etAssignedUser);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerPriority = findViewById(R.id.spinnerPriority); // Inicializado
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnPickDate = findViewById(R.id.btnPickDate);
        tvFormTitle = findViewById(R.id.tvFormTitle);

        // Adapter para Estado
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Adapter para Prioridad (NUEVO)
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);

        int taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId != -1) {
            isEditMode = true;
            task = db.getTask(taskId);
            tvFormTitle.setText("Editar Tarea");

            etTitle.setText(task.getTitle());
            etDescription.setText(task.getDescription());
            etDueDate.setText(task.getDueDate());
            etAssignedUser.setText(task.getAssignedUser());

            // Seleccionar estado actual
            String[] statuses = getResources().getStringArray(R.array.status_array);
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].replace(" ", "_").toLowerCase().equals(task.getStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }

            // Seleccionar prioridad actual (NUEVO)
            String[] priorities = getResources().getStringArray(R.array.priority_array);
            String taskPriority = task.getPriority() != null ? task.getPriority() : "media";
            for (int i = 0; i < priorities.length; i++) {
                if (priorities[i].toLowerCase().equals(taskPriority)) {
                    spinnerPriority.setSelection(i);
                    break;
                }
            }
        } else {
            task = new Task();
            tvFormTitle.setText("Nueva Tarea");
            // Por defecto, seleccionar "Media" (índice 1)
            spinnerPriority.setSelection(1);
        }

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDueDate.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String assignedUser = etAssignedUser.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString().replace(" ", "_").toLowerCase();
        String priority = spinnerPriority.getSelectedItem().toString().toLowerCase(); // NUEVO

        if (title.isEmpty()) {
            etTitle.setError("El título es obligatorio");
            etTitle.requestFocus();
            return;
        }

        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setAssignedUser(assignedUser);
        task.setStatus(status);
        task.setPriority(priority); // NUEVO

        if (isEditMode) {
            db.updateTask(task);
            Toast.makeText(this, "✅ Tarea actualizada", Toast.LENGTH_SHORT).show();
        } else {
            db.addTask(task);
            Toast.makeText(this, "✅ Tarea creada", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
