package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activities.ChatMessage;
import com.example.myapplication.databinding.ItemContainerReceivedMessageBinding;
import com.example.myapplication.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Bitmap receiverProfileImage;
    private final String senderId;
    private final List<ChatMessage> chatMessage;


    public static final int VIEW_TYPE_SENT = 1;

    public static final int VIEW_TYPE_RECEIVED = 2;
    public ChatAdapter(List<ChatMessage> chatMessages,Bitmap receiverProfileImage,String senderId) {
        this.chatMessage = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId= senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType ==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else{
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
if (getItemViewType(position)==VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessage.get(position));
        }else{
                ((ReceivedMessageViewHolder)holder).setData(chatMessage.get(position),receiverProfileImage);
            }
        }


    @Override
    public int getItemCount() {
        return chatMessage.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(chatMessage.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }

    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerReceivedMessageBinding binding;


        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage,Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }

    }
}
