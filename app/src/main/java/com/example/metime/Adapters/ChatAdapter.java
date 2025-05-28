package com.example.metime.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.metime.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_SUPPORT = 2;
    private List<Message> messages = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;

        UserMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.userMessageText);
            messageTime = view.findViewById(R.id.userMessageTime);
        }
    }

    static class SupportMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;

        SupportMessageViewHolder(View view) {
            super(view);
            messageText = view.findViewById(R.id.userMessageText);
            messageTime = view.findViewById(R.id.userMessageTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message == null) {
            Log.e("ChatAdapter", "Null message at position: " + position);
            return VIEW_TYPE_SUPPORT; // Fallback to support view type to avoid crash
        }
        return message.getSender_id() != null ? VIEW_TYPE_USER : VIEW_TYPE_SUPPORT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_support_message, parent, false);
            return new SupportMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message == null) {
            Log.e("ChatAdapter", "Attempting to bind null message at position: " + position);
            return; // Skip binding for null messages
        }
        String time = timeFormat.format(message.getCreated_at());
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).messageText.setText(message.getContent());
            ((UserMessageViewHolder) holder).messageTime.setText(time);
        } else {
            ((SupportMessageViewHolder) holder).messageText.setText(message.getContent());
            ((SupportMessageViewHolder) holder).messageTime.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(Message message) {
        if (message != null) {
            messages.add(message);
            notifyItemInserted(messages.size() - 1);
        } else {
            Log.e("ChatAdapter", "Attempted to add null message");
        }
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                if (message != null) {
                    this.messages.add(message);
                } else {
                    Log.e("ChatAdapter", "Null message in setMessages");
                }
            }
        } else {
            Log.e("ChatAdapter", "Received null messages list");
        }
        notifyDataSetChanged();
    }
}