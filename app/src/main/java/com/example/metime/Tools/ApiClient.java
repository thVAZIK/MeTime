package com.example.metime.Tools;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.metime.Models.Appointment;
import com.example.metime.Models.AppointmentInsert;
import com.example.metime.Models.AppointmentStatusUpdate;
import com.example.metime.Models.LoginRequest;
import com.example.metime.Models.ProfileUpdate;
import com.example.metime.Models.UserUpdateRequest;
import com.example.metime.Models.VerifyRequest;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {


    public interface SBC_Callback {
        public void onFailure(IOException e);

        public void onError(String errorBody);

        public void onResponse(String responseBody);
    }

    public ApiClient(Context context) {
        API_KEY = ApiKeyLoader.getApiKey(context);
    }

    OkHttpClient client = new OkHttpClient();
    public static String DOMAIN_NAME = "https://xjbqmokswhilgipdgose.supabase.co/";
    public static String REST_PATH = "rest/v1/";
    public static String AUTH_PATH = "auth/v1/";
    public static String API_KEY;

    public void getUser(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "user")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void getProfile(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?select=*&id=eq." + DataBinding.getUuidUser())
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Range", "0-9")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void fetchAllUserAppointments(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Appointments?select=*,Masters(*, Master_Ratings_Summary(*)),Services(*),Salons(*)&status=neq.4")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void login(LoginRequest loginRequest, final SBC_Callback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "token?grant_type=password")
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void register(LoginRequest loginRequest, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(loginRequest);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "signup")
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void updateProfile(ProfileUpdate profile, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "profiles?id=eq." + DataBinding.getUuidUser())
                .method("PATCH", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void fetchAllServices(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Services?select=*,Salons(*)")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void fetchAllMasters(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Masters?select=*,Specializations(*),Master_Ratings_Summary(*)")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void verifyUser(String email, String token, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(new VerifyRequest(email, token, "signup"));
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "verify")
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void uploadAvatar(String userUuid, byte[] imageData, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("image/png");
        RequestBody body = RequestBody.create(imageData, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + "storage/v1/object/avatars/" + userUuid + ".png")
                .method("PUT", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "image/png")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void updateUser(String email, String password, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(new UserUpdateRequest(email, password));
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + AUTH_PATH + "user")
                .method("PUT", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void fetchAllActiveBanners(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Banners?select=*&is_active=eq.true")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void updateAppointmentStatus(int appointmentId, int status, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(new AppointmentStatusUpdate(status));
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Appointments?appointment_id=eq." + appointmentId)
                .method("PATCH", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void fetchAllMasterCalendars(final SBC_Callback callback) {
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Master_Calendars?select=*,Masters(*)")
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }

    public void createAppointment(AppointmentInsert appointment, final SBC_Callback callback) {
        MediaType mediaType = MediaType.parse("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(appointment);
        RequestBody body = RequestBody.create(json, mediaType);
        Request request = new Request.Builder()
                .url(DOMAIN_NAME + REST_PATH + "Appointments")
                .method("POST", body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", DataBinding.getBearerToken())
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=minimal")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    callback.onResponse(responseBody);
                } else {
                    callback.onError(responseBody);
                }
            }
        });
    }
}
