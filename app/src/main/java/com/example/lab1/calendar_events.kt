package com.example.lab1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class calendar_events : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: eventadapter
    private lateinit var calendarView: CalendarView
    private lateinit var btnAddEvent: Button
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_calendar_events, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recycler_view)
        btnAddEvent = view.findViewById(R.id.btn_add_event)

        recyclerView.layoutManager = LinearLayoutManager(context)
        eventAdapter = eventadapter(ArrayList())
        recyclerView.adapter = eventAdapter

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_CALENDAR), PERMISSION_REQUEST_CODE
            )
        } else {
            loadCalendarEvents()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
            val selectedDate = formatDate(year, month, dayOfMonth)
            Log.d("CalendarFragment", "selected date: $selectedDate")
            loadEventsForDate(selectedDate)
        }

        btnAddEvent.setOnClickListener { addEventToCalendar() }

        return view
    }

    private fun loadEventsForDate(date: String) {
        val events = getEventsForDate(date)
        Log.d("CalendarFragment", "event for date $date: $events")
        eventAdapter.updateData(events)
    }

    private fun getEventsForDate(date: String): List<String> {
        val eventList = mutableListOf<String>()

        val startTime = getStartOfDayMillis(date)
        val endTime = startTime + 86400000

        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART)
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} < ?"
        val selectionArgs = arrayOf(startTime.toString(), endTime.toString())
        val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

        val cursor: Cursor? = requireContext().contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)

        cursor?.use {
            while (it.moveToNext()) {
                val eventTitle = it.getString(0)
                eventList.add(eventTitle)
            }
        }

        return eventList
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCalendarEvents()
        }
    }

    private fun loadCalendarEvents() {
        val eventList = mutableListOf<String>()
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART)

        val cursor: Cursor? = requireContext().contentResolver.query(uri, projection, null, null, "${CalendarContract.Events.DTSTART} ASC")

        cursor?.use {
            while (it.moveToNext()) {
                val eventTitle = it.getString(0)
                val eventDate = it.getLong(1)
                eventList.add("$eventTitle - ${Date(eventDate)}")
            }
        }

        eventAdapter.updateData(eventList)
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
    }

    private fun getStartOfDayMillis(dateString: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString)?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun addEventToCalendar() {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "new event")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "place")
            putExtra(CalendarContract.Events.DESCRIPTION, "...")
            putExtra(CalendarContract.Events.ALL_DAY, true)

            val startMillis = getStartOfDayMillis(formatDate(selectedYear, selectedMonth, selectedDay))
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        }

        try {
            startActivity(intent)
            Handler().postDelayed({ loadEventsForDate(formatDate(selectedYear, selectedMonth, selectedDay)) }, 2000)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open calendar", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
