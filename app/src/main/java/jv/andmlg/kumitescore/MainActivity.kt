package jv.andmlg.kumitescore

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private var timer: CountDownTimer? = null
    private var timerPlayer: MediaPlayer? = null

    //region UI Elements
    private var leftSideBackground: LinearLayout? = null
    private var rightSideBackground: LinearLayout? = null

    private var txtTimer: AppCompatTextView? = null

    private var txtLeftScore: AppCompatTextView? = null
    private var txtRightScore: AppCompatTextView? = null
    private var btnLeftSenshu: ImageView? = null
    private var btnRightSenshu: ImageView? = null

    private var btnLeftAdd: ImageButton? = null
    private var btnRightAdd: ImageButton? = null
    private var btnLeftSubs: ImageButton? = null
    private var btnRightSubs: ImageButton? = null

    private var btnLeftSubsCat1: ImageButton? = null
    private var btnLeftSubsCat2: ImageButton? = null
    private var btnLeftAddCat1: ImageButton? = null
    private var btnLeftAddCat2: ImageButton? = null
    private var btnRightSubsCat1: ImageButton? = null
    private var btnRightSubsCat2: ImageButton? = null
    private var btnRightAddCat1: ImageButton? = null
    private var btnRightAddCat2: ImageButton? = null

    private var imgLeftCcat1: ImageView? = null
    private var imgLeftKcat1: ImageView? = null
    private var imgLeftHCcat1: ImageView? = null
    private var imgLeftHcat1: ImageView? = null
    private var imgRightCcat1: ImageView? = null
    private var imgRightKcat1: ImageView? = null
    private var imgRightHCcat1: ImageView? = null
    private var imgRightHcat1: ImageView? = null
    private var imgLeftCcat2: ImageView? = null
    private var imgLeftKcat2: ImageView? = null
    private var imgLeftHCcat2: ImageView? = null
    private var imgLeftHcat2: ImageView? = null
    private var imgRightCcat2: ImageView? = null
    private var imgRightKcat2: ImageView? = null
    private var imgRightHCcat2: ImageView? = null
    private var imgRightHcat2: ImageView? = null

    private var btnSound: ImageButton? = null
    private var btnSwap: ImageButton? = null
    private var btnEraser: ImageButton? = null
    //endregion

    private var timerMin = 2
    private var timerSec = 0
    private var timerPlaying = false

    private var sound = true
    private var leftSideColor = "B"
    private var rightSideColor = "R"

    private var leftScore = 0
    private var rightScore = 0
    private var leftSenshu = false
    private var rightSenshu = false

    private var leftCat1 = 0
    private var leftCat2 = 0
    private var rightCat1 = 0
    private var rightCat2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        txtTimer?.setOnClickListener {
            if (!timerPlaying) timerStart()
            else timerStop()
        }
        txtTimer?.setOnLongClickListener {
            openTimerPicker()
            true
        }

        btnLeftAdd?.setOnClickListener {
            if (leftScore < 99) leftScore += 1
            printLeftScore(leftScore)
        }
        btnLeftSubs?.setOnClickListener {
            if (leftScore > 0) leftScore -= 1
            printLeftScore(leftScore)
        }

        btnRightAdd?.setOnClickListener {
            if (rightScore < 99) rightScore += 1
            printRightScore(rightScore)
        }
        btnRightSubs?.setOnClickListener {
            if (rightScore > 0) rightScore -= 1
            printRightScore(rightScore)
        }

        btnLeftSenshu?.setOnClickListener {
            if (!leftSenshu && !rightSenshu) leftSenshu = true
            else if (leftSenshu) leftSenshu = false
            printSenshu(leftSenshu, rightSenshu)
        }
        btnRightSenshu?.setOnClickListener {
            if (!rightSenshu && !leftSenshu) rightSenshu = true
            else if (rightSenshu) rightSenshu = false
            printSenshu(leftSenshu, rightSenshu)
        }

        btnLeftAddCat1?.setOnClickListener {
            if (leftCat1 < 4) leftCat1 += 1
            printLeftCat1(leftCat1)
        }
        btnLeftSubsCat1?.setOnClickListener {
            if (leftCat1 > 0) leftCat1 -= 1
            printLeftCat1(leftCat1)
        }
        btnLeftAddCat2?.setOnClickListener {
            if (leftCat2 < 4) leftCat2 += 1
            printLeftCat2(leftCat2)
        }
        btnLeftSubsCat2?.setOnClickListener {
            if (leftCat2 > 0) leftCat2 -= 1
            printLeftCat2(leftCat2)
        }

        btnRightAddCat1?.setOnClickListener {
            if (rightCat1 < 4) rightCat1 += 1
            printRightCat1(rightCat1)
        }
        btnRightSubsCat1?.setOnClickListener {
            if (rightCat1 > 0) rightCat1 -= 1
            printRightCat1(rightCat1)
        }
        btnRightAddCat2?.setOnClickListener {
            if (rightCat2 < 4) rightCat2 += 1
            printRightCat2(rightCat2)
        }
        btnRightSubsCat2?.setOnClickListener {
            if (rightCat2 > 0) rightCat2 -= 1
            printRightCat2(rightCat2)
        }

        btnSound?.setOnClickListener {
            sound = !sound
            printSound()

            val editor: SharedPreferences.Editor =
                getSharedPreferences("KumiteScore", MODE_PRIVATE).edit()
            editor.putBoolean("sound", sound).apply()
        }
        btnSwap?.setOnClickListener {
            val tmpColor = leftSideColor; leftSideColor = rightSideColor; rightSideColor = tmpColor
            printBackgroundColors(leftSideColor, rightSideColor)

            val tmpSenshu = leftSenshu; leftSenshu = rightSenshu; rightSenshu = tmpSenshu
            printSenshu(leftSenshu, rightSenshu)

            val tmpScore = leftScore; leftScore = rightScore; rightScore = tmpScore
            printLeftScore(leftScore)
            printRightScore(rightScore)

            val tmpCat1 = leftCat1; leftCat1 = rightCat1; rightCat1 = tmpCat1
            printLeftCat1(leftCat1)
            printRightCat1(rightCat1)

            val tmpCat2 = leftCat2; leftCat2 = rightCat2; rightCat2 = tmpCat2
            printLeftCat2(leftCat2)
            printRightCat2(rightCat2)
        }
        btnEraser?.setOnClickListener {
            showEraseMenu()
        }
    }

    private fun findViews() {
        leftSideBackground = findViewById(R.id.leftSide)
        rightSideBackground = findViewById(R.id.rightSide)
        txtTimer = findViewById(R.id.matchTimer)
        txtLeftScore = findViewById(R.id.textLeft_Score)
        txtRightScore = findViewById(R.id.textRight_Score)
        btnLeftSenshu = findViewById(R.id.buttonLeft_Senshu)
        btnRightSenshu = findViewById(R.id.buttonRight_Senshu)
        btnLeftAdd = findViewById(R.id.buttonLeft_Add)
        btnRightAdd = findViewById(R.id.buttonRight_Add)
        btnLeftSubs = findViewById(R.id.buttonLeft_Subs)
        btnRightSubs = findViewById(R.id.buttonRight_Subs)
        btnLeftSubsCat1 = findViewById(R.id.buttonLeft_SubsCat1)
        btnLeftSubsCat2 = findViewById(R.id.buttonLeft_SubsCat2)
        btnLeftAddCat1 = findViewById(R.id.buttonLeft_AddCat1)
        btnLeftAddCat2 = findViewById(R.id.buttonLeft_AddCat2)
        btnRightSubsCat1 = findViewById(R.id.buttonRight_SubsCat1)
        btnRightSubsCat2 = findViewById(R.id.buttonRight_SubsCat2)
        btnRightAddCat1 = findViewById(R.id.buttonRight_AddCat1)
        btnRightAddCat2 = findViewById(R.id.buttonRight_AddCat2)
        imgLeftCcat1 = findViewById(R.id.left_C_cat1)
        imgLeftKcat1 = findViewById(R.id.left_K_cat1)
        imgLeftHCcat1 = findViewById(R.id.left_HC_cat1)
        imgLeftHcat1 = findViewById(R.id.left_H_cat1)
        imgRightCcat1 = findViewById(R.id.right_C_cat1)
        imgRightKcat1 = findViewById(R.id.right_K_cat1)
        imgRightHCcat1 = findViewById(R.id.right_HC_cat1)
        imgRightHcat1 = findViewById(R.id.right_H_cat1)
        imgLeftCcat2 = findViewById(R.id.left_C_cat2)
        imgLeftKcat2 = findViewById(R.id.left_K_cat2)
        imgLeftHCcat2 = findViewById(R.id.left_HC_cat2)
        imgLeftHcat2 = findViewById(R.id.left_H_cat2)
        imgRightCcat2 = findViewById(R.id.right_C_cat2)
        imgRightKcat2 = findViewById(R.id.right_K_cat2)
        imgRightHCcat2 = findViewById(R.id.right_HC_cat2)
        imgRightHcat2 = findViewById(R.id.right_H_cat2)
        btnSound = findViewById(R.id.button_Sound)
        btnSwap = findViewById(R.id.button_Swap)
        btnEraser = findViewById(R.id.button_Eraser)
    }

    override fun onStart() {
        super.onStart()
        timerPlayer = MediaPlayer.create(this, R.raw.whistle)

        sound = getSharedPreferences("KumiteScore", MODE_PRIVATE).getBoolean("sound", true)
        printSound()
    }

    override fun onStop() {
        super.onStop()
        if (timerPlayer != null) {
            timerPlayer?.release()
            timerPlayer = null
        }
    }

    private fun showEraseMenu() {
        val eraseMenu = PopupMenu(this, btnEraser!!)
        eraseMenu.menuInflater.inflate(R.menu.reset_menu, eraseMenu.menu)
        eraseMenu.setForceShowIcon(true)
        eraseMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_reset_all -> resetAll()
                R.id.action_reset_score -> resetScore()
                R.id.action_reset_faults -> resetFaults()
                R.id.action_reset_timer -> resetTime()
            }
            true
        }
        eraseMenu.show()
    }

    private fun openTimerPicker() {
        timerStop()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.timer_picker_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)
        val pickerMinutes = dialogView.findViewById<NumberPicker>(R.id.picker_minutes)
        pickerMinutes.wrapSelectorWheel = false
        pickerMinutes.maxValue = 4
        pickerMinutes.minValue = 0
        pickerMinutes.value = timerMin
        val pickerSeconds = dialogView.findViewById<NumberPicker>(R.id.picker_seconds)
        pickerSeconds.setFormatter { String.format("%02d", it); }
        pickerSeconds.wrapSelectorWheel = false
        pickerSeconds.maxValue = 59
        pickerSeconds.minValue = 0
        pickerSeconds.value = timerSec

        dialogBuilder.setNegativeButton(resources.getString(R.string.picker_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(resources.getString(R.string.picker_save)) { _, _ ->
            timerMin = pickerMinutes.value
            timerSec = pickerSeconds.value
            printTimer(timerMin, timerSec)
        }
        dialogBuilder.create().show()
    }

    private fun printSound() {
        if (sound) btnSound?.setImageResource(R.drawable.sound)
        else btnSound?.setImageResource(R.drawable.sound_off)
    }

    private fun printLeftScore(ls: Int) {
        txtLeftScore?.text = ls.toString()
    }

    private fun printRightScore(rs: Int) {
        txtRightScore?.text = rs.toString()
    }

    private fun printSenshu(ls: Boolean, rs: Boolean) {
        if (ls) btnLeftSenshu?.setImageResource(R.drawable.circle)
        else btnLeftSenshu?.setImageResource(R.drawable.circle_empty)
        if (rs) btnRightSenshu?.setImageResource(R.drawable.circle)
        else btnRightSenshu?.setImageResource(R.drawable.circle_empty)
    }

    private fun printLeftCat1(lc1: Int) {
        when (lc1) {
            0 -> {
                imgLeftCcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftKcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftHCcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat1?.setImageResource(R.drawable.circle_empty)
            }
            1 -> {
                imgLeftCcat1?.setImageResource(R.drawable.circle)
                imgLeftKcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftHCcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat1?.setImageResource(R.drawable.circle_empty)
            }
            2 -> {
                imgLeftCcat1?.setImageResource(R.drawable.circle)
                imgLeftKcat1?.setImageResource(R.drawable.circle)
                imgLeftHCcat1?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat1?.setImageResource(R.drawable.circle_empty)
            }
            3 -> {
                imgLeftCcat1?.setImageResource(R.drawable.circle)
                imgLeftKcat1?.setImageResource(R.drawable.circle)
                imgLeftHCcat1?.setImageResource(R.drawable.circle)
                imgLeftHcat1?.setImageResource(R.drawable.circle_empty)
            }
            4 -> {
                imgLeftCcat1?.setImageResource(R.drawable.circle)
                imgLeftKcat1?.setImageResource(R.drawable.circle)
                imgLeftHCcat1?.setImageResource(R.drawable.circle)
                imgLeftHcat1?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printLeftCat2(lc2: Int) {
        when (lc2) {
            0 -> {
                imgLeftCcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftKcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftHCcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat2?.setImageResource(R.drawable.circle_empty)
            }
            1 -> {
                imgLeftCcat2?.setImageResource(R.drawable.circle)
                imgLeftKcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftHCcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat2?.setImageResource(R.drawable.circle_empty)
            }
            2 -> {
                imgLeftCcat2?.setImageResource(R.drawable.circle)
                imgLeftKcat2?.setImageResource(R.drawable.circle)
                imgLeftHCcat2?.setImageResource(R.drawable.circle_empty)
                imgLeftHcat2?.setImageResource(R.drawable.circle_empty)
            }
            3 -> {
                imgLeftCcat2?.setImageResource(R.drawable.circle)
                imgLeftKcat2?.setImageResource(R.drawable.circle)
                imgLeftHCcat2?.setImageResource(R.drawable.circle)
                imgLeftHcat2?.setImageResource(R.drawable.circle_empty)
            }
            4 -> {
                imgLeftCcat2?.setImageResource(R.drawable.circle)
                imgLeftKcat2?.setImageResource(R.drawable.circle)
                imgLeftHCcat2?.setImageResource(R.drawable.circle)
                imgLeftHcat2?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printRightCat1(rc1: Int) {
        when (rc1) {
            0 -> {
                imgRightCcat1?.setImageResource(R.drawable.circle_empty)
                imgRightKcat1?.setImageResource(R.drawable.circle_empty)
                imgRightHCcat1?.setImageResource(R.drawable.circle_empty)
                imgRightHcat1?.setImageResource(R.drawable.circle_empty)
            }
            1 -> {
                imgRightCcat1?.setImageResource(R.drawable.circle)
                imgRightKcat1?.setImageResource(R.drawable.circle_empty)
                imgRightHCcat1?.setImageResource(R.drawable.circle_empty)
                imgRightHcat1?.setImageResource(R.drawable.circle_empty)
            }
            2 -> {
                imgRightCcat1?.setImageResource(R.drawable.circle)
                imgRightKcat1?.setImageResource(R.drawable.circle)
                imgRightHCcat1?.setImageResource(R.drawable.circle_empty)
                imgRightHcat1?.setImageResource(R.drawable.circle_empty)
            }
            3 -> {
                imgRightCcat1?.setImageResource(R.drawable.circle)
                imgRightKcat1?.setImageResource(R.drawable.circle)
                imgRightHCcat1?.setImageResource(R.drawable.circle)
                imgRightHcat1?.setImageResource(R.drawable.circle_empty)
            }
            4 -> {
                imgRightCcat1?.setImageResource(R.drawable.circle)
                imgRightKcat1?.setImageResource(R.drawable.circle)
                imgRightHCcat1?.setImageResource(R.drawable.circle)
                imgRightHcat1?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printRightCat2(rc2: Int) {
        when (rc2) {
            0 -> {
                imgRightCcat2?.setImageResource(R.drawable.circle_empty)
                imgRightKcat2?.setImageResource(R.drawable.circle_empty)
                imgRightHCcat2?.setImageResource(R.drawable.circle_empty)
                imgRightHcat2?.setImageResource(R.drawable.circle_empty)
            }
            1 -> {
                imgRightCcat2?.setImageResource(R.drawable.circle)
                imgRightKcat2?.setImageResource(R.drawable.circle_empty)
                imgRightHCcat2?.setImageResource(R.drawable.circle_empty)
                imgRightHcat2?.setImageResource(R.drawable.circle_empty)
            }
            2 -> {
                imgRightCcat2?.setImageResource(R.drawable.circle)
                imgRightKcat2?.setImageResource(R.drawable.circle)
                imgRightHCcat2?.setImageResource(R.drawable.circle_empty)
                imgRightHcat2?.setImageResource(R.drawable.circle_empty)
            }
            3 -> {
                imgRightCcat2?.setImageResource(R.drawable.circle)
                imgRightKcat2?.setImageResource(R.drawable.circle)
                imgRightHCcat2?.setImageResource(R.drawable.circle)
                imgRightHcat2?.setImageResource(R.drawable.circle_empty)
            }
            4 -> {
                imgRightCcat2?.setImageResource(R.drawable.circle)
                imgRightKcat2?.setImageResource(R.drawable.circle)
                imgRightHCcat2?.setImageResource(R.drawable.circle)
                imgRightHcat2?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printBackgroundColors(lc: String, rc: String) {
        if (lc.contentEquals("B")) leftSideBackground?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.kumite_blue
            )
        )
        else if (lc.contentEquals("R")) leftSideBackground?.setBackgroundColor(
            ContextCompat.getColor(this, R.color.kumite_red)
        )

        if (rc.contentEquals("B")) rightSideBackground?.setBackgroundColor(
            ContextCompat.getColor(
                this, R.color.kumite_blue
            )
        )
        else if (rc.contentEquals("R")) rightSideBackground?.setBackgroundColor(
            ContextCompat.getColor(this, R.color.kumite_red)
        )
    }

    private fun printTimer(min: Int, sec: Int) {
        val minutes = min.toString()
        var seconds = sec.toString()
        if (sec < 10) seconds = "0".plus(seconds)
        txtTimer?.text = "$minutes:$seconds"
    }

    private fun resetAll() {
        resetScore()
        resetFaults()
        resetTime()
    }

    private fun resetScore() {
        leftScore = 0
        rightScore = 0
        leftSenshu = false
        rightSenshu = false
        printLeftScore(leftScore)
        printRightScore(rightScore)
        printSenshu(leftSenshu, rightSenshu)
    }

    private fun resetFaults() {
        leftCat1 = 0
        leftCat2 = 0
        rightCat1 = 0
        rightCat2 = 0
        printLeftCat1(leftCat1)
        printLeftCat2(leftCat2)
        printRightCat1(rightCat1)
        printRightCat2(rightCat2)
    }

    private fun resetTime() {
        timerStop()
        timerMin = 2
        timerSec = 0
        printTimer(timerMin, timerSec)
    }

    private fun timerStart() {
        if (timerMin > 0 || timerSec > 0) {
            timerPlaying = true
            val totalMillis = (timerMin * 60 * 1000) + (timerSec * 1000)

            timer = object : CountDownTimer(totalMillis.toLong(), 250) {
                override fun onTick(millisUntilFinished: Long) {
                    timerMin = ((millisUntilFinished / 1000) / 60).toInt()
                    timerSec = ((millisUntilFinished / 1000) % 60).toInt()
                    printTimer(timerMin, timerSec)
                    if (timerMin == 0 && timerSec == 15 && sound) playSound()
                }

                override fun onFinish() {
                    if (sound) playSound()
                }
            }
            timer?.start()
        }
    }

    private fun timerStop() {
        timer?.cancel()
        timerPlaying = false
    }

    private fun playSound() {
        timerPlayer?.start()
    }
}
