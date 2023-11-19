package com.example.lab1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import com.example.lab1.R.*

class MenuActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_menu)

        var selectedFieldSize = "3x3"
        var selectedTimer = "15s"
        var selectedFrameChange = "1-1.5s"
        var timer : Long = 0
        val fieldSizeSpinner = findViewById<Spinner>(id.field_size_selector)
        val timerSpinner = findViewById<Spinner>(id.timer_selector)
        val frameChangeSpinner = findViewById<Spinner>(id.frame_change_selector)
        val startButton = findViewById<Button>(id.startButton)

        val fieldSizes = resources.getStringArray(array.fieldSizes)
        val timers = resources.getStringArray(array.timers)
        val frameChanges = resources.getStringArray(array.frameChanges)

        val fieldSizesAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, fieldSizes)
        val timerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, timers)
        val frameChangesAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item, frameChanges)

        fieldSizeSpinner.adapter = fieldSizesAdapter
        timerSpinner.adapter = timerAdapter
        frameChangeSpinner.adapter = frameChangesAdapter

        fieldSizeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedFieldSize = fieldSizes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        timerSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedTimer = timers[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        frameChangeSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedFrameChange = frameChanges[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        startButton.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("fieldSize", selectedFieldSize)
            intent.putExtra("timer", selectedTimer)
            intent.putExtra("frameChange", selectedFrameChange)

            startActivity(intent)
            finish()
        }

    }

}