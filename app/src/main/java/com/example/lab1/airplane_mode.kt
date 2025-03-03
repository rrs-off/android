package com.example.lab1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class airplane_mode : Fragment() {

    private lateinit var txtStatus: TextView
    private lateinit var btnToggle: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_airplane_mode, container, false)

        txtStatus = view.findViewById(R.id.txtAirplaneStatus)
        btnToggle = view.findViewById(R.id.btnToggleAirplane)

        updateAirplaneStatus()

        btnToggle.setOnClickListener {
            openAirplaneSettings()
        }

        return view
    }

    private fun updateAirplaneStatus() {
        val isAirplaneModeOn = Settings.Global.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0

        if (isAirplaneModeOn) {
            btnToggle.text = "ON"
            btnToggle.setBackgroundColor(Color.RED)
        } else {
            btnToggle.text = "OFF"
            btnToggle.setBackgroundColor(Color.GREEN)
        }
    }

    private fun openAirplaneSettings() {
        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        updateAirplaneStatus()
    }
}
