package com.example.taskmanager_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameTextView.setText(user.getUserName());
        holder.emailTextView.setText(user.getEmail());
        holder.roleTextView.setText(user.getRole());

        // Cargar la imagen desde la URL usando Picasso
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.placeholder_image) // Imagen por defecto mientras se carga
                    .error(R.drawable.error_image)             // Imagen en caso de error
                    .into(holder.photoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, emailTextView, roleTextView;
        ImageView photoImageView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
