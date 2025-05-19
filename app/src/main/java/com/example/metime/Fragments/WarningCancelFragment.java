package com.example.metime.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.metime.R;
import com.google.android.material.button.MaterialButton;

public class WarningCancelFragment extends DialogFragment {
    public interface DialogListener {
        void onNoClicked();
        void onCancelClicked();
    }

    private DialogListener listener;

    public static WarningCancelFragment newInstance() {
        return new WarningCancelFragment();
    }

    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warning_cancel, container, false);

        MaterialButton NoBtn = view.findViewById(R.id.NoBtn);
        MaterialButton CancelBtn = view.findViewById(R.id.CancelBtn);

        NoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onNoClicked();
                }
                dismiss();
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCancelClicked();
                }
                dismiss();
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (315 * metrics.density);
                params.height = (int) (311 * metrics.density);
                window.setAttributes(params);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
    }
}
