package com.blendvision.playback.link.sample.playback_link_with_player.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaybackLinkWithPlayerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaybackLinkWithPlayerViewModel::class.java)) {
            return PlaybackLinkWithPlayerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}