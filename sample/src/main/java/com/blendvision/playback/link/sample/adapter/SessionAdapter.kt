package com.blendvision.playback.link.sample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.blendvision.playback.link.sample.databinding.ViewSessionItemBinding
import com.blendvision.playback.link.sample.viewholder.SessionViewHolder

class SessionAdapter(sessionItemDiffCallback: SessionItemDiffCallback) : ListAdapter<SessionItem, SessionViewHolder>(sessionItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SessionViewHolder(
            ViewSessionItemBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}