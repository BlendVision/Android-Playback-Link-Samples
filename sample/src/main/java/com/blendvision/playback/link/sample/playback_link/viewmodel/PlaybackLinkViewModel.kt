package com.blendvision.playback.link.sample.playback_link.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blendvision.playback.link.common.presentation.entity.BVPlaybackLinkState
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.common.adapter.SessionItem
import com.blendvision.playback.link.sample.main.PlaybackConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class PlaybackLinkViewModel(private val bvPlaybackLinker: BVPlaybackLink) : ViewModel() {

    private val _sessionList = MutableStateFlow<List<SessionItem>>(emptyList())
    val sessionList: StateFlow<List<SessionItem>> = _sessionList

    init {
        viewModelScope.launch {
            observePlaybackLinkEvents()
        }
    }

    fun updatePlaybackToken(token: String) {
        viewModelScope.launch {
            bvPlaybackLinker.updatePlaybackToken("${PlaybackConstants.PRE_FIX}$token")
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

    fun release() {
        viewModelScope.launch {
            bvPlaybackLinker.release()
        }
    }

    private fun logSession(message: String) {
        val newItem = SessionItem(UUID.randomUUID().toString(), message)
        _sessionList.value += newItem
    }

    private suspend fun observePlaybackLinkEvents() {
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
