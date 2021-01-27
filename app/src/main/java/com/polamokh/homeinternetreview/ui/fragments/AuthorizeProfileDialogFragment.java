package com.polamokh.homeinternetreview.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.listeners.IOnDialogButtonClickListener;

public class AuthorizeProfileDialogFragment extends DialogFragment {

    private EditText editText;
    private IOnDialogButtonClickListener listener;

    public AuthorizeProfileDialogFragment(IOnDialogButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_fragment_verify_profile, null);
        initializeView(view);

        builder.setView(view)
                .setTitle(R.string.dialog_verify_profile_title)
                .setPositiveButton(R.string.dialog_verify_profile_verify_btn, (dialog, which) -> {
                    listener.OnPositiveButtonClicked(editText.getText().toString());
                })
                .setNegativeButton(R.string.dialog_verify_profile_cancel_btn, (dialog, which) -> {
                    dialog.cancel();
                });

        return builder.create();
    }

    private void initializeView(View view) {
        TextInputLayout layout = view.findViewById(R.id.profile_password);
        editText = layout.getEditText();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(!TextUtils.isEmpty(s));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(editText.getText()))
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                    .setEnabled(false);
    }
}
