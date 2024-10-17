package com.example.taskmanager_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.Locale;


public class My_Profile extends AppCompatActivity {


    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageView firebaseimage;
    EditText txtProfName, profOldPass, profNewPass, profConfirmPass;
    Spinner spinnerPosition;
    Button btnSelectImage, btnUploadImage, btnSaveUser, btnDeleteAccount, btnLogout, bntSavePass;
    private String role;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("users");
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


        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        if (currentUser != null) {
            emailUser = currentUser.getEmail();
            userDatabase.child(currentUser.getUid()).child("photoUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String photoUrl = task.getResult().getValue(String.class);
                        if (photoUrl != null) {
                            Uri photoUri = Uri.parse(photoUrl);
                            new SaveImageHelper(firebaseimage).loadImage(photoUrl);
                        }
                    }
                }
            });
        } else {
            emailUser = "Filed try again later";
        }


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

        //  readUserData();

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
                                            Toast.makeText(My_Profile.this,
                                                    "Failed to delete account from Auth. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(My_Profile.this,
                                            "Failed to delete account from database. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        bntSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });

    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                firebaseimage.setImageURI(imageUri);
                                Toast.makeText(My_Profile.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                saveImageUrlToUserProfile(imageUrl);
                            }
                        });
                        Toast.makeText(My_Profile.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(My_Profile.this, "Failed to Upload", Toast.LENGTH_SHORT).show();


                    }
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
        if (requestCode == 100 && data.getData() != null) {
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
                        Toast.makeText(My_Profile.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void readUserData() {
        if (currentUser == null) {
            Toast.makeText(My_Profile.this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        userDatabase.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    DataSnapshot snapshot = task.getResult();
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        txtProfName.setText(user.getUserName());

                        setSpinnerRole(user.getRole());
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

    private void updateUserData() {
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
                }
            }
        });
    }

}
