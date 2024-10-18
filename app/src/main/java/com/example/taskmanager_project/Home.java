package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    Button editProfile, taskManagment, users, roles, logout;
    Spinner spinner;
    TextView textName, infoText;
    ImageView imgProfile;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userDatabase;
    private String currentUserRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        editProfile = findViewById(R.id.btnEditProfile);
        taskManagment = findViewById(R.id.btnTasks);
        users = findViewById(R.id.btnUsers);
        roles = findViewById(R.id.btnRoles);
        logout = findViewById(R.id.logout);
        //spinner = findViewById(R.id.spinner);
        textName = findViewById(R.id.nametxt);
        infoText = findViewById(R.id.personaltxt);
        imgProfile = findViewById(R.id.topImage);

        users.setVisibility(View.GONE);
        roles.setVisibility(View.GONE);
        if (currentUser != null) {
            loadUserRole();
        }


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
            Intent intent = new Intent(Home.this,
                    My_Profile.class);
            startActivity(intent);
            finish();
            }
        });

        taskManagment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,
                        New_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,
                        Manage_User_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        roles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,
                        Roles_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void loadUserRole() {
        String userId = currentUser.getUid();

        userDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserRole = snapshot.child("role").getValue(String.class); // Save the role

                    assert currentUserRole != null;
                    if (currentUserRole.equals("Project Manager")) {
                        users.setVisibility(View.VISIBLE);
                        roles.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCurrentUserRole() {
        return currentUserRole; // Add this method to access the role
    }
}