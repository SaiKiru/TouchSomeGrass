package com.grassyass.touchsomegrass.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class DeleteExerciseConfirmDialog : DialogFragment() {
    private var listener: DeleteExerciseConfirmDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Are you sure you want to delete this exercise?")
                setMessage("This action cannot be undone!")

                setPositiveButton("Yes, I'm sure") { _, _ ->
                    listener?.onDialogPositiveClick()
                }

                setNegativeButton("No") { _, _ ->
                    listener?.onDialogNegativeClick()
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnDialogButtonClickListener(onDialogButtonClickListener: DeleteExerciseConfirmDialogListener) {
        listener = onDialogButtonClickListener
    }

    interface DeleteExerciseConfirmDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }
}