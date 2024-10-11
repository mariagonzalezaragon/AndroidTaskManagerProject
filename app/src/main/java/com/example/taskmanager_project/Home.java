package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button editProfile, taskManagment, dashboard, logout;
    Spinner spinner;
    TextView textName, infoText;
    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editProfile = findViewById(R.id.btnEditProfile);
        taskManagment = findViewById(R.id.btnTasks);
        dashboard = findViewById(R.id.btnDashboard);
        logout = findViewById(R.id.logout);
        //spinner = findViewById(R.id.spinner);
        textName = findViewById(R.id.nametxt);
        infoText = findViewById(R.id.personaltxt);
        imgProfile = findViewById(R.id.topImage);


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
                        ActivityDetail.class);
                startActivity(intent);
                finish();
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this,
                        Dashboard.class);
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
}