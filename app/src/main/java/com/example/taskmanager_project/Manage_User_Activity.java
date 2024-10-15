package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Manage_User_Activity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private Button btnAddUser;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        recyclerView = findViewById(R.id.recycler_view_users);
        btnAddUser = findViewById(R.id.btnAddUser);

        userDatabase = FirebaseDatabase.getInstance().getReference("users");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        recyclerView.setAdapter(userAdapter);

        loadUsersFromFirebase();

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(Manage_User_Activity.this, Register.class);
            startActivity(intent);
        });
    }

    private void loadUsersFromFirebase() {
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (user != null) {
                        userList.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error loading users: " + databaseError.getMessage());
            }
        });
    }
}
