package com.example.taskscheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddTaskDialogFragment extends DialogFragment {
    private EditText editTaskTitle;
    private EditText editTaskDetail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        // Inflate and set the layout for the dialog
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String username = editTaskTitle.getText().toString();
                        String password = editTaskDetail.getText().toString();
                        listener.applyTexts(username, password);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        editTaskTitle = view.findViewById(R.id.task_title);
        editTaskDetail = view.findViewById(R.id.task_detail);
        return builder.create();
    }


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface TaskDialogListener {
        void applyTexts(String taskTitle, String taskDetail);
    }
    // Use this instance of the interface to deliver action events
    TaskDialogListener listener;
    // Override the Fragment.onAttach() method to instantiate the TaskDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the TaskDialogListener so we can send events to the host
            listener = (TaskDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + "must implement TaskDialogListener");
        }
    }





}
