package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class My_Profile extends AppCompatActivity {


    Button delete, logout;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        delete = findViewById(R.id.btnDelete);
        logout = findViewById(R.id.btnSingOut);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String emailUser = "";

        if(currentUser!=null){
            emailUser = currentUser.getEmail();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(My_Profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUser.delete().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(My_Profile.this, Register.class );
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });


    }
}