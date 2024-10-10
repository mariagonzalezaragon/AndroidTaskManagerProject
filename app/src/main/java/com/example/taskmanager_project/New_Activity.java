package com.example.taskmanager_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    EditText taskNameEditText, dueDateEditText;
    Spinner statusSpinner;
    Button addButton;
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        taskNameEditText = findViewById(R.id.et_task);
        dueDateEditText = findViewById(R.id.et_dueDate);
        statusSpinner = findViewById(R.id.et_Status);
        addButton = findViewById(R.id.Add);
        recyclerView = findViewById(R.id.recycler_view);

        // Setup DatePicker for due date
        dueDateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Setup spinner for task status
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, taskRef);
        recyclerView.setAdapter(taskAdapter);

        // Add new task
        addButton.setOnClickListener(v -> {
            String taskName = taskNameEditText.getText().toString();
            String dueDate = dueDateEditText.getText().toString();
            String status = statusSpinner.getSelectedItem().toString();

            Task task = new Task(taskName, dueDate, status);
            taskRef.push().setValue(task);  // Save to Firebase
        });

        // Listen for changes in Firebase
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList.clear(); // Clear the list before updating
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    task.setId(snapshot.getKey());  // Set the task ID from Firebase
                    taskList.add(task);
                }
                taskAdapter.notifyDataSetChanged(); // Notify the adapter after the data is updated
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });

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
}
