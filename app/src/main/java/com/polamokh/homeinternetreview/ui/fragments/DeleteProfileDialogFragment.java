package com.polamokh.homeinternetreview.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.polamokh.homeinternetreview.R;
import com.polamokh.homeinternetreview.ui.listeners.IOnDialogButtonClickListener;

public class DeleteProfileDialogFragment extends DialogFragment {
    private IOnDialogButtonClickListener listener;

    public DeleteProfileDialogFragment(IOnDialogButtonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_delete_profile_title)
                .setMessage(R.string.dialog_delete_profile_message)
                .setPositiveButton(R.string.dialog_delete_profile_delete_btn, (dialog, which) -> {
                    listener.OnPositiveButtonClicked(null);
                })
                .setNegativeButton(R.string.dialog_delete_profile_cancel_btn, (dialog, which) -> {
                    dialog.cancel();
                });
        return builder.create();
    }
}
