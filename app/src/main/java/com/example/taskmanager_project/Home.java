package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Home extends AppCompatActivity {

    Button editProfile, taskManagement, dashboard, logout;
    TextView textName, infoText, positiontxt;
    ImageView imgProfile;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
    String emailUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editProfile = findViewById(R.id.btnEditProfile);
        taskManagement = findViewById(R.id.btnTasks);
        dashboard = findViewById(R.id.btnDashboard);
        logout = findViewById(R.id.logout);
        textName = findViewById(R.id.nametxt);
        infoText = findViewById(R.id.personaltxt);
        imgProfile = findViewById(R.id.topImage);
        positiontxt = findViewById(R.id.positiontxt);


        if (currentUser != null) {
            emailUser = currentUser.getEmail();

            userDatabase.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String userName = dataSnapshot.child("userName").getValue(String.class);
                        String photoUrl = dataSnapshot.child("photoUrl").getValue(String.class);
                        String role = dataSnapshot.child("role").getValue(String.class);

                        if (photoUrl != null) {
                            new SaveImageHelper(imgProfile).loadImage(photoUrl);
                        }

                        textName.setText("Welcome " + (userName != null ? userName : "Guest"));
                        infoText.setText("Email: " + (email != null ? email : "Not provided"));
                        positiontxt.setText("Position: " + (role != null ? role : "Not provided"));
                    } else {
                        textName.setText("Error fetching user data.");
                    }
                }
            });

        } else {
            emailUser = "Failed, try again later";
            textName.setText("User not logged in.");
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

        taskManagement.setOnClickListener(new View.OnClickListener() {
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