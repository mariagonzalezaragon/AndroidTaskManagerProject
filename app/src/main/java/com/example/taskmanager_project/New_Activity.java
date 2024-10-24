package com.example.taskmanager_project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class New_Activity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference taskRef = database.getReference("tasks");
    DatabaseReference userRef = database.getReference("users");

    EditText taskNameEditText, dueDateEditText;
    Spinner statusSpinner, userSpinner;
    Button addButton,go_to_home;
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    List<Task> taskList = new ArrayList<>();
    List<User> userList = new ArrayList<>();
    List<String> userNames = new ArrayList<>();
    private FirebaseAuth auth;
    private String userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        auth = FirebaseAuth.getInstance();
        taskNameEditText = findViewById(R.id.et_task);
        dueDateEditText = findViewById(R.id.et_dueDate);
        statusSpinner = findViewById(R.id.status_spinner);
        userSpinner = findViewById(R.id.user_spinner);
        addButton = findViewById(R.id.add_button);
        recyclerView = findViewById(R.id.recycler_view);
        go_to_home = findViewById(R.id.go_to_home);

        loadCurrentUserRole();

        go_to_home.setOnClickListener(view -> {
            Intent intent = new Intent(New_Activity.this,
                    Home.class);
            startActivity(intent);
            finish();
        });

        dueDateEditText.setOnClickListener(v -> showDatePickerDialog());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, taskRef);  // Pass the taskRef here
        recyclerView.setAdapter(taskAdapter);

        addButton.setOnClickListener(v -> {
            String taskName = taskNameEditText.getText().toString();
            String dueDate = dueDateEditText.getText().toString();
            String status = statusSpinner.getSelectedItem().toString();

            int selectedUserPosition = userSpinner.getSelectedItemPosition();
            User selectedUser = userList.get(selectedUserPosition);

            String userId = selectedUser.getUserId();
            String userName = selectedUser.getUserName();
            if ("Project Manager".equals(userRole) || "Product Owner".equals(userRole)) {
                Task task = new Task(taskName, dueDate, status, userId, userName);
                taskRef.push().setValue(task).addOnCompleteListener(taskSave -> {
                    if (taskSave.isSuccessful()) {

                        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show();

                        taskNameEditText.setText("");
                        dueDateEditText.setText("");
                        statusSpinner.setSelection(0);
                        userSpinner.setSelection(0);
                    } else {
                        Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
                    }
                });  // Save to Firebase



            }else {
                // Handle the case where the user does not have the right to add tasks
                Toast.makeText(this, "You do not have permission to add tasks.", Toast.LENGTH_SHORT).show();
            }

        });

        // Load users from Firebase
        loadUsersFromFirebase();

        // Listen for changes in Firebase for tasks
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear(); // Clear the list before updating
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        task.setId(snapshot.getKey());  // Set the task ID from Firebase
                        taskList.add(task);
                    }
                }
                taskAdapter.notifyDataSetChanged(); // Notify the adapter after the data is updated
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }
    private void loadCurrentUserRole() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        userRole = snapshot.child("role").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(New_Activity.this, "Failed to load user role", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // DatePicker dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dueDateEditText.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void loadUsersFromFirebase() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                userNames.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                        userNames.add(user.getUserName());
                    }
                }

                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(New_Activity.this,
                        android.R.layout.simple_spinner_item, userNames);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
