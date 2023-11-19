package com.example.lab1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.log
import kotlin.random.Random as Randomn

var fieldSize = 0
var gameTimer = 0L
lateinit var tableLayout: TableLayout
val random = Random()
var activeCell: Cell? = null
private lateinit var cells: Array<Array<Cell?>>
var points: Int = 0
var activationInterval: Long = 0L
var minIntervalTimer: Double = 0.0
var maxIntervalTimer: Double = 0.0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableLayout = findViewById(R.id.gameField)

        fieldSize = when (intent.getStringExtra("fieldSize")) {
            "3x3" -> 3
            "4x4" -> 4
            "5x5" -> 5
            else -> 3
        }
        gameTimer = when(intent.getStringExtra("timer")){
            "15s" -> 18000L
            "30s" -> 33000L
            "45s" -> 48000L
            else  -> 18000L
        }
        minIntervalTimer = when(intent.getStringExtra("frameChange")){
            "1-1.5s" -> 1.0
            "0.7-1s" -> 0.7
            "0.5-0.7s" -> 0.5
            "0.3-0.6s" -> 0.3
            else  -> 1.0
        }
        maxIntervalTimer = when(intent.getStringExtra("frameChange")){
            "1-1.5s" -> 1.5
            "0.7-1s" -> 1.0
            "0.5-0.7s" -> 0.7
            "0.3-0.6s" -> 0.6
            else  -> 1.5
        }
        generateSquareField(this, tableLayout, fieldSize)

        startRandomActivation()

    }

    fun startTimer(){
        points = 0
        activationInterval = 1000L
        var countdown = 4
        val resultTextView: TextView = findViewById(R.id.textView2)
        val timer = fixedRateTimer("startTimer", false, 0, activationInterval) {
            runOnUiThread {
                Log.d("Activation", "$countdown")
                countdown-=1
                if(countdown!=0){
                    resultTextView.text = "$countdown"
                }else{
                    resultTextView.text = "Start!"
                }
                if (countdown == 0) {
                    cancel()
                }
            }
        }
    }
    fun generateRandomInterval(minMeasure: Double, maxMeasure: Double): Long {
        require(minMeasure >= 0) { "Minimum measure must be non-negative" }
        require(maxMeasure >= minMeasure) { "Maximum measure must be greater than or equal to the minimum measure" }

        val randomValue = Randomn.nextDouble(0.0, maxMeasure - minMeasure)
        val interval = minMeasure + randomValue

        return (interval * 1000).toLong()
    }

    private fun generateSquareField(context: AppCompatActivity, tableLayout: TableLayout, size: Int) {
        var idCounter = 0
        var initialActiveCell: Cell? = null

        cells = Array(size) { arrayOfNulls<Cell>(size) }

        for (i in 0 until size) {
            val row = TableRow(context)
            val layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
            layoutParams.weight = 1f
            row.layoutParams = layoutParams

            for (j in 0 until size) {
                val cell = Cell(context, false, false, idCounter)
                row.addView(cell.getView())
                cells[i][j] = cell
                idCounter++

                if (initialActiveCell == null && random.nextBoolean()) {
                    initialActiveCell = cell
                }
            }

            tableLayout.addView(row)
        }

    }

    private fun startRandomActivation() {
        startTimer()
        activationInterval = generateRandomInterval(minIntervalTimer, maxIntervalTimer)
        var elapsedTime = 0L

        val timer = fixedRateTimer("activationTimer", false, 3000L, activationInterval) {
            runOnUiThread {
                activateRandomCell()

                elapsedTime += activationInterval
                if (elapsedTime >= gameTimer) {
                    Handler().postDelayed({
                        showCustomDialog()
                    }, 0L)
                    cancel()
                }
            }
        }
    }

    private fun goToMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun activateRandomCell() {
        val randomRowIndex = random.nextInt(fieldSize)
        val randomColIndex = random.nextInt(fieldSize)

        if (randomRowIndex >= 0 && randomRowIndex < cells.size) {
            if (randomColIndex >= 0 && randomColIndex < cells[randomRowIndex].size) {
                val cell = cells[randomRowIndex][randomColIndex]
                activeCell?.deactivateCell()
                cell?.activateCell()
                activeCell = cell
            }
        }
    }
    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Results")
        if(points<0){
            points = 0
        }
        builder.setMessage("Your result: $points")

        builder.setPositiveButton("OK") { dialog, which ->
            dialog.dismiss()
            goToMenuActivity()
        }


        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

@SuppressLint("ResourceAsColor")
class Cell(context: Context, var isActive: Boolean, var isSelected: Boolean, val id: Int) {
    private val view: View

    init {
        view = View(context)

        val screenWidth = context.resources.displayMetrics.widthPixels
        val cellSizeInPixels = screenWidth / fieldSize

        val layoutParams = LayoutParams(cellSizeInPixels, cellSizeInPixels)
        layoutParams.weight = 1f
        view.layoutParams = layoutParams
        view.setBackgroundResource(R.drawable.cell_border)

        view.setOnClickListener {
            onCellClick()
        }
    }

    fun getView(): View {
        return view
    }


    fun activateCell() {
        isActive = true
        isSelected = false
        view.setBackgroundResource(R.drawable.active_cell)
    }

    fun deactivateCell() {
        isActive = false
        isSelected = false
        view.setBackgroundResource(R.drawable.cell_border)
    }

    private fun onCellClick() {
        if (isActive) {
            if (!isSelected) {
                points += 2
                isSelected = true
                view.setBackgroundResource(R.drawable.selected_cell)
            }
        }else {
            points -= 1
        }
    }
}
