package com.payments.app.seey.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.braintreepayments.cardform.view.CardForm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.payments.app.seey.IntegrationActivity;
import com.payments.app.seey.R;
import com.payments.app.seey.action.Beneficiary;
import com.payments.app.seey.rapyd.RapydUtils;
import com.zaferayan.creditcard.model.CreditCard;
import com.zaferayan.creditcard.view.CreditCardView;

public class PayoutFragment extends BottomSheetDialogFragment {

    private Button sebdBtn;
    private ProgressBar progressBar;
    private CreditCardView ccView;
    private String amount;

    public PayoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        amount = "233.22";
        sebdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccView.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                CreditCard creditCard =ccView.getCreditCardInfo();

                final Beneficiary beneficiary = new Beneficiary(creditCard);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RapydUtils.Payout( Double.parseDouble(amount),  beneficiary);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

    }

    // TODO: Rename and change types and number of parameters
    public static PayoutFragment newInstance() {
        PayoutFragment fragment = new PayoutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_payout, container, false);
        sebdBtn = view.findViewById(R.id.sebdBtn);
        progressBar = view.findViewById(R.id.progressBar);
        ccView = view.findViewById(R.id.ccView);

        return view;
    }
}