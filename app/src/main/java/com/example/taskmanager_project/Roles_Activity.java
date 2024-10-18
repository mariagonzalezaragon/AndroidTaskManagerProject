package com.example.taskmanager_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Roles_Activity extends AppCompatActivity {

    private EditText edtRoleName, edtRoleDescription;
    private CheckBox chkFullAccess; // Nuevo campo para elegir si el rol tiene acceso completo
    private Button btnCreateRole, btnBackToHome;
    private DatabaseReference rolesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roles);

        // Referencia a la tabla Roles en Firebase
        rolesDatabase = FirebaseDatabase.getInstance().getReference("Roles");

        // Inicializar los elementos de la UI
        edtRoleName = findViewById(R.id.edtRoleName);
        edtRoleDescription = findViewById(R.id.edtRoleDescription);
        chkFullAccess = findViewById(R.id.chkFullAccess); // CheckBox para el acceso completo
        btnCreateRole = findViewById(R.id.btnCreateRole);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Crear un nuevo rol cuando se haga clic en el botón
        btnCreateRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roleName = edtRoleName.getText().toString().trim();
                String roleDescription = edtRoleDescription.getText().toString().trim();
                boolean fullAccess = chkFullAccess.isChecked(); // Obtener el valor del CheckBox

                if (!roleName.isEmpty()) {
                    createRole(roleName, roleDescription, fullAccess);
                } else {
                    Toast.makeText(Roles_Activity.this, "Please fill in the role name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Botón para regresar a Home
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresar a la actividad Home
                Intent intent = new Intent(Roles_Activity.this, Home.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual
            }
        });
    }

    // Método para crear un rol y agregarlo a Firebase
    private void createRole(String roleName, String roleDescription, boolean fullAccess) {
        // Generar un nuevo roleId automáticamente usando push()
        String roleId = rolesDatabase.push().getKey();

        Role newRole = new Role(roleId, roleName, roleDescription, fullAccess);

        // Guardar el nuevo rol en Firebase
        rolesDatabase.child(roleId).setValue(newRole).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Roles_Activity.this, "Role created successfully", Toast.LENGTH_SHORT).show();
                edtRoleName.setText("");
                edtRoleDescription.setText("");
                chkFullAccess.setChecked(false); // Restablecer el CheckBox
            } else {
                Toast.makeText(Roles_Activity.this, "Failed to create role", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
