package com.grassyass.touchsomegrass.fragments.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.grassyass.touchsomegrass.R

private const val ARG_VALUE = "value"

class HoverLabelFragment : Fragment() {
    private var value: String? = null
    private lateinit var valueTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            value = it.getString(ARG_VALUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hover_label, container, false)

        valueTV = view.findViewById(R.id.value_tv)
        valueTV.text = value

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(value: String) =
            HoverLabelFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VALUE, value)
                }
            }
    }
}