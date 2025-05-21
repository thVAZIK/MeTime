package com.example.metime.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.metime.R;

public class SidePanelDialogFragment extends DialogFragment {

    private OnNavigationItemSelectedListener listener;
    private CardView sidePanelCard;
    private boolean isDismissing = false;

    public interface OnNavigationItemSelectedListener {
        void onItemSelected(String item);
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
}