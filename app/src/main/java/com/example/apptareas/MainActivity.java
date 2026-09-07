package com.example.apptareas;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private TaskDatabase db;
    private Spinner spinnerFilter;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private EditText etSearch;
    private ImageButton btnStatistics;
    private ImageButton btnThemeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new TaskDatabase(this);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAddTask);
        etSearch = findViewById(R.id.etSearch);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Buscador en tiempo real
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadTasks();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskFormActivity.class);
            startActivity(intent);
        });

        // Botón de estadísticas
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        // Modo Oscuro/Claro
        setupThemeToggle();
    }

    private void setupThemeToggle() {
        // Cargar preferencia guardada
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", false);

        // Aplicar tema al iniciar (usando iconos universales de Android)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            btnThemeToggle.setImageResource(android.R.drawable.ic_menu_preferences);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            btnThemeToggle.setImageResource(android.R.drawable.ic_menu_manage);
        }

        // Toggle al hacer clic
        btnThemeToggle.setOnClickListener(v -> {
            boolean currentMode = getSharedPreferences("Settings", MODE_PRIVATE)
                    .getBoolean("isDarkMode", false);
            boolean newMode = !currentMode;

            // Guardar preferencia
            getSharedPreferences("Settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isDarkMode", newMode)
                    .apply();

            // Cambiar tema e icono
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                btnThemeToggle.setImageResource(android.R.drawable.ic_menu_preferences);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                btnThemeToggle.setImageResource(android.R.drawable.ic_menu_manage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        String filter = spinnerFilter.getSelectedItem().toString();
        String searchQuery = etSearch.getText().toString().trim();
        List<Task> taskList;

        // Si hay búsqueda activa
        if (!searchQuery.isEmpty()) {
            taskList = db.searchTasks(searchQuery);
            // Aplicar filtro adicional si no es "Todas"
            if (!filter.equals("Todas")) {
                String status = filter.toLowerCase().replace(" ", "_");
                List<Task> filteredList = new java.util.ArrayList<>();
                for (Task task : taskList) {
                    if (task.getStatus().equals(status)) {
                        filteredList.add(task);
                    }
                }
                taskList = filteredList;
            }
        } else {
            // Sin búsqueda, solo filtro
            if (filter.equals("Todas")) {
                taskList = db.getAllTasks();
            } else {
                String status = filter.toLowerCase().replace(" ", "_");
                taskList = db.getTasksByStatus(status);
            }
        }

        if (taskList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (!searchQuery.isEmpty()) {
                tvEmpty.setText("\n\nNo se encontraron tareas\ncon \"" + searchQuery + "\"");
            } else {
                tvEmpty.setText("📝\n\nNo hay tareas\n¡Crea la primera!");
            }
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new TaskAdapter(this, taskList, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onEditClick(Task task) {
        Intent intent = new Intent(this, TaskFormActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de eliminar \"" + task.getTitle() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    db.deleteTask(task.getId());
                    loadTasks();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onStatusClick(Task task) {
        String[] options = {"pendiente", "en_progreso", "completada"};
        new AlertDialog.Builder(this)
                .setTitle("Cambiar estado")
                .setItems(options, (dialog, which) -> {
                    task.setStatus(options[which]);
                    db.updateTask(task);
                    loadTasks();
                })
                .show();
    }
}