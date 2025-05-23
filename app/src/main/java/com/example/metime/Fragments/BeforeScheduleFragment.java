package com.example.metime.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.metime.LoginActivity;
import com.example.metime.R;
import com.example.metime.SignUpActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BeforeScheduleFragment extends BottomSheetDialogFragment {

    public BeforeScheduleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before_schedule, container, false);

        Button LoginBtn = view.findViewById(R.id.LogInBtn);
        LoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.putExtra("completeOnboarding", true);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
            dismiss();
        });

        Button CreateAccountBtn = view.findViewById(R.id.CreateAccountBtn);
        CreateAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SignUpActivity.class);
            intent.putExtra("completeOnboarding", true);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
