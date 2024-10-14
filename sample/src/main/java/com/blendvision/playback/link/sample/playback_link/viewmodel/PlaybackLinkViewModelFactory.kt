package com.blendvision.playback.link.sample.playback_link.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blendvision.playback.link.core.presentation.BVPlaybackLink

class PlaybackLinkViewModelFactory(private val bvPlaybackLinker: BVPlaybackLink) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaybackLinkViewModel::class.java)) {
            return PlaybackLinkViewModel(bvPlaybackLinker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}