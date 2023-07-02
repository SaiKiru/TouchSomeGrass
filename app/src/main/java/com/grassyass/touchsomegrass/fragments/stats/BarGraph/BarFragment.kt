package com.grassyass.touchsomegrass.fragments.stats.BarGraph

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.grassyass.touchsomegrass.R

private const val ARG_HEIGHT = "height"

class BarFragment : Fragment() {
    private var height: Int? = null
    private lateinit var view: View
    private lateinit var bar: FrameLayout
    private var listener: OnTouchListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            height = it.getInt(ARG_HEIGHT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_bar, container, false)
        bar = view.findViewById(R.id.bar)

        (bar.layoutParams as FrameLayout.LayoutParams).apply {
            height = this@BarFragment.height!!
        }

        (view.layoutParams as LinearLayout.LayoutParams).apply {
            weight = 1.0f
            height = LinearLayout.LayoutParams.MATCH_PARENT
            gravity = Gravity.BOTTOM
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (listener != null) {
            view.setOnTouchListener(listener)
        }
    }

    fun setOnTouchListener(listener: OnTouchListener) {
        if (::view.isInitialized) {
            view.setOnTouchListener(listener)
        } else {
            this.listener = listener
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(height: Int) = BarFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_HEIGHT, height)
            }
        }
    }
}