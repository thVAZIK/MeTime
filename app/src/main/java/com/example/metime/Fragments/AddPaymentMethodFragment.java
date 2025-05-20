package com.example.metime.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.metime.LoginActivity;
import com.example.metime.R;
import com.example.metime.SignUpActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddPaymentMethodFragment extends BottomSheetDialogFragment {

    public AddPaymentMethodFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_payment_method, container, false);

        ImageView CloseBtn = view.findViewById(R.id.CloseBtn);

        CloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
