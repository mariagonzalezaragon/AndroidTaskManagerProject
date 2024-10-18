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
    private Button btnAddUser, btnHome; // Añadir el botón Home
    private DatabaseReference databaseReference;
    private ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        recyclerViewUsers = findViewById(R.id.recycler_view_users);
        btnAddUser = findViewById(R.id.btnAddUser);
        btnHome = findViewById(R.id.btnHome); // Enlazar el botón Home

        // Configurar el RecyclerView
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Cargar los datos desde Firebase
        loadUsersFromDatabase();

        // Configurar el adaptador
        userAdapter = new UserAdapter(userList);
        recyclerViewUsers.setAdapter(userAdapter);

        // Funcionalidad del botón "Agregar Usuario"
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(Manage_User_Activity.this, Register.class);
                startActivity(intent);
            }
        });

        // Funcionalidad del botón "Home"
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la actividad Home
                Intent intent = new Intent(Manage_User_Activity.this, Home.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual para no volver con el botón de retroceso
            }
        });
    }

    // Método para cargar usuarios desde Firebase Database
    private void loadUsersFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Limpiar la lista antes de agregar nuevos datos

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener los datos del usuario desde Firebase
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user); // Añadir el usuario a la lista
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de la base de datos
                Log.e("Manage_User_Activity", "Error al cargar usuarios", databaseError.toException());
            }
        });
    }
}
