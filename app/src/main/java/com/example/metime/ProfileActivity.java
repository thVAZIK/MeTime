package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.metime.Fragments.SidePanelDialogFragment;
import com.example.metime.Models.Profile;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.DataBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements SidePanelDialogFragment.OnNavigationItemSelectedListener {
    ImageView MenuBtn, UserAvatarIV;
    TextView UserFullNameTV, ChangeProfileDataBtn, ChangePasswordButton, LogOutBtn;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_PIN_CODE = "pin_code";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private void init() {
        MenuBtn = findViewById(R.id.MenuBtn);
        UserAvatarIV = findViewById(R.id.UserAvatarIV);
        UserFullNameTV = findViewById(R.id.UserFullNameTV);
        ChangeProfileDataBtn = findViewById(R.id.ChangeProfileDataBtn);
        ChangePasswordButton = findViewById(R.id.ChangePasswordButton);
        LogOutBtn = findViewById(R.id.LogOutBtn);
        getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        MenuBtn.setOnClickListener(view -> {
            SidePanelDialogFragment sidePanel = SidePanelDialogFragment.newInstance("profile");
            sidePanel.setOnNavigationItemSelectedListener(this);
            sidePanel.show(getSupportFragmentManager(), "SidePanelDialogFragment");
        });

        ChangeProfileDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(KEY_EMAIL);
                editor.remove(KEY_PASSWORD);
                editor.remove(KEY_USER_ID);
                editor.remove(KEY_ACCESS_TOKEN);
                editor.remove(KEY_PIN_CODE);
                editor.apply();
                DataBinding.clearUuidUser();
                DataBinding.clearBearerToken();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(String item) {
        switch (item) {
            case "home":
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                finish();
                break;
            case "profile":

                break;
            case "bookings":
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
                finish();
                break;
            case "support":
                startActivity(new Intent(getApplicationContext(), SupportChatActivity.class));
                finish();
                break;
        }
    }

    private void getCurrentUser() {
        ApiClient supabaseClient = new ApiClient(ProfileActivity.this);
        supabaseClient.getProfile(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:user:onFailure", e.getLocalizedMessage());
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:user:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Profile>>(){}.getType();
                    List<Profile> profiles = gson.fromJson(responseBody, type);
                    if (profiles != null && !profiles.isEmpty()) {
                        Profile profile = profiles.get(0);
                        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/avatars//";
                        Glide.with(getApplicationContext())
                                .load(url + profile.getAvatar_url())
                                .placeholder(R.drawable.placeholder_avatar)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .error(R.drawable.placeholder_avatar)
                                .into(UserAvatarIV);
                        UserFullNameTV.setText(profile.getUsername());
                    }
                });
            }
        });
    }
}