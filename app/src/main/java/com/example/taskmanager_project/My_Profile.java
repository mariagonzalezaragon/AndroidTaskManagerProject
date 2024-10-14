package com.example.taskmanager_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class My_Profile extends AppCompatActivity {


    EditText txtProfName, profOldPass, profNewPass, profConfirmPass;

    Spinner spinnerPosition;

    Button btnImage, btnSaveUser, btnDeleteAccount, btnLogout, bntSavePass;
    private String role;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
    String emailUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView topImage;
    private Uri imageUri;


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
        btnImage = findViewById(R.id.btnImage);
        topImage = findViewById(R.id.topImage);


        // I commented this as it was in the method loaduserdata - maria
        //if (currentUser != null) {
        //  emailUser = currentUser.getEmail();
        //} else {
        //  emailUser = "Filed try again later";
        // }

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

        loadUserData();


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


        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageChooser();
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


    private void loadUserData() {
        String userId = currentUser.getUid();

        userDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();

                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        txtProfName.setText(user.getUserName());
                        if (user.getPhotoUrl() != null) {
                            //failed attempt to use glide
                        }

                        String role = user.getRole();

                        setSpinnerRole(role);
                    }
                } else {
                    Toast.makeText(My_Profile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSpinnerRole(String role) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(adapter);


        int position = adapter.getPosition(role);
        if (position >= 0) {
            spinnerPosition.setSelection(position);
        }
    }

    private void saveUserData() {
        String userId = currentUser.getUid();

        userDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        String updatedName = txtProfName.getText().toString().trim();
                        String updatedRole = role;

                        if (updatedName.isEmpty()) {
                            txtProfName.setError("Please enter your name");
                            return;
                        }

                        if (!updatedName.equals(user.getUserName())) {
                            user.setUserName(updatedName);
                        }
                        if (!updatedRole.equals(user.getRole())) {
                            user.setRole(updatedRole);
                        }


                        if (imageUri != null) {
                            String imageUrl = imageUri.toString();
                            user.setPhotoUrl(imageUrl);
                        }

                        // Ahora se guarda el usuario actualizado en la base de datos
                        userDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(My_Profile.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(My_Profile.this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(My_Profile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            topImage.setImageURI(imageUri);
        }
    }
}