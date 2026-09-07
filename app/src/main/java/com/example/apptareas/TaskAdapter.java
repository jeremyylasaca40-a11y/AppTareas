package com.example.apptareas;



import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onStatusClick(Task task);
    }

    public TaskAdapter(Context context, List<Task> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());

        String statusText = task.getStatus().replace("_", " ").toUpperCase();
        holder.tvStatus.setText(statusText);

        holder.tvDueDate.setText("📅 Vence: " + (task.getDueDate() != null ? task.getDueDate() : "Sin fecha"));
        holder.tvAssignedUser.setText("👤 " + (task.getAssignedUser() != null ? task.getAssignedUser() : "Sin asignar"));
        holder.tvCreatedDate.setText("Creada: " + task.getCreatedDate());

        // Aplicar estilo según estado
        applyStatusStyle(holder, task.getStatus());

        // Aplicar color según prioridad (NUEVO)
        applyPriorityColor(holder, task.getPriority());

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(task));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(task));
        holder.btnStatus.setOnClickListener(v -> listener.onStatusClick(task));
    }

    private void applyStatusStyle(TaskViewHolder holder, String status) {
        int badgeRes, textColor;

        switch (status) {
            case "completada":
                badgeRes = R.drawable.badge_completada;
                textColor = ContextCompat.getColor(context, R.color.status_completada);
                break;
            case "en_progreso":
                badgeRes = R.drawable.badge_progreso;
                textColor = ContextCompat.getColor(context, R.color.status_progreso);
                break;
            default:
                badgeRes = R.drawable.badge_pendiente;
                textColor = ContextCompat.getColor(context, R.color.status_pendiente);
                break;
        }

        holder.tvStatus.setBackgroundResource(badgeRes);
        holder.tvStatus.setTextColor(textColor);
    }

    // Método nuevo para el color de prioridad
    private void applyPriorityColor(TaskViewHolder holder, String priority) {
        int color;
        if (priority != null) {
            switch (priority.toLowerCase()) {
                case "alta":
                    color = ContextCompat.getColor(context, R.color.priority_alta);
                    break;
                case "baja":
                    color = ContextCompat.getColor(context, R.color.priority_baja);
                    break;
                default: // media
                    color = ContextCompat.getColor(context, R.color.priority_media);
                    break;
            }
        } else {
            color = ContextCompat.getColor(context, R.color.priority_media);
        }
        holder.viewPriority.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateList(List<Task> newList) {
        taskList = newList;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvStatus, tvDueDate, tvAssignedUser, tvCreatedDate;
        ImageButton btnEdit, btnDelete, btnStatus;
        View viewPriority; // NUEVO

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvAssignedUser = itemView.findViewById(R.id.tvAssignedUser);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnStatus = itemView.findViewById(R.id.btnStatus);
            viewPriority = itemView.findViewById(R.id.viewPriority); // NUEVO
        }
    }
}