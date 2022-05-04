package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.adapters.UsersAdapters;
import com.example.myapplication.databinding.ActivityUsersBinding;
import com.example.myapplication.listeners.UsersListener;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UsersListener  {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        setListener();
        getUsers();

    }
    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(v->
                {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USR_ID);
                            if(v.isSuccessful() && v.getResult()!=null){
                                List<User> users = new ArrayList<>();
                                for(QueryDocumentSnapshot queryDocumentSnapshot: v.getResult()){
                                    if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                        continue;
                                    }
                                    User user = new User();
                                    user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                    user.email=queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                    user.image=queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                    user.token=queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                    user.id=queryDocumentSnapshot.getId();
                                    users.add(user);

                                }
                                        if(users.size() > 0){
                                            UsersAdapters usersAdapter = new UsersAdapters(users,this);
                                            binding.usersRecyclerView.setAdapter(usersAdapter);
                                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                                        }else {
                                            showErrorMessage();
                                        }

                            } else{
                                showErrorMessage();
                            }
                });
    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

 }
 private void set(){

 }

 @Override
    public void onUserClicked(User user)
 {
     Intent intent = new Intent(UsersActivity.this,ChatActivity.class);
     set();
     intent.putExtra(Constants.KEY_USER,user);
     startActivity(intent);
     finish();
 }

}