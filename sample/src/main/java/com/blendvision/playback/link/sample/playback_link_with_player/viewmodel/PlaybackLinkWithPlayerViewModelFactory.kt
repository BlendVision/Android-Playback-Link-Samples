package com.blendvision.playback.link.sample.playback_link_with_player.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blendvision.playback.link.core.presentation.BVPlaybackLink

class PlaybackLinkWithPlayerViewModelFactory(private val bvPlaybackLinker: BVPlaybackLink) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaybackLinkWithPlayerViewModel::class.java)) {
            return PlaybackLinkWithPlayerViewModel(bvPlaybackLinker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}