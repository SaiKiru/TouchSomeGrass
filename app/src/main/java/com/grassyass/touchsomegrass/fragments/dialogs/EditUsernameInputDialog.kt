package com.grassyass.touchsomegrass.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class EditUsernameInputDialog : DialogFragment() {
    private var listener: EditUsernameInputDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val usernameField = EditText(activity)
            usernameField.inputType = InputType.TYPE_CLASS_TEXT

            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Edit username")
                setView(usernameField)

                setPositiveButton("Save") { _, _ ->
                    listener?.onDialogPositiveClick(usernameField.text.toString())
                }

                setNegativeButton("Cancel") { _, _ ->
                    listener?.onDialogNegativeClick()
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnDialogButtonClickListener(onDialogButtonClickListener: EditUsernameInputDialogListener) {
        listener = onDialogButtonClickListener
    }

    interface EditUsernameInputDialogListener {
        fun onDialogPositiveClick(value: String)
        fun onDialogNegativeClick()
    }
}