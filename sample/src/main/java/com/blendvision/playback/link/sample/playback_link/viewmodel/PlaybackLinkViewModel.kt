package com.blendvision.playback.link.sample.playback_link.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blendvision.playback.link.common.presentation.entity.BVPlaybackLinkState
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.common.adapter.SessionItem
import com.blendvision.playback.link.sample.main.PlaybackConstants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlaybackLinkViewModel(private val bvPlaybackLinker: BVPlaybackLink) : ViewModel() {

    private val sessionList = mutableListOf<SessionItem>()
    private val _onSession = MutableSharedFlow<List<SessionItem>>()
    val onSession: SharedFlow<List<SessionItem>> = _onSession

    init {
        viewModelScope.launch {
            observePlaybackLinkEvents()
        }
    }

    fun updatePlaybackToken(token: String) {
        viewModelScope.launch {
            bvPlaybackLinker.updatePlaybackToken(token)
            logSession("Updated playback token: ${bvPlaybackLinker.getCurrentPlaybackToken()}")
        }
    }

    fun getCurrentPlaybackToken() {
        viewModelScope.launch {
            val playbackToken = bvPlaybackLinker.getCurrentPlaybackToken()
            logSession("Current playback token: $playbackToken")
        }
    }

    fun startSession() {
        viewModelScope.launch {
            bvPlaybackLinker.startSession()
            logSession("Session started")
        }
    }

    fun endSession() {
        viewModelScope.launch {
            bvPlaybackLinker.endSession()
            logSession("Session ended")
        }
    }

    fun getResourceInfo() {
        viewModelScope.launch {
            val resourceInfo = bvPlaybackLinker.getResourceInfo()
            logSession("Resource info: $resourceInfo")
        }
    }

    fun getPlaybackSessionInfo() {
        viewModelScope.launch {
            val playbackSessionInfo = bvPlaybackLinker.getPlaybackSessionInfo()
            logSession("Playback session info: $playbackSessionInfo")
        }
    }

    fun release() {
        viewModelScope.launch {
            bvPlaybackLinker.release()
        }
    }

    private suspend fun logSession(message: String) {
        val newItem = SessionItem(UUID.randomUUID().toString(), message)
        sessionList.add(newItem)
        _onSession.emit(sessionList.toList())
    }

    private suspend fun observePlaybackLinkEvents() {
        bvPlaybackLinker.stateEvent.collect { state ->
            val message = when (state) {
                is BVPlaybackLinkState.GetResourceInfoSuccess -> PlaybackConstants.GET_RESOURCE_INFO_SUCCESS
                is BVPlaybackLinkState.StartPlaybackSessionSuccess -> PlaybackConstants.START_PLAYBACK_SESSION_SUCCESS
                is BVPlaybackLinkState.GetPlaybackSessionInfoSuccess -> PlaybackConstants.GET_PLAYBACK_SESSION_INFO_SUCCESS
                is BVPlaybackLinkState.StartHeartbeatSuccess -> PlaybackConstants.START_HEARTBEAT_SUCCESS
                is BVPlaybackLinkState.EndPlaybackSessionSuccess -> PlaybackConstants.END_PLAYBACK_SESSION_SUCCESS
                is BVPlaybackLinkState.Failure -> state.bvPlaybackLinkError.message
            }
            logSession(message)
        }
    }
}
