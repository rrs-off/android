package com.example.lab1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection

import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class Music : Fragment() {
    private lateinit var seekBar: SeekBar
    private var handler = Handler(Looper.getMainLooper())
    private var musicService: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            isBound = true
            setupSeekBar()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)

        val startButton: Button = view.findViewById(R.id.btnStart)
        val pauseButton: Button = view.findViewById(R.id.btnPause)
        val stopButton: Button = view.findViewById(R.id.btnStop)
        val songTitle: TextView = view.findViewById(R.id.txtMusicTitle)
        seekBar = view.findViewById(R.id.seekBar)

        songTitle.text = "Keipker"

        startButton.setOnClickListener { controlMusicService("START") }
        pauseButton.setOnClickListener { controlMusicService("PAUSE") }
        stopButton.setOnClickListener { controlMusicService("STOP") }

        return view
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), MusicService::class.java)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
    }

    private fun controlMusicService(action: String) {
        val intent = Intent(requireContext(), MusicService::class.java)
        intent.action = action
        requireContext().startService(intent)
    }

    private fun setupSeekBar() {
        musicService?.let {
            seekBar.max = it.getDuration()
            handler.postDelayed(updateSeekBar, 1000)
        }
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            musicService?.let {
                seekBar.progress = it.getCurrentPosition()
                handler.postDelayed(this, 1000)
            }
        }
    }
}
