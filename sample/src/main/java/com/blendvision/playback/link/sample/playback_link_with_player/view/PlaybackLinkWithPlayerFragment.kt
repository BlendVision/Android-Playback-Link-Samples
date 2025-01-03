package com.blendvision.playback.link.sample.playback_link_with_player.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blendvision.playback.link.sample.R
import com.blendvision.playback.link.sample.common.adapter.SessionAdapter
import com.blendvision.playback.link.sample.common.adapter.SessionItemDiffCallback
import com.blendvision.playback.link.sample.databinding.FragmentPlaybackLinkWithPlayerBinding
import com.blendvision.playback.link.sample.main.PlaybackConstants
import com.blendvision.playback.link.sample.playback_link_with_player.viewmodel.PlaybackLinkWithPlayerViewModel
import com.blendvision.playback.link.sample.playback_link_with_player.viewmodel.PlaybackLinkWithPlayerViewModelFactory
import com.blendvision.player.playback.presentation.entity.PanelType
import com.blendvision.player.playback.presentation.entity.SettingOptionConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaybackLinkWithPlayerFragment : Fragment() {

    private var _binding: FragmentPlaybackLinkWithPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaybackLinkWithPlayerViewModel by viewModels {
        PlaybackLinkWithPlayerViewModelFactory()
    }

    private val sessionAdapter by lazy { SessionAdapter(SessionItemDiffCallback()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaybackLinkWithPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initBVPlayback(context = requireContext(), viewLifecycleOwner.lifecycle)
        setupViews()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            licenseKeyTextInputView.hint = getString(R.string.input_license_key)
        }
    }

    private fun setupRecyclerView() {
        binding.sessionRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            adapter = sessionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.loadPlayerButton.setOnClickListener {
            val playbackToken = arguments?.getString(PlaybackConstants.PLAYBACK_TOKEN_KEY) ?: ""
            val licenseKey = binding.licenseKeyInputEditText.text.toString()
            viewModel.loadPlayer(playbackToken = playbackToken, licenseKey = licenseKey)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onSession.collectLatest { sessionList ->
                    sessionAdapter.submitList(sessionList) {
                        binding.sessionRecyclerView.smoothScrollToPosition(sessionAdapter.itemCount)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collectLatest { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onPlayerInit.collectLatest { isInit ->
                    if (isInit) configurePlayer()
                }
            }
        }

    }

    private fun configurePlayer() {
        binding.playerView.setupControlPanel(
            autoKeepScreenOnEnabled = true,
            defaultPanelType = PanelType.EMBEDDED,
            disableControlPanel = null
        )
        binding.playerView.configureSettingOption(
            SettingOptionConfig(forceHideAutoPlay = true)
        )
        binding.playerView.setUnifiedPlayer(viewModel.getPlayer())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


