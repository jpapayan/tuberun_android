package com.papagiannis.tuberun.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.papagiannis.tuberun.R;

public class AlertDialogFragment extends DialogFragment {
	public static AlertDialogFragment newInstance(String title, String msg) {
		AlertDialogFragment frag = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", msg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String msg = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_dialog_alert_holo_dark)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	dismiss();
                        }
                    }
                )
                .create();
    }
}
