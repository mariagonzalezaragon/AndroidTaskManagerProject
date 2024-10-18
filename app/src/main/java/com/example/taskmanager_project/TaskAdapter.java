package com.example.taskmanager_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private DatabaseReference taskRef;

    public TaskAdapter(List<Task> taskList, DatabaseReference taskRef) {
        this.taskList = taskList;
        this.taskRef = taskRef;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskNameTextView.setText(task.getTaskName());
        holder.dueDateTextView.setText(task.getDueDate());
        holder.userTextView.setText(task.getUserName()); // Add this line to display the user's name

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(holder.itemView.getContext(),
                R.array.task_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.statusSpinner.setAdapter(adapter);
        holder.statusSpinner.setSelection(getStatusPosition(task.getStatus()));

        holder.saveButton.setOnClickListener(v -> {
            task.setStatus(holder.statusSpinner.getSelectedItem().toString());
            taskRef.child(task.getId()).setValue(task);
        });

        holder.deleteButton.setOnClickListener(v -> {
            // Delete the task from Firebase
            taskRef.child(task.getId()).removeValue().addOnCompleteListener(taskDelete -> {
                if (taskDelete.isSuccessful()) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        taskList.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(holder.itemView.getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView, dueDateTextView, userTextView; // Add userTextView here
        Spinner statusSpinner;
        Button saveButton, deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            userTextView = itemView.findViewById(R.id.userTextView); // Initialize userTextView here
            statusSpinner = itemView.findViewById(R.id.statusSpinner);
            saveButton = itemView.findViewById(R.id.saveButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private int getStatusPosition(String status) {
        switch (status) {
            case "In Progress":
                return 1;
            case "Done":
                return 2;
            default:
                return 0;
        }
    }
}

