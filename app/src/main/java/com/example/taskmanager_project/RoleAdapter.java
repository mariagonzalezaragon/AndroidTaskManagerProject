package com.example.taskmanager_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {

    private List<Role> roleList;
    private OnDeleteClickListener deleteClickListener;

    public RoleAdapter(List<Role> roleList, OnDeleteClickListener deleteClickListener) {
        this.roleList = roleList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_item, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Role role = roleList.get(position);
        holder.roleName.setText(role.getRoleName());
        holder.roleDescription.setText(role.getRoleDescription());

        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(role.getRoleId());
        });
    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {

        TextView roleName, roleDescription;
        Button deleteButton;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            roleName = itemView.findViewById(R.id.role_name);
            roleDescription = itemView.findViewById(R.id.role_description);
            deleteButton = itemView.findViewById(R.id.delete_button); // Assuming you have a delete button in the layout
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(String roleId);
    }
}
