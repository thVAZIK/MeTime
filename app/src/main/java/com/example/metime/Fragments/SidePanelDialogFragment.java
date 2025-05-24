package com.example.metime.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.metime.Models.Profile;
import com.example.metime.R;
import com.example.metime.Tools.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class SidePanelDialogFragment extends DialogFragment {

    private OnNavigationItemSelectedListener listener;
    private CardView sidePanelCard;
    private boolean isDismissing = false;
    private ImageView userAvatarIV;
    private static final String ARG_CURRENT_ACTIVITY = "current_activity";

    public interface OnNavigationItemSelectedListener {
        void onItemSelected(String item);
    }

    public static SidePanelDialogFragment newInstance(String currentActivity) {
        SidePanelDialogFragment fragment = new SidePanelDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_ACTIVITY, currentActivity);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.SidePanelDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nav_panel, container, false);

        sidePanelCard = view.findViewById(R.id.SidePanelCV);
        userAvatarIV = view.findViewById(R.id.UserAvatarIV);

        // Get the current activity from arguments
        String currentActivity = getArguments() != null ? getArguments().getString(ARG_CURRENT_ACTIVITY, "") : "";

        // Initialize navigation items
        ImageView homeIcon = view.findViewById(R.id.HomeIconIV);
        TextView homeText = view.findViewById(R.id.HomeSignTV);
        ImageView profileIcon = userAvatarIV; // UserAvatarIV is the icon for Profile
        TextView profileText = view.findViewById(R.id.ProfileSignTV);
        ImageView bookingsIcon = view.findViewById(R.id.BookingsIconIV);
        TextView bookingsText = view.findViewById(R.id.BookingsSignTV);
        ImageView supportIcon = view.findViewById(R.id.SupportIconIV);
        TextView supportText = view.findViewById(R.id.SupportSignTV);

        int selectedColor = ContextCompat.getColor(requireContext(), R.color.nav_menu_selected);
        int defaultColor = ContextCompat.getColor(requireContext(), R.color.white);

        switch (currentActivity) {
            case "home":
                homeIcon.setColorFilter(selectedColor);
                homeText.setTextColor(selectedColor);
                profileText.setTextColor(defaultColor);
                bookingsIcon.setColorFilter(defaultColor);
                bookingsText.setTextColor(defaultColor);
                supportIcon.setColorFilter(defaultColor);
                supportText.setTextColor(defaultColor);
                break;
            case "profile":
                homeIcon.setColorFilter(defaultColor);
                homeText.setTextColor(defaultColor);
                profileText.setTextColor(selectedColor);
                bookingsIcon.setColorFilter(defaultColor);
                bookingsText.setTextColor(defaultColor);
                supportIcon.setColorFilter(defaultColor);
                supportText.setTextColor(defaultColor);
                break;
            case "bookings":
                homeIcon.setColorFilter(defaultColor);
                homeText.setTextColor(defaultColor);
                profileText.setTextColor(defaultColor);
                bookingsIcon.setColorFilter(selectedColor);
                bookingsText.setTextColor(selectedColor);
                supportIcon.setColorFilter(defaultColor);
                supportText.setTextColor(defaultColor);
                break;
            case "support":
                homeIcon.setColorFilter(defaultColor);
                homeText.setTextColor(defaultColor);
                profileText.setTextColor(defaultColor);
                bookingsIcon.setColorFilter(defaultColor);
                bookingsText.setTextColor(defaultColor);
                supportIcon.setColorFilter(selectedColor);
                supportText.setTextColor(selectedColor);
                break;
            default:
                homeIcon.setColorFilter(defaultColor);
                homeText.setTextColor(defaultColor);
                profileText.setTextColor(defaultColor);
                bookingsIcon.setColorFilter(defaultColor);
                bookingsText.setTextColor(defaultColor);
                supportIcon.setColorFilter(defaultColor);
                supportText.setTextColor(defaultColor);
                break;
        }

        sidePanelCard.setTranslationX(-sidePanelCard.getWidth());
        sidePanelCard.post(() -> {
            ObjectAnimator slideIn = ObjectAnimator.ofFloat(sidePanelCard, "translationX", -sidePanelCard.getWidth(), 0f);
            slideIn.setDuration(300);
            slideIn.start();
        });

        view.findViewById(R.id.MenuBtn).setOnClickListener(v -> dismissWithAnimation());
        view.findViewById(R.id.HomeBtn).setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected("home");
            dismissWithAnimation();
        });
        view.findViewById(R.id.ProfileBtn).setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected("profile");
            dismissWithAnimation();
        });
        view.findViewById(R.id.BookingsBtn).setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected("bookings");
            dismissWithAnimation();
        });
        view.findViewById(R.id.SupportBtn).setOnClickListener(v -> {
            if (listener != null) listener.onItemSelected("support");
            dismissWithAnimation();
        });

        getCurrentUser();

        return view;
    }

    private void dismissWithAnimation() {
        if (isDismissing) return;
        isDismissing = true;

        ObjectAnimator slideOut = ObjectAnimator.ofFloat(sidePanelCard, "translationX", 0f, -sidePanelCard.getWidth());
        slideOut.setDuration(300);
        slideOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dismiss();
            }
        });
        slideOut.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void dismiss() {
        if (!isDismissing) {
            dismissWithAnimation();
        } else {
            super.dismiss();
        }
    }

    private void getCurrentUser() {
        if (getContext() == null) {
            Log.e("SidePanelDialogFragment", "Context is null, cannot fetch user");
            return;
        }

        ApiClient api = new ApiClient(requireContext());
        api.getProfile(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("fetch:userAvatar:onFailure", e.getLocalizedMessage());
                    userAvatarIV.setImageResource(R.drawable.placeholder_avatar);
                });
            }

            @Override
            public void onResponse(String responseBody) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("fetch:userAvatar:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Profile>>(){}.getType();
                    List<Profile> profiles = gson.fromJson(responseBody, type);
                    if (profiles != null && !profiles.isEmpty()) {
                        Profile profile = profiles.get(0);
                        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/avatars/";
                        Glide.with(requireContext())
                                .load(url + profile.getAvatar_url())
                                .placeholder(R.drawable.placeholder_avatar)
                                .error(R.drawable.placeholder_avatar)
                                .into(userAvatarIV);
                    } else {
                        userAvatarIV.setImageResource(R.drawable.placeholder_avatar);
                    }
                });
            }
        });
    }
}