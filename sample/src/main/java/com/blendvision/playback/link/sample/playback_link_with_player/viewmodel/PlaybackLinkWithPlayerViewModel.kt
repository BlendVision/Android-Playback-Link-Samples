package com.blendvision.playback.link.sample.playback_link_with_player.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blendvision.playback.link.common.presentation.entity.BVPlaybackLinkState
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.common.adapter.SessionItem
import com.blendvision.playback.link.sample.main.PlaybackConstants
import com.blendvision.player.common.presentation.entity.analytics.AnalyticsConfig
import com.blendvision.player.playback.presentation.UniPlayer
import com.blendvision.player.playback.presentation.delegate.StateEventListener
import com.blendvision.player.playback.presentation.entity.MediaConfig
import com.blendvision.player.playback.presentation.entity.PlaybackState
import com.blendvision.player.playback.presentation.entity.PlayerConfig
import com.blendvision.player.playback.presentation.error.EventCallback
import com.blendvision.player.playback.presentation.error.UniErrorEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlaybackLinkWithPlayerViewModel(
    private val bvPlaybackLinker: BVPlaybackLink
) : ViewModel() {

    private val sessionList = mutableListOf<SessionItem>()
    private val _onSession = MutableSharedFlow<List<SessionItem>>()
    val onSession: SharedFlow<List<SessionItem>> = _onSession

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    private val _onPlayerInit = MutableSharedFlow<Boolean>()
    val onPlayerInit: SharedFlow<Boolean> = _onPlayerInit

    private var player: UniPlayer? = null

    private val eventCallback: EventCallback = object : EventCallback {
        override fun onUniError(errorEvent: UniErrorEvent): Boolean {
            bvPlaybackLinker.endSession()
            return false
        }
    }

    private val stateEventListener = object : StateEventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: PlaybackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {
                PlaybackState.IDLE -> Unit
                PlaybackState.BUFFERING -> Unit
                PlaybackState.READY -> {
                    if (playWhenReady) {
                        bvPlaybackLinker.startSession()
                    } else {
                        bvPlaybackLinker.endSession()
                    }
                }

                PlaybackState.ENDED -> {
                    bvPlaybackLinker.endSession()
                }
            }
        }
    }

    init {
        observeEvents()
    }

    fun updatePlaybackToken(token: String) {
        viewModelScope.launch {
            bvPlaybackLinker.updatePlaybackToken("${PlaybackConstants.PRE_FIX}$token")
            logSession("update playback token: ${bvPlaybackLinker.getCurrentPlaybackToken()}")
        }
    }

    fun initPlayer(context: Context, licenseKey: String) {
        viewModelScope.launch {
            if (licenseKey.isEmpty()) {
                _toastMessage.emit("Please input license key")
                return@launch
            }

            val resourceInfo = bvPlaybackLinker.getResourceInfo() ?: return@launch

            val analyticsConfig = AnalyticsConfig.Builder()
                .setResourceId(resourceInfo.id)
                .setResourceType(resourceInfo.type)
                .build()

            val playerConfig = PlayerConfig(
                licenseKey = licenseKey,
                serviceConfig = PlayerConfig.ServiceConfig(
                    licenseVersion = PlayerConfig.ServiceConfig.LicenseVersion.V2
                )
            )

            player = UniPlayer.Builder(
                context = context,
                playerConfig = playerConfig
            ).setAnalyticsConfig(analyticsConfig).build().apply {
                addEventListener(eventCallback)
                addStateEventListener(stateEventListener)
            }

            logSession(PlaybackConstants.INIT_PLAYER)

            _onPlayerInit.emit(true)
        }
    }

    fun loadPlayer(mpdUrl: String) {
        viewModelScope.launch {
            if (mpdUrl.isEmpty()) {
                _toastMessage.emit("Please input mpd url")
                return@launch
            }
            val mediaConfig = MediaConfig(
                source = MediaConfig.Source(url = mpdUrl, protocol = MediaConfig.Protocol.DASH),
                title = "NEW_CONTENT_TITLE",
                imageUrl = "NEW_COVER_IMAGE",
                playWhenReady = true,
                sharedUrl = "NEW_SHARED_INFO",
                description = "NEW_DESC_INFO"
            )
            player?.load(mediaConfig)?.let {
                logSession(PlaybackConstants.LOAD_PLAYER)
            }
        }
    }

    fun startPlayer() {
        viewModelScope.launch {
            player?.start()?.let {
                logSession(PlaybackConstants.START_PLAYER)
            }
        }
    }

    fun stopPlayer() {
        viewModelScope.launch {
            player?.stop()?.let {
                logSession(PlaybackConstants.STOP_PLAYER)
            }
        }
    }

    fun release() {
        viewModelScope.launch {
            player?.release()?.let {
                bvPlaybackLinker.release()
            }
            player = null
        }
    }

    fun getPlayer(): UniPlayer? = player

    private suspend fun logSession(message: String) {
        val newItem = SessionItem(UUID.randomUUID().toString(), message)
        sessionList.add(newItem)
        _onSession.emit(sessionList.toList())
    }

    private fun observeEvents() {
        viewModelScope.launch {
            bvPlaybackLinker.stateEvent.collect { state ->
                val message = when (state) {
                    is BVPlaybackLinkState.GetResourceInfoSuccess -> PlaybackConstants.GET_RESOURCE_INFO_SUCCESS
                    is BVPlaybackLinkState.StartPlaybackSessionSuccess -> PlaybackConstants.START_PLAYBACK_SESSION_SUCCESS
                    is BVPlaybackLinkState.StartHeartbeatSuccess -> PlaybackConstants.START_HEARTBEAT_SUCCESS
                    is BVPlaybackLinkState.EndPlaybackSessionSuccess -> PlaybackConstants.END_PLAYBACK_SESSION_SUCCESS
                    is BVPlaybackLinkState.Failure -> state.bvPlaybackLinkError.message
                }
                logSession(message)
            }
        }
    }

}
