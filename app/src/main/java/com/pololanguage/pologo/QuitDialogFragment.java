package com.pololanguage.pologo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class QuitDialogFragment extends DialogFragment {

  public interface QuitDialogListener {
    public void onDialogPositiveClick();
    public void onDialogNeutralClick();
    //public void onDialogNegativeClick();
  }

  QuitDialogListener listener;

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      listener = (QuitDialogListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement QuitDialogListener");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    builder.setPositiveButton(R.string.quit, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        listener.onDialogPositiveClick();
      }
    });

    builder.setNeutralButton(R.string.new_game, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        listener.onDialogNeutralClick();
      }
    });

    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        // Do nothing
      }
    });

    return builder.create();
  }
}
