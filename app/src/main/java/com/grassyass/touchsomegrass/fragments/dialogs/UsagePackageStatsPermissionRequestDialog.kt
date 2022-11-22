package com.grassyass.touchsomegrass.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

class UsagePackageStatsPermissionRequestDialog : DialogFragment() {
    private var listener: UsagePackageStatsPermissionRequestDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity).apply {
                setTitle("Request for Permissions")
                setMessage("For this app to work correctly, it will need to read other app's usage")

                setPositiveButton("Enable") { _, _ ->
                    listener?.onDialogPositiveClick()
                }

                setNegativeButton("Cancel") { _, _ ->
                    listener?.onDialogNegativeClick()
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setOnDialogButtonClickListener(onDialogButtonClickListener: UsagePackageStatsPermissionRequestDialogListener) {
        listener = onDialogButtonClickListener
    }

    interface UsagePackageStatsPermissionRequestDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }
}