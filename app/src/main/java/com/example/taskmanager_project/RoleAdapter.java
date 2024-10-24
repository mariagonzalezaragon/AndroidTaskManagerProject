package com.example.taskmanager_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RoleAdapter extends android.widget.ArrayAdapter<Role> {

    private Context context;
    private List<Role> roleList;
    private DatabaseReference rolesDatabase;
    private DatabaseReference usersDatabase;

    public RoleAdapter(@NonNull Context context, List<Role> roleList) {
        super(context, R.layout.role_item, roleList);
        this.context = context;
        this.roleList = roleList;
        this.rolesDatabase = FirebaseDatabase.getInstance().getReference("Roles");
        this.usersDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.role_item, parent, false);
        }

        Role role = roleList.get(position);

        TextView textViewRoleName = convertView.findViewById(R.id.textViewRoleName);
        EditText editTextRoleDescription = convertView.findViewById(R.id.editTextRoleDescription);
        ImageButton buttonDeleteRole = convertView.findViewById(R.id.buttonDeleteRole);
        ImageButton buttonEditRole = convertView.findViewById(R.id.buttonEditRole);

        textViewRoleName.setText(role.getRoleName());
        editTextRoleDescription.setText(role.getRoleDescription());

        buttonDeleteRole.setOnClickListener(view -> checkIfRoleAssignedToUsers(role));

        buttonEditRole.setOnClickListener(view -> {
            String updatedName = textViewRoleName.getText().toString();
            String updatedDescription = editTextRoleDescription.getText().toString();

            Role updatedRole = new Role(role.getRoleId(), updatedName, updatedDescription, role.isFullAccess());
            rolesDatabase.child(role.getRoleId()).setValue(updatedRole).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Role updated", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Failed to update role", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return convertView;
    }

    private void checkIfRoleAssignedToUsers(Role role) {
        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAssigned = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userRole = snapshot.child("role").getValue(String.class);
                    if (userRole != null && userRole.equals(role.getRoleName())) {
                        isAssigned = true;
                        break;
                    }
                }

                if (isAssigned) {
                    Toast.makeText(context, "Cannot delete role. It's assigned to a user.", Toast.LENGTH_SHORT).show();
                } else {
                    deleteRole(role);
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(context, "Error checking role assignments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRole(Role role) {
        rolesDatabase.child(role.getRoleId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Role deleted", Toast.LENGTH_SHORT).show();
                roleList.remove(role);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Failed to delete role", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
