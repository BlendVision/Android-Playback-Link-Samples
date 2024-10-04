package com.blendvision.playback.link.sample.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blendvision.playback.link.core.presentation.BVPlaybackLink
import com.blendvision.playback.link.sample.R
import com.blendvision.playback.link.sample.adapter.SessionAdapter
import com.blendvision.playback.link.sample.adapter.SessionItemDiffCallback
import com.blendvision.playback.link.sample.databinding.ActivityMainBinding
import com.blendvision.playback.link.sample.viewmodel.MainViewModel
import com.blendvision.playback.link.sample.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val bvPlaybackLinker by lazy {
        BVPlaybackLink.Builder(this).enableDebugMode(true).build()
    }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(bvPlaybackLinker)
    }

    private val sessionAdapter by lazy {
        SessionAdapter(SessionItemDiffCallback())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        initRecyclerView()
        initObserve()
    }

    private fun initView() {
        binding.playbackTokenTextInputView.hint = getString(R.string.input_playback_token)

        binding.updatePlaybackTokenButton.setOnClickListener {
            val playbackToken = binding.playbackTokenInputEditText.text.toString()
            mainViewModel.updatePlaybackToken(playbackToken)
        }

        binding.resetPlaybackTokenButton.setOnClickListener {
            binding.playbackTokenInputEditText.text?.clear()
            mainViewModel.resetPlaybackToken()
        }

        binding.startSessionButton.setOnClickListener {
            mainViewModel.startSession()
        }

        binding.endSessionButton.setOnClickListener {
            mainViewModel.endSession()
        }

        binding.getResourceInfoButton.setOnClickListener {
            mainViewModel.getResourceInfo()
        }

        binding.releaseSessionButton.setOnClickListener {
            mainViewModel.release()
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        binding.sessionRecyclerView.layoutManager = layoutManager
        binding.sessionRecyclerView.adapter = sessionAdapter
    }

    private fun initObserve() {
        lifecycleScope.launch {
            mainViewModel.sessionList.collect { sessionList ->
                sessionAdapter.submitList(sessionList){
                    binding.sessionRecyclerView.smoothScrollToPosition(sessionAdapter.itemCount)
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}