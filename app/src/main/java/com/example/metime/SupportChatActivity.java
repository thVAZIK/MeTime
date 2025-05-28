package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.ChatAdapter;
import com.example.metime.Fragments.SidePanelDialogFragment;
import com.example.metime.Models.Chat;
import com.example.metime.Models.ChatCreateRequest;
import com.example.metime.Models.Message;
import com.example.metime.Models.MessageCreateRequest;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SupportChatActivity extends AppCompatActivity implements SidePanelDialogFragment.OnNavigationItemSelectedListener {
    private RecyclerView chatRV;
    private TextInputEditText messageET;
    private ImageView sendBtn, MenuBtn;
    private ChatAdapter adapter;
    private int chatId = -1;
    private String userId;
    private ApiClient apiClient;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Gson gson = new Gson();
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String TAG = "SupportChatActivity";
    private static final String[] SUPPORT_RESPONSES = {
            "Thank you for your message! How can we assist you today?",
            "Our team is here to help. Could you provide more details?",
            "We appreciate your contact. What's the issue you're facing?",
            "Thanks for reaching out! We'll look into this for you."
    };
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    private void init() {
        // Retrieve userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getString(KEY_USER_ID, null);
        if (userId == null) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ApiClient
        apiClient = new ApiClient(this);

        MenuBtn = findViewById(R.id.MenuBtn);
        chatRV = findViewById(R.id.ChatRV);
        messageET = findViewById(R.id.MessageFieldET);
        sendBtn = findViewById(R.id.SendBtn);

        adapter = new ChatAdapter();
        chatRV.setLayoutManager(new LinearLayoutManager(this));
        chatRV.setAdapter(adapter);

        // Check for existing chat
        fetchChats();

        sendBtn.setOnClickListener(v -> sendMessage());

        MenuBtn.setOnClickListener(view -> {
            SidePanelDialogFragment sidePanel = SidePanelDialogFragment.newInstance("support");
            sidePanel.setOnNavigationItemSelectedListener(this);
            sidePanel.show(getSupportFragmentManager(), "SidePanelDialogFragment");
        });
    }

    private void fetchChats() {
        apiClient.fetchAllChats(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "fetchAllChats response: " + responseBody);
                    List<Chat> chats = gson.fromJson(responseBody, new TypeToken<List<Chat>>() {
                    }.getType());
                    if (chats == null) {
                        Log.e(TAG, "fetchAllChats: Parsed chats list is null");
                        Toast.makeText(SupportChatActivity.this, "Failed to load chats", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Chat chat : chats) {
                        if (chat != null && chat.getUser_id().equals(userId)) {
                            chatId = chat.getChat_id();
                            fetchMessages();
                            return;
                        }
                    }
                    // No existing chat, create new one
                    createNewChat();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllChats error: " + errorBody);
                    Toast.makeText(SupportChatActivity.this, "Error fetching chats: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllChats failure: " + e.getMessage());
                    Toast.makeText(SupportChatActivity.this, "Failed to fetch chats: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void createNewChat() {
        ChatCreateRequest request = new ChatCreateRequest(userId);
        apiClient.createChat(request, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "createChat response: success");
                    fetchChats();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createChat error: " + errorBody);
                    Toast.makeText(SupportChatActivity.this, "Error creating chat: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createChat failure: " + e.getMessage());
                    Toast.makeText(SupportChatActivity.this, "Failed to create chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchMessages() {
        apiClient.fetchAllMessages(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "fetchAllMessages response: " + responseBody);
                    List<Message> messages = gson.fromJson(responseBody, new TypeToken<List<Message>>() {
                    }.getType());
                    if (messages == null) {
                        Log.e(TAG, "fetchAllMessages: Parsed messages list is null");
                        Toast.makeText(SupportChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adapter.setMessages(messages);
                    chatRV.scrollToPosition(messages.size() - 1);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllMessages error: " + errorBody);
                    Toast.makeText(SupportChatActivity.this, "Error fetching messages: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllMessages failure: " + e.getMessage());
                    Toast.makeText(SupportChatActivity.this, "Failed to fetch messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sendMessage() {
        String content = messageET.getText().toString().trim();
        if (content.isEmpty() || chatId == -1) return;

        MessageCreateRequest request = new MessageCreateRequest(chatId, userId, content);
        apiClient.createMessage(request, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "createMessage response: 201");
                    messageET.setText("");
                    fetchMessages();
                    chatRV.scrollToPosition(adapter.getItemCount() - 1);
                    simulateSupportResponse();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createMessage error: " + errorBody);
                    Toast.makeText(SupportChatActivity.this, "Error sending message: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createMessage failure: " + e.getMessage());
                    Toast.makeText(SupportChatActivity.this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void simulateSupportResponse() {
        handler.postDelayed(() -> {
            String response = SUPPORT_RESPONSES[new Random().nextInt(SUPPORT_RESPONSES.length)];
            MessageCreateRequest supportResponse = new MessageCreateRequest(
                    chatId,
                    null, // null sender_id for support
                    response
            );
            apiClient.createMessage(supportResponse, new ApiClient.SBC_Callback() {
                @Override
                public void onResponse(String responseBody) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "createMessage response: 201");
                        fetchMessages();
                        chatRV.scrollToPosition(adapter.getItemCount() - 1);

                    });
                }

                @Override
                public void onError(String errorBody) {
                    runOnUiThread(() -> Log.e(TAG, "simulateSupportResponse error: " + errorBody));
                }

                @Override
                public void onFailure(IOException e) {
                    runOnUiThread(() -> Log.e(TAG, "simulateSupportResponse failure: " + e.getMessage()));
                }
            });
        }, 3000 + new Random().nextInt(2000)); // 3-5 seconds delay
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_support_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onItemSelected(String item) {
        switch (item) {
            case "home":
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                finish();
                break;
            case "profile":
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                break;
            case "bookings":
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
                finish();
                break;
            case "support":
                break;
        }
    }
}