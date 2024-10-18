package com.blendvision.playback.link.sample.playback_link_with_player.viewmodel

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blendvision.playback.link.core.playback.presentation.BVPlayback
import com.blendvision.playback.link.core.playback.presentation.entity.BVPlaybackState
import com.blendvision.playback.link.core.playback.presentation.listener.BVPlaybackStateListener
import com.blendvision.playback.link.sample.common.adapter.SessionItem
import com.blendvision.player.playback.presentation.UniPlayer
import com.blendvision.player.playback.presentation.entity.PlayerConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlaybackLinkWithPlayerViewModel : ViewModel() {

    private val sessionList = mutableListOf<SessionItem>()

    private val _onSession = MutableSharedFlow<List<SessionItem>>()
    val onSession: SharedFlow<List<SessionItem>> = _onSession.asSharedFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val _onPlayerInit = MutableSharedFlow<Boolean>()
    val onPlayerInit: SharedFlow<Boolean> = _onPlayerInit.asSharedFlow()

    private var bvPlayback: BVPlayback? = null
    private var player: UniPlayer? = null

    private val bvPlaybackStateListener: BVPlaybackStateListener = object : BVPlaybackStateListener {
        override fun onStateChanged(bvPlaybackState: BVPlaybackState) {
            when (bvPlaybackState) {
                is BVPlaybackState.Failure -> sendToast(bvPlaybackState.bvPlaybackError.message)
                is BVPlaybackState.InitiatePlayerSuccess -> {
                    viewModelScope.launch {
                        player = bvPlaybackState.player
                        _onPlayerInit.emit(true)
                    }
                }

                else -> Unit
            }
            logSession(bvPlaybackState.toString())
        }
    }

    fun initBVPlayback(context: Context, lifecycle: Lifecycle) {
        bvPlayback = BVPlayback.Builder(context = context, lifecycle = lifecycle)
            .enableDebugMode(true)
            .create(bvPlaybackStateListener)
    }

    fun loadPlayer(playbackToken: String, mpdUrl: String, licenseKey: String) {
        viewModelScope.launch {
            if (playbackToken.isEmpty()) {
                sendToast("Please input playback token")
                return@launch
            }
            if (licenseKey.isEmpty()) {
                sendToast("Please input license key")
                return@launch
            }
            if (mpdUrl.isEmpty()) {
                sendToast("Please input mpd url")
                return@launch
            }

            val playerConfig = PlayerConfig(
                licenseKey = licenseKey,
                serviceConfig = PlayerConfig.ServiceConfig(
                    licenseVersion = PlayerConfig.ServiceConfig.LicenseVersion.V2
                )
            )
            bvPlayback?.load(playbackToken = playbackToken, playerConfig = playerConfig)
        }
    }

    fun getPlayer(): UniPlayer? = player

    private fun logSession(message: String) {
        viewModelScope.launch {
            val newItem = SessionItem(UUID.randomUUID().toString(), message)
            sessionList.add(newItem)
            _onSession.emit(sessionList.toList())
        }
    }

    // Sending a toast message
    private fun sendToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
