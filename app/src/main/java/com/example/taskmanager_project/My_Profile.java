package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class My_Profile extends AppCompatActivity {


    EditText txtProfName, profOldPass, profNewPass, profConfirmPass;

    Spinner spinnerPosition;

    Button btnImage, btnSaveUser, btnDeleteAccount, btnLogout, bntSavePass;
    private String role;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String emailUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        txtProfName = findViewById(R.id.txtProfName);
        spinnerPosition = findViewById(R.id.spinnerPosition);
        profOldPass = findViewById(R.id.txtProfOldPass);
        profNewPass = findViewById(R.id.txtProfNewPass);
        profConfirmPass = findViewById(R.id.txtProfConfirmPass);
        btnImage = findViewById(R.id.btnImage);
        btnSaveUser = findViewById(R.id.btnSaveUser);
        btnDeleteAccount = findViewById(R.id.btndeleteAccount);
        btnLogout = findViewById(R.id.btnLogout);
        bntSavePass = findViewById(R.id.bntSavePass);


        if (currentUser != null) {
            emailUser = currentUser.getEmail();
        } else {
            emailUser = "Filed try again later";
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(My_Profile.this,
                R.array.roles_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);

        spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                role = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(My_Profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(My_Profile.this, Register.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        bntSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePassword();
            }
        });

    }

    private void handlePassword() {

        String oldPassword = profOldPass.getText().toString().trim();
        String newPassword = profNewPass.getText().toString().trim();
        String confirmPassword = profConfirmPass.getText().toString().trim();


        if (oldPassword.isEmpty()) {
            profOldPass.setError("Please enter your old password");
            return;
        }
        if (newPassword.isEmpty()) {
            profNewPass.setError("Please enter your new password");
            return;
        }
        if (confirmPassword.isEmpty()) {
            profConfirmPass.setError("Please confirm your new password");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(My_Profile.this, "Password confirmation do not match, try again", Toast.LENGTH_SHORT).show();

            return;
        }

        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(My_Profile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        profOldPass.setText("");
                        profOldPass.setText("");
                        profNewPass.setText("");
                        profConfirmPass.setText("");
                    } else {
                        Toast.makeText(My_Profile.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}