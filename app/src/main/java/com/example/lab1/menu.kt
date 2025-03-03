package com.example.lab1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class menu : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val btnExtra1 = view.findViewById<Button>(R.id.btn_extra1)
        val btnExtra2 = view.findViewById<Button>(R.id.btn_extra2)
        val btnExtra3 = view.findViewById<Button>(R.id.btn_extra3)
        val btnExtra4 = view.findViewById<Button>(R.id.btn_extra4)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnExtra1.setOnClickListener { openFragment(IntentsandDeepLinking())
            bottomNav.selectedItemId = R.id.nav_idl}
        btnExtra2.setOnClickListener { openFragment(Music())
            bottomNav.selectedItemId = R.id.nav_music}
        btnExtra3.setOnClickListener { openFragment(airplane_mode())
            bottomNav.selectedItemId = R.id.nav_airplane}
        btnExtra4.setOnClickListener { openFragment(calendar_events())
            bottomNav.selectedItemId = R.id.nav_calendar}

        return view
    }

    private fun openFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
