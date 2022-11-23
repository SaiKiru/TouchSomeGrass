package com.grassyass.touchsomegrass.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grassyass.touchsomegrass.R
import com.grassyass.touchsomegrass.data.models.OnboardingData

class OnboardingPagerAdapter(
    private val context: Context,
    private val onboardingData: List<OnboardingData>
) : RecyclerView.Adapter<OnboardingPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_onboarding_page_item, parent, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pageData = onboardingData[position]

        holder.pageHeader.text = pageData.header
        holder.pageDescription.text = pageData.description
        holder.pageIllustration.setImageDrawable(pageData.illustration)
    }

    override fun getItemCount(): Int {
        return onboardingData.size
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val pageHeader = view.findViewById<TextView>(R.id.page_header)
        val pageDescription = view.findViewById<TextView>(R.id.page_description)
        val pageIllustration = view.findViewById<ImageView>(R.id.page_illustration)
    }
}