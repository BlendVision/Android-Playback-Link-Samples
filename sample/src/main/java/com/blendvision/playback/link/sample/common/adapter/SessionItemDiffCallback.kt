package com.blendvision.playback.link.sample.common.adapter

import androidx.recyclerview.widget.DiffUtil

class SessionItemDiffCallback : DiffUtil.ItemCallback<SessionItem>() {
    override fun areItemsTheSame(oldItem: SessionItem, newItem: SessionItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SessionItem, newItem: SessionItem): Boolean {
        return oldItem.text == newItem.text
    }
}