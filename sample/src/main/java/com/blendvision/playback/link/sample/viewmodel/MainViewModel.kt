package com.blendvision.playback.link.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blendvision.playback.link.common.presentation.entity.BVPlaybackLinkState
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.adapter.SessionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val bvPlaybackLinker: BVPlaybackLink) : ViewModel() {

    private val _sessionList = MutableStateFlow<List<SessionItem>>(emptyList())
    val sessionList: StateFlow<List<SessionItem>> = _sessionList

    init {
        observeErrorEvents()
    }

    fun updatePlaybackToken(token: String) {
        bvPlaybackLinker.updatePlaybackToken("$PRE_FIX$token")
        addSessionItem("Current playback token: ${bvPlaybackLinker.getCurrentPlaybackToken()}")
    }

    fun resetPlaybackToken() {
        bvPlaybackLinker.updatePlaybackToken("")
        addSessionItem("Current playback token: ${bvPlaybackLinker.getCurrentPlaybackToken()}")
    }

    fun startSession() {
        bvPlaybackLinker.startSession()
        addSessionItem("Start session")
    }

    fun endSession() {
        bvPlaybackLinker.endSession()
        addSessionItem("End session")
    }

    fun getResourceInfo() {
        viewModelScope.launch {
            val resourceInfo = bvPlaybackLinker.getResourceInfo()
            addSessionItem("Resource info: $resourceInfo")
        }
    }

    fun release() {
        bvPlaybackLinker.release()
        addSessionItem("Release")
    }

    private fun addSessionItem(text: String) {
        val newList = _sessionList.value.toMutableList()
        newList.add(SessionItem(UUID.randomUUID().toString(), text))
        _sessionList.value = newList
    }

    private fun observeErrorEvents() {
        viewModelScope.launch {
            bvPlaybackLinker.stateEvent.collect { state ->
                val message = when (state) {
                    is BVPlaybackLinkState.GetResourceInfoSuccess -> GET_RESOURCE_INFO_SUCCESS
                    is BVPlaybackLinkState.StartPlaybackSessionSuccess -> START_PLAYBACK_SESSION_SUCCESS
                    is BVPlaybackLinkState.StartHeartbeatSuccess -> START_HEARTBEAT_SUCCESS
                    is BVPlaybackLinkState.EndPlaybackSessionSuccess -> END_PLAYBACK_SESSION_SUCCESS
                    is BVPlaybackLinkState.Failure -> state.bvPlaybackLinkError.message
                }
                addSessionItem(message)
            }
        }
    }

    companion object{
        private const val PRE_FIX = "Bearer "
        private const val GET_RESOURCE_INFO_SUCCESS = "Get resource info success"
        private const val START_PLAYBACK_SESSION_SUCCESS = "Start playback session success"
        private const val START_HEARTBEAT_SUCCESS = "Start heartbeat success"
        private const val END_PLAYBACK_SESSION_SUCCESS = "End playback session success"
    }

}