package com.example.myapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.adapters.ChatAdapter;
import com.example.myapplication.databinding.ActivityChatBinding;
import com.example.myapplication.models.Chat;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.example.myapplication.utilities.encryption;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ChatActivity extends BaseActivity {
    private DocumentReference documentReference;
    private ActivityChatBinding binding;
    private User receiverUser;
    private Chat chat;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Boolean isReceiverAvailable = false;
    ListenerRegistration listenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        checkChat();
        init();
        listenMessages();



    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USR_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() throws Exception {  // look here here is message sending
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (binding.sendText.getText().toString().trim().isEmpty()) {
        } else {
            encryption encryption = new encryption();
            String key =preferenceManager.getString(Constants.KEY_SHARED_SECRET_KEY);
            String messageee =binding.sendText.getText().toString();
            String messagee = encryption.encrypt(messageee,key);
            showToast(messagee);
            showToast(key);
            HashMap<String, Object> message = new HashMap<>();
            message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USR_ID));
            message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            message.put(Constants.KEY_MESSAGE, messagee );
            message.put(Constants.KEY_TIMESTAMP, new Date());
            database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
            binding.sendText.setText(null);
        }
    }

    private void listenAvailabilityOfReceiver(String id) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager.putString(Constants.KEY_CHAT_ID,id);
        if(listenerRegistration==null) {
            listenerRegistration = database.collection(Constants.KEY_CHAT).document(id)
                    .addSnapshotListener(ChatActivity.this, (value, error) -> {
                        if (error != null) {
                            return;
                        }
                        ;
                        if (value != null) {
                            if (value.getString(Constants.KEY_B).equals("none")) {
                                isReceiverAvailable = false;
                            } else {
                                isReceiverAvailable = true;
                            }

                        }
                        if (isReceiverAvailable) {
                            showToast("finnaly");
                            binding.layoutSend.setVisibility(View.VISIBLE);
                            createSecret1(value.getString(Constants.KEY_B));
                        } else {
                            showToast("fuck my ass");
                        }
                    });
        }
    }

    private void listenAvailabilityOfMaster(String id)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager.putString(Constants.KEY_CHAT_ID,id);
        if(listenerRegistration==null) {
            listenerRegistration = database.collection(Constants.KEY_CHAT).document(id)
                    .addSnapshotListener(ChatActivity.this, (value, error) -> {
                        if (error != null) {
                            return;
                        }

                        if (value == null) {
                            onBackPressed();
                        }
                    });
        }
    }

    private void listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USR_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USR_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    encryption encryption = new encryption();
                    String key =preferenceManager.getString(Constants.KEY_SHARED_SECRET_KEY);
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    try {
                        chatMessage.message = encryption.decrypt(
                                documentChange.getDocument().getString(Constants.KEY_MESSAGE),key);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    }

                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }

            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));

            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    });

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails() {
        Intent intent = getIntent();
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> {
            try {
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(listenerRegistration!=null){
        listenerRegistration.remove();
        deleteChat();}
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public String putBigInt() {
        Random rnd = new Random();
        BigInteger i = new BigInteger(10000, rnd);
        return i.toString();
    }
    private void createChat() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> chat = new HashMap<>();
        chat.put(Constants.KEY_FIRST_USER, preferenceManager.getString(Constants.KEY_USR_ID));
        chat.put(Constants.KEY_SECOND_USER, receiverUser.id);
        preferenceManager.putString(Constants.KEY_G, putBigInt());
        chat.put(Constants.KEY_G, preferenceManager.getString(Constants.KEY_G));
        preferenceManager.putString(Constants.KEY_P, putBigInt());
        chat.put(Constants.KEY_P, preferenceManager.getString(Constants.KEY_P));
        preferenceManager.putString(Constants.KEY_A, createKey());
        chat.put(Constants.KEY_A, preferenceManager.getString(Constants.KEY_A));
        chat.put(Constants.KEY_B, "none");
        preferenceManager.putString(Constants.KEY_B, "none");
        database.collection(Constants.KEY_CHAT).add(chat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                listenAvailabilityOfReceiver(documentReference.getId());
            }
        });
    }


    private void checkChat() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        CollectionReference reference = database.collection(Constants.KEY_CHAT);
        reference.whereEqualTo(Constants.KEY_SECOND_USER, preferenceManager.getString(Constants.KEY_USR_ID))
                .whereEqualTo(Constants.KEY_FIRST_USER, receiverUser.id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putString(Constants.KEY_CHAT_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_G, documentSnapshot.getString(Constants.KEY_G));
                        preferenceManager.putString(Constants.KEY_P, documentSnapshot.getString(Constants.KEY_P));
                        preferenceManager.putString(Constants.KEY_A, documentSnapshot.getString(Constants.KEY_A));
                        showToast(preferenceManager.getString(Constants.KEY_CHAT_ID));
                        createSecret();
                        binding.layoutSend.setVisibility(View.VISIBLE);
                       // listenAvailabilityOfMaster(documentSnapshot.getId());
                    } else {
                        checkChat2();
                        showToast("Unable to sign in");
                    }
                });

    }
    private void checkChat2(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        CollectionReference reference = database.collection(Constants.KEY_CHAT);
        reference.whereEqualTo(Constants.KEY_FIRST_USER, preferenceManager.getString(Constants.KEY_USR_ID))
                .whereEqualTo(Constants.KEY_SECOND_USER, receiverUser.id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task != null && task.getResult().getDocuments().size() > 0) {
                        deleteChat();
                    }
                    createChat();
                });

    }
    private void deleteChat() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        database.collection(Constants.KEY_CHAT).document(preferenceManager.getString(Constants.KEY_CHAT_ID))
                .delete();
    }
    private String createKey(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        Random rnd = new Random();
        BigInteger g = new BigInteger(preferenceManager.getString(Constants.KEY_G));
        BigInteger p = new BigInteger(preferenceManager.getString(Constants.KEY_P));
        BigInteger a = new BigInteger(putBigInt());
        preferenceManager.putString(Constants.KEY_a,a.toString());
        BigInteger A = g.modPow(a,p);
        preferenceManager.putString(Constants.KEY_A, A.toString());
        return A.toString();
    }
    private String createSecret() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        BigInteger b = new BigInteger(putBigInt());
        BigInteger p = new BigInteger(preferenceManager.getString(Constants.KEY_P));
        BigInteger g = new BigInteger(preferenceManager.getString(Constants.KEY_G));
        BigInteger A = new BigInteger(preferenceManager.getString(Constants.KEY_A));
        BigInteger K = A.modPow(b,p);
        BigInteger B = g.modPow(b,p);
        preferenceManager.putString(Constants.KEY_A, A.toString());
       // showToast(K.toString());

        Map<String, Object> data = new HashMap<>();
        preferenceManager.putString(Constants.KEY_B, B.toString());
        data.put(Constants.KEY_B, preferenceManager.getString(Constants.KEY_B));
        database.collection(Constants.KEY_CHAT).document(preferenceManager.getString(Constants.KEY_CHAT_ID))
                .set(data, SetOptions.merge());
        preferenceManager.putString(Constants.KEY_SHARED_SECRET_KEY,K.toString());
        showToast(K.toString());
        return K.toString();
    }
    private String createSecret1(String Biba) {
        preferenceManager = new PreferenceManager(getApplicationContext());
        Random rnd = new Random();
        BigInteger a= new BigInteger(preferenceManager.getString(Constants.KEY_a));
        BigInteger p = new BigInteger(preferenceManager.getString(Constants.KEY_P));
        BigInteger B = new BigInteger(Biba);
        BigInteger K = B.modPow(a,p);
        preferenceManager.putString(Constants.KEY_SHARED_SECRET_KEY,K.toString());
        showToast(K.toString());
        return K.toString();
    }


}