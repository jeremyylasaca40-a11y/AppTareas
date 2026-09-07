package com.example.apptareas;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvTotal, tvPendientes, tvProgreso, tvCompletadas, tvVencidas;
    private TextView tvPorcentaje, tvPorcentajeLabel;
    private View progressBar;
    private ImageButton btnBack;
    private TaskDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        db = new TaskDatabase(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvPendientes = findViewById(R.id.tvPendientes);
        tvProgreso = findViewById(R.id.tvProgreso);
        tvCompletadas = findViewById(R.id.tvCompletadas);
        tvVencidas = findViewById(R.id.tvVencidas);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        tvPorcentajeLabel = findViewById(R.id.tvPorcentajeLabel);
        progressBar = findViewById(R.id.progressBar);

        // Configurar el botón de regreso
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadStatistics();
    }

    private void loadStatistics() {
        List<Task> allTasks = db.getAllTasks();
        List<Task> pendientes = db.getTasksByStatus("pendiente");
        List<Task> enProgreso = db.getTasksByStatus("en_progreso");
        List<Task> completadas = db.getTasksByStatus("completada");

        int total = allTasks.size();
        int countPendientes = pendientes.size();
        int countProgreso = enProgreso.size();
        int countCompletadas = completadas.size();

        // Calcular tareas vencidas
        int vencidas = 0;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (Task task : allTasks) {
            if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
                if (task.getDueDate().compareTo(today) < 0 && !task.getStatus().equals("completada")) {
                    vencidas++;
                }
            }
        }

        // Calcular porcentaje
        int porcentaje = total > 0 ? (countCompletadas * 100) / total : 0;

        // Actualizar UI
        tvTotal.setText(String.valueOf(total));
        tvPendientes.setText(String.valueOf(countPendientes));
        tvProgreso.setText(String.valueOf(countProgreso));
        tvCompletadas.setText(String.valueOf(countCompletadas));
        tvVencidas.setText(String.valueOf(vencidas));
        tvPorcentaje.setText(porcentaje + "%");

        // Mensaje motivacional
        if (porcentaje >= 70) {
            tvPorcentajeLabel.setText("¡Excelente trabajo!");
        } else if (porcentaje >= 40) {
            tvPorcentajeLabel.setText("Vas bien");
        } else {
            tvPorcentajeLabel.setText("¡Sigue adelante!");
        }

        // Actualizar barra de progreso
        ViewGroup.LayoutParams params = progressBar.getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxWidth = (int) (screenWidth * 0.8);
        params.width = (int) (maxWidth * porcentaje / 100);
        progressBar.setLayoutParams(params);

        // Color de la barra
        if (porcentaje >= 70) {
            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.status_completada));
        } else if (porcentaje >= 40) {
            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.status_progreso));
        } else {
            progressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.status_pendiente));
        }
    }
}
