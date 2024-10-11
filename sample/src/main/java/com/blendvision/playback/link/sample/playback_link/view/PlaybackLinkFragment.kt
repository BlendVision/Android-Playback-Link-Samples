package com.blendvision.playback.link.sample.playback_link.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.common.adapter.SessionAdapter
import com.blendvision.playback.link.sample.common.adapter.SessionItemDiffCallback
import com.blendvision.playback.link.sample.databinding.FragmentPlaybackLinkBinding
import com.blendvision.playback.link.sample.main.PlaybackConstants
import com.blendvision.playback.link.sample.playback_link.viewmodel.PlaybackLinkViewModel
import com.blendvision.playback.link.sample.playback_link.viewmodel.PlaybackLinkViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaybackLinkFragment : Fragment() {

    private var _binding: FragmentPlaybackLinkBinding? = null
    private val binding get() = _binding!!

    private val bvPlaybackLinker: BVPlaybackLink by lazy {
        BVPlaybackLink.Builder(requireContext()).enableDebugMode(false).build()
    }

    private val viewModel: PlaybackLinkViewModel by viewModels {
        PlaybackLinkViewModelFactory(bvPlaybackLinker)
    }

    private val sessionAdapter by lazy { SessionAdapter(SessionItemDiffCallback()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playbackToken = arguments?.getString(PlaybackConstants.PLAYBACK_TOKEN_KEY) ?: ""
        viewModel.updatePlaybackToken(playbackToken)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaybackLinkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.apply {
            getCurrentPlaybackTokenButton.setOnClickListener { viewModel.getCurrentPlaybackToken() }
            startSessionButton.setOnClickListener { viewModel.startSession() }
            endSessionButton.setOnClickListener { viewModel.endSession() }
            getResourceInfoButton.setOnClickListener { viewModel.getResourceInfo() }
        }
    }

    private fun setupRecyclerView() {
        binding.sessionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sessionAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.sessionList.collectLatest { sessionList ->
                sessionAdapter.submitList(sessionList) {
                    binding.sessionRecyclerView.smoothScrollToPosition(sessionAdapter.itemCount)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.endSession()
        viewModel.release()
    }
}
