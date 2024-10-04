package com.blendvision.playback.link.sample.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blendvision.playback.link.core.presentation.BVPlaybackLink

class MainViewModelFactory(private val bvPlaybackLinker: BVPlaybackLink) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(bvPlaybackLinker) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}