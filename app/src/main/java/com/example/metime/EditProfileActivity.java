package com.example.metime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.metime.Models.Profile;
import com.example.metime.Models.ProfileUpdate;
import com.example.metime.Models.User;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.DataBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView backBtn, userAvatarIV;
    private FloatingActionButton editAvatarFab;
    private TextInputEditText firstNameET, lastNameET, emailET;
    private MaterialButton cancelBtn, saveChangesBtn;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private void init() {
        backBtn = findViewById(R.id.BackBtn);
        userAvatarIV = findViewById(R.id.UserAvatarIV);
        editAvatarFab = findViewById(R.id.editAvatarFab);
        firstNameET = findViewById(R.id.FirstNameET);
        lastNameET = findViewById(R.id.LastNameET);
        emailET = findViewById(R.id.EmailET);
        cancelBtn = findViewById(R.id.CancelBtn);
        saveChangesBtn = findViewById(R.id.SaveChangesBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editing_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Инициализация лаунчера для выбора изображения
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.placeholder_avatar)
                        .error(R.drawable.placeholder_avatar)
                        .into(userAvatarIV);
            }
        });

        // Загрузка текущих данных профиля
        getCurrentUser();

        // Обработчики кнопок
        backBtn.setOnClickListener(v -> finish());
        cancelBtn.setOnClickListener(v -> finish());
        editAvatarFab.setOnClickListener(v -> openImagePicker());
        saveChangesBtn.setOnClickListener(v -> saveProfileChanges());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void getCurrentUser() {
        ApiClient supabaseClient = new ApiClient(this);
        supabaseClient.getProfile(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:user:onFailure", e.getLocalizedMessage());
                    Toast.makeText(EditProfileActivity.this, "Не удалось загрузить профиль", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetch:user:onError", errorBody);
                    Toast.makeText(EditProfileActivity.this, "Не удалось загрузить профиль", Toast.LENGTH_SHORT).show();
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
                        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/avatars/";
                        Glide.with(EditProfileActivity.this)
                                .load(url + profile.getAvatar_url())
                                .placeholder(R.drawable.placeholder_avatar)
                                .signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
                                .error(R.drawable.placeholder_avatar)
                                .into(userAvatarIV);
                        // Разделяем username на first_name и last_name
                        String[] nameParts = profile.getUsername() != null ? profile.getUsername().split(" ", 2) : new String[]{"", ""};
                        firstNameET.setText(nameParts[0]);
                        lastNameET.setText(nameParts.length > 1 ? nameParts[1] : "");
                    }
                });
            }
        });
        supabaseClient.getUser(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:userEmail:onFailure", e.getLocalizedMessage());
                    Toast.makeText(EditProfileActivity.this, "Не удалось загрузить email", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetch:userEmail:onError", errorBody);
                    Toast.makeText(EditProfileActivity.this, "Не удалось загрузить email", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:userEmail:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<User>() {
                    }.getType();
                    User user = gson.fromJson(responseBody, type);
                    if (user != null) {
                        emailET.setText(user.getEmail());
                    }
                });
            }
        });
    }

    private void saveProfileChanges() {
        String firstName = firstNameET.getText().toString().trim();
        String lastName = lastNameET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String username = firstName + " " + lastName;

        // Валидация
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = DataBinding.getUuidUser();
        if (userId == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient supabaseClient = new ApiClient(this);
        if (selectedImageUri != null) {
            // Преобразование изображения в byte[]
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] imageData = stream.toByteArray();

                // Загрузка аватарки
                supabaseClient.uploadAvatar(userId, imageData, new ApiClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                        runOnUiThread(() -> {
                            Log.e("uploadAvatar:onFailure", e.getLocalizedMessage());
                            Toast.makeText(EditProfileActivity.this, "Не удалось загрузить аватарку", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String errorBody) {
                        runOnUiThread(() -> {
                            Log.e("uploadAvatar:onError", errorBody);
                            Toast.makeText(EditProfileActivity.this, "Не удалось загрузить аватарку", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        runOnUiThread(() -> {
                            Log.d("uploadAvatar:onResponse", responseBody);
                            String avatarUrl = userId + ".png"; // Имя файла в бакете
                            updateProfile(username, email, avatarUrl);
                        });
                    }
                });
            } catch (IOException e) {
                Log.e("ImageConversion", "Failed to convert image", e);
                Toast.makeText(this, "Ошибка обработки изображения", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Обновление без аватарки
            updateProfile(username, email, null);
        }
    }

    private void updateProfile(String username, String email, String avatarUrl) {
        ApiClient supabaseClient = new ApiClient(this);
        ProfileUpdate profileUpdate = new ProfileUpdate(username, avatarUrl);
        supabaseClient.updateProfile(profileUpdate, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("updateProfile:onFailure", e.getLocalizedMessage());
                    Toast.makeText(EditProfileActivity.this, "Не удалось обновить профиль", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("updateProfile:onError", errorBody);
                    Toast.makeText(EditProfileActivity.this, "Не удалось обновить профиль", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("updateProfile:onResponse", responseBody);
                    Toast.makeText(EditProfileActivity.this, "Профиль успешно обновлён", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}