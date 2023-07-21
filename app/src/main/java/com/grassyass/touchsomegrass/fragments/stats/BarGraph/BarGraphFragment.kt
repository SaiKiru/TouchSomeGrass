package com.grassyass.touchsomegrass.fragments.stats.BarGraph

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.fragments.stats.HoverLabelFragment
import com.grassyass.touchsomegrass.utils.dpToPx

class BarGraphFragment : Fragment() {
    private var values: ArrayList<Number> = arrayListOf()
    private var labels: ArrayList<String> = arrayListOf()
    private lateinit var barsLL: LinearLayout
    private lateinit var labelsLL: LinearLayout
    private lateinit var barLabelsCL: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bar_graph, container, false)

        barsLL = view.findViewById(R.id.bars_ll)
        labelsLL = view.findViewById(R.id.labels_ll)
        barLabelsCL = view.findViewById(R.id.barLabels_cl)
        val skyline = dpToPx(requireContext(), 150)

        view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                createBars(values, skyline)
                createLabels(labels)

                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        return view
    }

    private fun createBarLabel(value: Number, left: Int, top: Int): FrameLayout {
        val fragmentContainer = FrameLayout(requireContext())
        val barLabel = HoverLabelFragment.newInstance(value.toString())
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        val fragmentContainerId = View.generateViewId()
        fragmentContainer.id = fragmentContainerId

        val transaction = parentFragmentManager.beginTransaction()
        transaction.add(fragmentContainer.id, barLabel)
        transaction.commitAllowingStateLoss()

        fragmentContainer.layoutParams = layoutParams
        barLabelsCL.addView(fragmentContainer)

        ConstraintSet().apply {
            clone(barLabelsCL)

            connect(fragmentContainerId, ConstraintSet.TOP, barLabelsCL.id, ConstraintSet.TOP)
            connect(fragmentContainerId, ConstraintSet.LEFT, barLabelsCL.id, ConstraintSet.LEFT)

            applyTo(barLabelsCL)
        }

        fragmentContainer.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                layoutParams.apply {
                    setMargins(
                        left - fragmentContainer.width / 2,
                        top - fragmentContainer.height / 2,
                        0,
                        0
                    )
                }

                fragmentContainer.layoutParams = layoutParams
            }
        })

        hideBarLabel(fragmentContainer)

        return fragmentContainer
    }

    private fun hideBarLabel(barLabel: FrameLayout) {
        barLabel.visibility = View.GONE
    }

    private fun showBarLabel(barLabel: FrameLayout) {
        barLabel.visibility = View.VISIBLE
    }

    private fun createBars(values: ArrayList<Number>, skyline: Float) {
        if (values.size == 0) return

        val transaction = parentFragmentManager.beginTransaction()
        val max = values.maxBy { it.toDouble() }

        values.forEachIndexed { idx, value ->
            val barHeight = value.toDouble() / max.toDouble() * skyline
            val bar = BarFragment.newInstance(barHeight.toInt())
            val barAreaWidth = barsLL.width.toDouble() / values.size

            val barLabel = createBarLabel(
                value,
                (barAreaWidth * idx + (barAreaWidth / 2)).toInt(),
                (skyline / 2).toInt()
            )

            bar.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        showBarLabel(barLabel)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        hideBarLabel(barLabel)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        hideBarLabel(barLabel)
                        true
                    }
                    else -> false
                }
            }

            transaction.add(barsLL.id, bar)
        }

        transaction.commitAllowingStateLoss()
    }

    private fun createLabels(labels: ArrayList<String>) {
        for (label in labels) {
            val tv = TextView(context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            tv.text = label
            layoutParams.apply {
                width = 0
                weight = 1.0f
            }
            tv.layoutParams = layoutParams
            tv.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            tv.setTypeface(null, Typeface.BOLD)

            labelsLL.addView(tv)
        }
    }

    fun setData(values: ArrayList<Number>, labels: ArrayList<String>) {
        this.values = values
        this.labels = labels

        clearGraph()

        val skyline = dpToPx(requireContext(), 150)

        createBars(values, skyline)
        createLabels(labels)
    }

    private fun clearGraph() {
        labelsLL.removeAllViews()
        barsLL.removeAllViews()
        barLabelsCL.removeAllViews()
    }
}