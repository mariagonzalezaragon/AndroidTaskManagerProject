package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private String selectedRole;
    private String photoUrl = "https://example.com/profile.jpg";
    Button registerButton, loginregButton;
    List<String> rolesList;
    private DatabaseReference userDatabase, rolesDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");
        rolesDatabase = FirebaseDatabase.getInstance().getReference("Roles");
        firebaseAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordregister);
        registerButton = findViewById(R.id.registerbtn);
        loginregButton = findViewById(R.id.loginregbtn);

        rolesList = new ArrayList<>();
        Spinner spinner = findViewById(R.id.spinner);

        loadRoles(spinner);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        loginregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadRoles(Spinner spinner) {
        rolesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rolesList.add("Select a role");
                for (DataSnapshot roleSnapshot : snapshot.getChildren()) {
                    String roleName = roleSnapshot.child("roleName").getValue(String.class);
                    if (roleName != null) {
                        rolesList.add(roleName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Register.this, android.R.layout.simple_spinner_item, rolesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Register.this, "Error loading roles", Toast.LENGTH_SHORT).show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedRole = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                selectedRole = null;
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String userName = nameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email. Please provide a valid email address");
            emailEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password should contain at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password and confirmation password do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (selectedRole == null || "Select a role".equals(selectedRole)) {
            Toast.makeText(this, "Please select a valid role", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Register.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                    createUser(email, userName, selectedRole, photoUrl);
                    startActivity(new Intent(Register.this, Home.class));
                    finish();
                } else {
                    Toast.makeText(Register.this, "Unable to complete registration. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String userName, String selectedRole, String photoUrl) {
        String userId = firebaseAuth.getCurrentUser() != null ? firebaseAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "User ID is null. Registration may have failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(userId, email, userName, selectedRole, photoUrl);

        userDatabase.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User created in database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create user in database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
