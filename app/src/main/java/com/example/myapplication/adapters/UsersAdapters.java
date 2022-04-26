package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemContainerUserBinding;
import com.example.myapplication.listeners.UsersListener;
import com.example.myapplication.models.User;

import java.util.List;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.UserViewHolder> {

    private final List<User> users;
    private final UsersListener usersListener;



    public UsersAdapters(List<User> users, UsersListener usersListener) {
        this.users = users;
        this.usersListener= usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,false
        );
        return new UserViewHolder((itemContainerUserBinding));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapters.UserViewHolder holder, int position) {
    holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }
        void setUserData(User user){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v->usersListener.onUserClicked(user));
        }
    }
    private Bitmap getUserImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
