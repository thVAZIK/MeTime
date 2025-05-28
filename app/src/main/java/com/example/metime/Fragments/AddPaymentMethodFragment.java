package com.example.metime.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.metime.AddCardActivity;
import com.example.metime.CheckoutActivity;
import com.example.metime.Models.PaymentMethod;
import com.example.metime.R;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;

public class AddPaymentMethodFragment extends BottomSheetDialogFragment {
    private static final int REQUEST_CODE_ADD_CARD = 100;
    private TextView removeBtn, CardSign;
    private ImageView AddCardBtn;
    private String cardPaymentMethodId;
    private ActivityResultLauncher<Intent> addCardLauncher;

    public AddPaymentMethodFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ActivityResultLauncher
        addCardLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchPaymentMethods();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_payment_method, container, false);

        ImageView closeBtn = view.findViewById(R.id.CloseBtn);
        ConstraintLayout cardCL = view.findViewById(R.id.CardCL);
        AddCardBtn = view.findViewById(R.id.AddCardBtn);
        CardSign = view.findViewById(R.id.CardSign);
        removeBtn = view.findViewById(R.id.RemoveBtn);
        ConstraintLayout applePayCL = view.findViewById(R.id.ApplePayCL);
        ConstraintLayout paypalCL = view.findViewById(R.id.PaypalCL);
        ConstraintLayout cashCL = view.findViewById(R.id.CashCL);

        // Disable other payment options
        applePayCL.setEnabled(false);
        paypalCL.setEnabled(false);
        cashCL.setEnabled(false);

        closeBtn.setOnClickListener(v -> dismiss());

        cardCL.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddCardActivity.class);
            addCardLauncher.launch(intent);
        });

        removeBtn.setOnClickListener(v -> {
            if (cardPaymentMethodId != null) {
                deletePaymentMethod(cardPaymentMethodId);
            }
        });

        fetchPaymentMethods();

        return view;
    }

    private void fetchPaymentMethods() {
        ApiClient api = new ApiClient(requireContext());
        api.fetchAllPaymentMethods(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("fetchPaymentMethods:onFailure", e.getLocalizedMessage());
                    Toast.makeText(getContext(), "Failed to fetch payment methods", Toast.LENGTH_SHORT).show();
                    updateRemoveButton(null);
                });
            }

            @Override
            public void onError(String errorBody) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("fetchPaymentMethods:onError", errorBody);
                    Toast.makeText(getContext(), "Error: " + errorBody, Toast.LENGTH_SHORT).show();
                    updateRemoveButton(null);
                });
            }

            @Override
            public void onResponse(String responseBody) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("fetchPaymentMethods:onResponse", responseBody);
                    Gson gson = new Gson();
                    TypeToken<List<PaymentMethod>> typeToken = new TypeToken<List<PaymentMethod>>() {};
                    List<PaymentMethod> paymentMethods = gson.fromJson(responseBody, typeToken.getType());
                    PaymentMethod cardMethod = null;
                    for (PaymentMethod method : paymentMethods) {
                        if (method.getType() == 4) {
                            cardMethod = method;
                            break;
                        }
                    }
                    updateRemoveButton(cardMethod);
                });
            }
        });
    }

    private void deletePaymentMethod(String paymentMethodId) {
        ApiClient api = new ApiClient(requireContext());
        api.deletePaymentMethod(paymentMethodId, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to delete payment method", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Payment method deleted", Toast.LENGTH_SHORT).show();
                    fetchPaymentMethods(); // Refresh to hide RemoveBtn
                });
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((CheckoutActivity)getContext()).fetchPaymentMethods();
    }

    private void updateRemoveButton(PaymentMethod cardMethod) {
        if (cardMethod != null) {
            cardPaymentMethodId = cardMethod.getPayment_method_id();
            removeBtn.setVisibility(View.VISIBLE);
            AddCardBtn.setVisibility(View.INVISIBLE);
            CardSign.setText(cardMethod.getDetails().getCard_number().substring(0, 4) + " **** **** " + cardMethod.getDetails().getCard_number().substring(12, 16));
        } else {
            cardPaymentMethodId = null;
            removeBtn.setVisibility(View.INVISIBLE);
            AddCardBtn.setVisibility(View.VISIBLE);
            CardSign.setText("Card");
        }
    }
}