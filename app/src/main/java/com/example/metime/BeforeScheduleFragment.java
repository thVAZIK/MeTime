package com.example.metime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
            dismiss();
        });

        Button CreateAccountBtn = view.findViewById(R.id.CreateAccountBtn);
        CreateAccountBtn.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SignUpActivity.class));
            getActivity().finish();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
