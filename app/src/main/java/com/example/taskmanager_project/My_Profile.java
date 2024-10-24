package com.example.taskmanager_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class My_Profile extends AppCompatActivity {

    private static final String TAG = "My_Profile";
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageView firebaseimage;
    EditText txtProfName, profOldPass, profNewPass, profConfirmPass;
    Spinner spinnerPosition;
    Button btnSelectImage, btnUploadImage, btnSaveUser, btnDeleteAccount, btnLogout, btnHome, bntSavePass;
    private String selectedRole;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
    DatabaseReference rolesDatabase = FirebaseDatabase.getInstance().getReference("Roles");
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
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveUser = findViewById(R.id.btnSaveUser);
        btnDeleteAccount = findViewById(R.id.btndeleteAccount);
        btnLogout = findViewById(R.id.btnLogout);
        bntSavePass = findViewById(R.id.bntSavePass);
        firebaseimage = findViewById(R.id.topImage);
        btnHome = findViewById(R.id.btnHome);

        btnSelectImage.setOnClickListener(v -> selectImage());

        btnUploadImage.setOnClickListener(v -> uploadImage());

        if (currentUser != null) {
            emailUser = currentUser.getEmail();
            // Cargar la URL de la imagen del usuario desde Firebase
            userDatabase.child(currentUser.getUid()).child("photoUrl").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String photoUrl = task.getResult().getValue(String.class);
                    if (photoUrl != null) {
                        // Cargar la imagen usando Picasso directamente desde la URL almacenada
                        Picasso.get().load(photoUrl).into(firebaseimage);
                    }
                } else {
                    Log.e(TAG, "Error al cargar la imagen de Firebase", task.getException());
                }
            });
        } else {
            emailUser = "Failed, try again later";
            Log.e(TAG, "No se encontró el usuario actual");
        }

        loadRolesIntoSpinner();
        loadUserData();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(My_Profile.this, Login.class);
            startActivity(intent);
            finish();
        });

        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(My_Profile.this, Home.class);
            startActivity(intent);
            finish();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(My_Profile.this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete your account? This action is irreversible.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String userId = currentUser.getUid();
                        userDatabase.child(userId).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                currentUser.delete().addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        Intent intent = new Intent(My_Profile.this, Register.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(TAG, "Error al eliminar la cuenta de autenticación", authTask.getException());
                                    }
                                });
                            } else {
                                Log.e(TAG, "Error al eliminar la cuenta en la base de datos", task.getException());
                            }
                        });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        bntSavePass.setOnClickListener(v -> updatePassword());

        btnSaveUser.setOnClickListener(view -> updateUserData());
    }

    private void loadRolesIntoSpinner() {
        rolesDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> rolesList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String roleName = snapshot.child("roleName").getValue(String.class);
                    rolesList.add(roleName);
                }

                if (rolesList.isEmpty()) {
                    Log.e(TAG, "No se encontraron roles en Firebase.");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(My_Profile.this, android.R.layout.simple_spinner_item, rolesList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPosition.setAdapter(adapter);

                spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                        selectedRole = parentView.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            } else {
                Log.e(TAG, "Error al cargar los roles desde Firebase", task.getException());
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDatabase.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        txtProfName.setText(user.getUserName());
                        selectUserRoleInSpinner(user.getRole());
                        Log.d(TAG, "Datos del usuario cargados correctamente: " + user.getUserName());
                    } else {
                        Log.e(TAG, "Usuario no encontrado en Firebase");
                    }
                } else {
                    Log.e(TAG, "Error al cargar los datos del usuario desde Firebase", task.getException());
                }
            });
        }
    }

    private void selectUserRoleInSpinner(String userRole) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerPosition.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(userRole);
            if (position >= 0) {
                spinnerPosition.setSelection(position);
            } else {
                Log.e(TAG, "Rol no encontrado en el Spinner");
            }
        }
    }

    private void uploadImage() {
        if (imageUri == null) {
            Toast.makeText(My_Profile.this, "Please select an image to upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File...");
        progressDialog.show();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).into(firebaseimage);
                    Toast.makeText(My_Profile.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    saveImageUrlToUserProfile(imageUrl);
                }))
                .addOnFailureListener(e -> {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(My_Profile.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToUserProfile(String imageUrl) {
        String userId = currentUser.getUid();
        userDatabase.child(userId).child("photoUrl").setValue(imageUrl);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            firebaseimage.setImageURI(imageUri);
        }
    }

    private void updatePassword() {
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
            Toast.makeText(My_Profile.this, "Password confirmation does not match, try again", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(My_Profile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        profOldPass.setText("");
                        profNewPass.setText("");
                        profConfirmPass.setText("");
                    } else {
                        Toast.makeText(My_Profile.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserData() {
        String userId = currentUser.getUid();

        userDatabase.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    String updatedName = txtProfName.getText().toString().trim();
                    String updatedRole = selectedRole;

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

                    userDatabase.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(My_Profile.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(My_Profile.this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
