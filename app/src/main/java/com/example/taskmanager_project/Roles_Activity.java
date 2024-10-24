package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Roles_Activity extends AppCompatActivity {

    private ListView rolesListView;
    private EditText editTextRoleName, editTextRoleDescription;
    private Button buttonAddRole, buttonBack;
    private List<Role> roleList;
    private RoleAdapter roleAdapter;
    private DatabaseReference rolesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);

        rolesListView = findViewById(R.id.rolesListView);
        editTextRoleName = findViewById(R.id.editTextRoleName);
        editTextRoleDescription = findViewById(R.id.editTextRoleDescription);
        buttonAddRole = findViewById(R.id.buttonAddRole);
        buttonBack = findViewById(R.id.buttonBack);

        roleList = new ArrayList<>();
        roleAdapter = new RoleAdapter(this, roleList);
        rolesListView.setAdapter(roleAdapter);

        rolesDatabase = FirebaseDatabase.getInstance().getReference("Roles");

        loadRoles();

        buttonAddRole.setOnClickListener(view -> addRole());

        buttonBack.setOnClickListener(view -> {
            Intent intent = new Intent(Roles_Activity.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadRoles() {
        rolesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roleList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Role role = snapshot.getValue(Role.class);
                    roleList.add(role);
                }
                roleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Roles_Activity.this, "Error loading roles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addRole() {
        String roleName = editTextRoleName.getText().toString().trim();
        String roleDescription = editTextRoleDescription.getText().toString().trim();

        if (roleName.isEmpty() || roleDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String roleId = rolesDatabase.push().getKey();
        Role newRole = new Role(roleId, roleName, roleDescription, true);

        rolesDatabase.child(roleId).setValue(newRole).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Roles_Activity.this, "Role added", Toast.LENGTH_SHORT).show();
                editTextRoleName.setText("");
                editTextRoleDescription.setText("");
            } else {
                Toast.makeText(Roles_Activity.this, "Failed to add role", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
