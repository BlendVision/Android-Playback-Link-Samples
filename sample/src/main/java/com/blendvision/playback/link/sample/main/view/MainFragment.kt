package com.blendvision.playback.link.sample.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blendvision.playback.link.sample.R
import com.blendvision.playback.link.sample.databinding.FragmentMainBinding
import com.blendvision.playback.link.sample.main.PlaybackConstants

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.playbackTokenTextInputView.hint = getString(R.string.input_playback_token)

        binding.goPlaybackLinkFragmentButton.setOnClickListener {
            val bundle = Bundle()
            val playbackToken = binding.playbackTokenInputEditText.text.toString()
            bundle.putString(PlaybackConstants.PLAYBACK_TOKEN_KEY, playbackToken)
            findNavController().navigate(R.id.action_mainFragment_to_playbackLinkFragment, bundle)
        }
        binding.goPlaybackLinkWithPlayerFragmentButton.setOnClickListener {
            val bundle = Bundle()
            val playbackToken = binding.playbackTokenInputEditText.text.toString()
            bundle.putString(PlaybackConstants.PLAYBACK_TOKEN_KEY, playbackToken)
            findNavController().navigate(R.id.action_mainFragment_to_playbackLinkWithPlayerFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}