package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Manage_User_Activity extends AppCompatActivity {

    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private Button btnAddUser, btnHome;
    private DatabaseReference databaseReference;
    private ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnHome = findViewById(R.id.btnHome);

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        loadUsersFromDatabase();

        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(Manage_User_Activity.this, Register.class);
            startActivity(intent);
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(Manage_User_Activity.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUsersFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Manage_User_Activity", "Error al cargar usuarios",
                        databaseError.toException());
            }
        });
    }
}
