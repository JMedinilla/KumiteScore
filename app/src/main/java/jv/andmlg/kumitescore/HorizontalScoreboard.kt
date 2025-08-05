package jv.andmlg.kumitescore

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.button.MaterialButtonToggleGroup
import com.nambimobile.widgets.efab.ExpandableFab
import com.nambimobile.widgets.efab.FabOption
import java.util.Locale

class HorizontalScoreboard : AppCompatActivity() {

    //region UI Elements
    private var leftSideBackground: LinearLayout? = null
    private var rightSideBackground: LinearLayout? = null

    private var txtTimer: AppCompatTextView? = null

    private var txtLeftScore: AppCompatTextView? = null
    private var txtRightScore: AppCompatTextView? = null
    private var txtLeftIppons: AppCompatTextView? = null
    private var txtLeftWazas: AppCompatTextView? = null
    private var txtLeftYukos: AppCompatTextView? = null
    private var txtRightIppons: AppCompatTextView? = null
    private var txtRightWazas: AppCompatTextView? = null
    private var txtRightYukos: AppCompatTextView? = null

    private var btnLeftSenshu: ImageView? = null
    private var btnRightSenshu: ImageView? = null

    private var btnLeftPointsMain: ExpandableFab? = null
    private var btnRightPointsMain: ExpandableFab? = null

    private var btnLeftIppon: FabOption? = null
    private var btnLeftWaza: FabOption? = null
    private var btnLeftYuko: FabOption? = null
    private var btnLeftEdit: FabOption? = null
    private var btnRightIppon: FabOption? = null
    private var btnRightWaza: FabOption? = null
    private var btnRightYuko: FabOption? = null
    private var btnRightEdit: FabOption? = null

    private var btnLeftChui1: ImageView? = null
    private var btnLeftChui2: ImageView? = null
    private var btnLeftChui3: ImageView? = null
    private var btnLeftHC: ImageView? = null
    private var btnLeftH: ImageView? = null
    private var btnRightChui1: ImageView? = null
    private var btnRightChui2: ImageView? = null
    private var btnRightChui3: ImageView? = null
    private var btnRightHC: ImageView? = null
    private var btnRightH: ImageView? = null

    private var btnSettings: ImageButton? = null
    private var btnSwap: ImageButton? = null
    private var btnReset: ImageButton? = null
    //endregion

    private var timer: CountDownTimer? = null
    private var timerPlayer: MediaPlayer? = null
    private var timerPlayerShort: MediaPlayer? = null

    private var timerMin = 2
    private var timerSec = 0
    private var timerPlaying = false
    private var atoShibaraku = 15

    private var sound = true
    private var leftSideColor = "B"
    private var rightSideColor = "R"

    private var leftScore = 0
    private var rightScore = 0
    private var leftChui = 0
    private var rightChui = 0
    private var leftSenshu = false
    private var rightSenshu = false

    private var leftIppons = 0
    private var leftWazas = 0
    private var leftYukos = 0
    private var rightIppons = 0
    private var rightWazas = 0
    private var rightYukos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.horizontal_activity_main)
        findViews()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        txtTimer?.setOnClickListener {
            if (!timerPlaying) timerStart()
            else timerStop()
        }
        txtTimer?.setOnLongClickListener {
            openTimerPicker()
            true
        }

        btnLeftIppon?.setOnClickListener {
            if (leftScore < 99) leftScore += 3; leftIppons += 1
            printLeftScore(leftScore)
            printLeftIppons(leftIppons)
        }
        btnLeftWaza?.setOnClickListener {
            if (leftScore < 99) leftScore += 2; leftWazas += 1
            printLeftScore(leftScore)
            printLeftWazas(leftWazas)
        }
        btnLeftYuko?.setOnClickListener {
            if (leftScore < 99) leftScore += 1; leftYukos += 1
            printLeftScore(leftScore)
            printLeftYukos(leftYukos)
        }
        btnLeftEdit?.setOnClickListener {
            openPointsPicker(true)
        }
        btnRightIppon?.setOnClickListener {
            if (rightScore < 99) rightScore += 3; rightIppons += 1
            printRightScore(rightScore)
            printRightIppons(rightIppons)
        }
        btnRightWaza?.setOnClickListener {
            if (rightScore < 99) rightScore += 2; rightWazas += 1
            printRightScore(rightScore)
            printRightWazas(rightWazas)
        }
        btnRightYuko?.setOnClickListener {
            if (rightScore < 99) rightScore += 1; rightYukos += 1
            printRightScore(rightScore)
            printRightYukos(rightYukos)
        }
        btnRightEdit?.setOnClickListener {
            openPointsPicker(false)
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

        btnLeftChui1?.setOnClickListener {
            leftChui = if (leftChui == 1) {
                0
            } else {
                1
            }; printLeftChui(leftChui)
        }
        btnLeftChui2?.setOnClickListener {
            leftChui = if (leftChui == 2) {
                0
            } else {
                2
            }; printLeftChui(leftChui)
        }
        btnLeftChui3?.setOnClickListener {
            leftChui = if (leftChui == 3) {
                0
            } else {
                3
            }; printLeftChui(leftChui)
        }
        btnLeftHC?.setOnClickListener {
            leftChui = if (leftChui == 4) {
                0
            } else {
                4
            }; printLeftChui(leftChui)
        }
        btnLeftH?.setOnClickListener {
            leftChui = if (leftChui == 5) {
                0
            } else {
                5
            }; printLeftChui(leftChui)
        }
        btnRightChui1?.setOnClickListener {
            rightChui = if (rightChui == 1) {
                0
            } else {
                1
            }; printRightChui(rightChui)
        }
        btnRightChui2?.setOnClickListener {
            rightChui = if (rightChui == 2) {
                0
            } else {
                2
            }; printRightChui(rightChui)
        }
        btnRightChui3?.setOnClickListener {
            rightChui = if (rightChui == 3) {
                0
            } else {
                3
            }; printRightChui(rightChui)
        }
        btnRightHC?.setOnClickListener {
            rightChui = if (rightChui == 4) {
                0
            } else {
                4
            }; printRightChui(rightChui)
        }
        btnRightH?.setOnClickListener {
            rightChui = if (rightChui == 5) {
                0
            } else {
                5
            }; printRightChui(rightChui)
        }

        btnSettings?.setOnClickListener {
            openSettingsMenu()
        }
        btnReset?.setOnClickListener {
            val popup = LayoutInflater.from(this).inflate(R.layout.reset_confirm, null)
            popup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val confirm = popup.findViewById<ImageView>(R.id.popConfirm)
            val popupHeight = popup.measuredHeight

            val pop = PopupWindow(
                popup,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            confirm.setOnClickListener {
                resetAll()
                pop.dismiss()
            }

            val location = IntArray(2); it.getLocationOnScreen(location)

            pop.elevation = 20f
            pop.showAtLocation(it, Gravity.NO_GRAVITY, location[0], location[1] - popupHeight)
        }
        btnSwap?.setOnClickListener {
            val tmpColor = leftSideColor; leftSideColor = rightSideColor; rightSideColor = tmpColor
            printBackgroundColors(leftSideColor, rightSideColor)
            val tmpSenshu = leftSenshu; leftSenshu = rightSenshu; rightSenshu = tmpSenshu
            printSenshu(leftSenshu, rightSenshu)
            val tmpScore = leftScore; leftScore = rightScore; rightScore = tmpScore
            printLeftScore(leftScore); printRightScore(rightScore)
            val tmpIppons = leftIppons; leftIppons = rightIppons; rightIppons = tmpIppons
            printLeftIppons(leftIppons); printRightIppons(rightIppons)
            val tmpWazas = leftWazas; leftWazas = rightWazas; rightWazas = tmpWazas
            printLeftWazas(leftWazas); printRightWazas(rightWazas)
            val tmpYukos = leftYukos; leftYukos = rightYukos; rightYukos = tmpYukos
            printLeftYukos(leftYukos); printRightYukos(rightYukos)
            val tmpChui = leftChui; leftChui = rightChui; rightChui = tmpChui
            printLeftChui(leftChui); printRightChui(rightChui)
        }

        readDefaultValues()
        resetAll()
    }

    private fun findViews() {
        leftSideBackground = findViewById(R.id.leftSideScreen)
        rightSideBackground = findViewById(R.id.rightSideScreen)
        txtTimer = findViewById(R.id.mainMatchTimer)
        txtLeftScore = findViewById(R.id.leftScore)
        txtRightScore = findViewById(R.id.rightScore)
        txtLeftIppons = findViewById(R.id.textLeft_Ippon)
        txtLeftWazas = findViewById(R.id.textLeft_Waza)
        txtLeftYukos = findViewById(R.id.textLeft_Yuko)
        txtRightIppons = findViewById(R.id.textRight_Ippon)
        txtRightWazas = findViewById(R.id.textRight_Waza)
        txtRightYukos = findViewById(R.id.textRight_Yuko)

        btnLeftSenshu = findViewById(R.id.leftSenshuButton)
        btnRightSenshu = findViewById(R.id.rightSenshuButton)

        btnLeftPointsMain = findViewById(R.id.left_PointsMain)
        btnRightPointsMain = findViewById(R.id.right_PointsMain)
        btnLeftIppon = findViewById(R.id.left_PointsIppon)
        btnLeftWaza = findViewById(R.id.left_PointsWaza)
        btnLeftYuko = findViewById(R.id.left_PointsYuko)
        btnLeftEdit = findViewById(R.id.left_PointsEdit)
        btnRightIppon = findViewById(R.id.right_PointsIppon)
        btnRightWaza = findViewById(R.id.right_PointsWaza)
        btnRightYuko = findViewById(R.id.right_PointsYuko)
        btnRightEdit = findViewById(R.id.right_PointsEdit)

        btnLeftChui1 = findViewById(R.id.left_1C)
        btnLeftChui2 = findViewById(R.id.left_2C)
        btnLeftChui3 = findViewById(R.id.left_3C)
        btnLeftHC = findViewById(R.id.left_HC)
        btnLeftH = findViewById(R.id.left_H)
        btnRightChui1 = findViewById(R.id.right_1C)
        btnRightChui2 = findViewById(R.id.right_2C)
        btnRightChui3 = findViewById(R.id.right_3C)
        btnRightHC = findViewById(R.id.right_HC)
        btnRightH = findViewById(R.id.right_H)

        btnSettings = findViewById(R.id.barSettings)
        btnSwap = findViewById(R.id.barSwap)
        btnReset = findViewById(R.id.barReset)
    }

    override fun onStart() {
        super.onStart()
        timerPlayer = MediaPlayer.create(this, R.raw.whistle)
        timerPlayerShort = MediaPlayer.create(this, R.raw.beep)

        sound = getSharedPreferences("KumiteScore", MODE_PRIVATE).getBoolean("sound", true)
    }

    override fun onStop() {
        super.onStop()
        if (timerPlayer != null) {
            timerPlayer?.release()
            timerPlayer = null
        }
        if (timerPlayerShort != null) {
            timerPlayerShort?.release()
            timerPlayerShort = null
        }
    }

    private fun readDefaultValues() {
        timerMin = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defMin", 2)
        timerSec = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defSec", 0)
        atoShibaraku = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defAto", 15)
    }

    private fun openSettingsMenu() {
        val dialogView = layoutInflater.inflate(R.layout.options_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val optionTime: Button? = dialogView.findViewById(R.id.option_Clock)
        val optionAto: MaterialButtonToggleGroup? = dialogView.findViewById(R.id.AtoToggle)
        val optionSound: SwitchCompat? = dialogView.findViewById(R.id.option_Sound)
        val soundIcon: ImageView? = dialogView.findViewById(R.id.soundIcon)

        val min = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defMin", 2).toString()
        val s = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defSec", 0)
        var sec = s.toString()
        if (s < 10) sec = "0".plus(s)
        optionTime?.text = getString(R.string.time_left, min, sec)

        optionSound?.isChecked = sound
        if (sound) soundIcon?.setImageResource(R.drawable.sound)
        else soundIcon?.setImageResource(R.drawable.sound_off)

        if (atoShibaraku == 15) optionAto?.check(R.id.option_Ato15)
        else optionAto?.check(R.id.option_Ato10)

        optionTime?.setOnClickListener {
            openDefaultPicker()
            dialog.dismiss()
        }
        optionSound?.setOnCheckedChangeListener { _, isChecked ->
            sound = isChecked
            if (sound) soundIcon?.setImageResource(R.drawable.sound)
            else soundIcon?.setImageResource(R.drawable.sound_off)
            getSharedPreferences("KumiteScore", MODE_PRIVATE).edit().putBoolean("sound", sound)
                .apply()
        }
        optionAto?.addOnButtonCheckedListener { _, checkedId, isCheked ->
            if (isCheked) {
                when (checkedId) {
                    R.id.option_Ato15 -> atoShibaraku = 15
                    R.id.option_Ato10 -> atoShibaraku = 10
                }
                getSharedPreferences("KumiteScore", MODE_PRIVATE).edit()
                    .putInt("defAto", atoShibaraku).apply()
            }
        }

        dialog.show()
    }

    private fun openTimerPicker() {
        timerStop()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.timer_picker_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)
        val pickerMinutes = dialogView.findViewById<NumberPicker>(R.id.picker_minutes)
        pickerMinutes.minValue = 0
        pickerMinutes.maxValue = 4
        pickerMinutes.wrapSelectorWheel = false
        pickerMinutes.value = timerMin
        val pickerSeconds = dialogView.findViewById<NumberPicker>(R.id.picker_seconds)
        pickerSeconds.setFormatter { String.format(Locale.US, "%02d", it); }
        pickerSeconds.minValue = 0
        pickerSeconds.maxValue = 59
        pickerSeconds.wrapSelectorWheel = true
        pickerSeconds.value = timerSec

        dialogBuilder.setNegativeButton(resources.getString(R.string.picker_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(resources.getString(R.string.picker_save)) { _, _ ->
            timerMin = pickerMinutes.value
            timerSec = pickerSeconds.value
            printTimer(timerMin, timerSec)
        }
        dialogBuilder.create().show()
    }

    private fun openPointsPicker(ls: Boolean) {
        var tmpI: Int
        var tmpW: Int
        var tmpY: Int

        if (ls) {
            tmpI = leftIppons; tmpW = leftWazas; tmpY = leftYukos
        } else {
            tmpI = rightIppons; tmpW = rightWazas; tmpY = rightYukos
        }

        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.points_picker_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)

        val pkIppon = dialogView.findViewById<NumberPicker>(R.id.pickIppon)
        pkIppon.minValue = 0; pkIppon.maxValue = 30;
        pkIppon.wrapSelectorWheel = false; pkIppon.value = tmpI

        val pkWaza = dialogView.findViewById<NumberPicker>(R.id.pickWaza)
        pkWaza.minValue = 0; pkWaza.maxValue = 30;
        pkWaza.wrapSelectorWheel = false; pkWaza.value = tmpW

        val pkYuko = dialogView.findViewById<NumberPicker>(R.id.pickYuko)
        pkYuko.minValue = 0; pkYuko.maxValue = 30;
        pkYuko.wrapSelectorWheel = false; pkYuko.value = tmpY

        dialogBuilder.setNegativeButton(resources.getString(R.string.picker_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(resources.getString(R.string.picker_save)) { _, _ ->
            if (ls) {
                leftIppons = pkIppon.value; leftWazas = pkWaza.value; leftYukos = pkYuko.value
                leftScore = (leftIppons * 3) + (leftWazas * 2) + leftYukos

                printLeftIppons(leftIppons)
                printLeftWazas(leftWazas)
                printLeftYukos(leftYukos)
                printLeftScore(leftScore)
            } else {
                rightIppons = pkIppon.value; rightWazas = pkWaza.value; rightYukos = pkYuko.value
                rightScore = (rightIppons * 3) + (rightWazas * 2) + rightYukos

                printRightIppons(rightIppons)
                printRightWazas(rightWazas)
                printRightYukos(rightYukos)
                printRightScore(rightScore)
            }
        }
        dialogBuilder.create().show()
    }

    private fun openDefaultPicker() {
        timerStop()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.timer_picker_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setView(dialogView)
        val picketTitle = dialogView.findViewById<TextView>(R.id.picker_title)
        picketTitle.setText(R.string.pickerTitle_default)
        val pickerMinutes = dialogView.findViewById<NumberPicker>(R.id.picker_minutes)
        pickerMinutes.minValue = 0
        pickerMinutes.maxValue = 4
        pickerMinutes.wrapSelectorWheel = false
        pickerMinutes.value = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defMin", 2)
        val pickerSeconds = dialogView.findViewById<NumberPicker>(R.id.picker_seconds)
        pickerSeconds.setFormatter { String.format(Locale.US, "%02d", it); }
        pickerSeconds.minValue = 0
        pickerSeconds.maxValue = 59
        pickerSeconds.wrapSelectorWheel = true
        pickerSeconds.value = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defSec", 0)

        dialogBuilder.setNegativeButton(resources.getString(R.string.picker_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(resources.getString(R.string.picker_save)) { _, _ ->
            val editor: SharedPreferences.Editor =
                getSharedPreferences("KumiteScore", MODE_PRIVATE).edit()
            editor.putInt("defMin", pickerMinutes.value).apply()
            editor.putInt("defSec", pickerSeconds.value).apply()
        }
        dialogBuilder.create().show()
    }

    private fun printLeftScore(ls: Int) {
        var score: Int = ls
        if (ls > 99) score = 99
        txtLeftScore?.text = String.format(Locale.US, "%d", score)
    }

    private fun printRightScore(rs: Int) {
        var score: Int = rs
        if (rs > 99) score = 99
        txtRightScore?.text = String.format(Locale.US, "%d", score)
    }

    private fun printLeftIppons(li: Int) {
        txtLeftIppons?.text = getString(R.string.PlcHldr, li)
    }

    private fun printLeftWazas(lw: Int) {
        txtLeftWazas?.text = getString(R.string.PlcHldr, lw)
    }

    private fun printLeftYukos(ly: Int) {
        txtLeftYukos?.text = getString(R.string.PlcHldr, ly)
    }

    private fun printRightIppons(ri: Int) {
        txtRightIppons?.text = getString(R.string.PlcHldr, ri)
    }

    private fun printRightWazas(rw: Int) {
        txtRightWazas?.text = getString(R.string.PlcHldr, rw)
    }

    private fun printRightYukos(ry: Int) {
        txtRightYukos?.text = getString(R.string.PlcHldr, ry)
    }

    private fun printSenshu(ls: Boolean, rs: Boolean) {
        if (ls) btnLeftSenshu?.setImageResource(R.drawable.circle)
        else btnLeftSenshu?.setImageResource(R.drawable.circle_empty)
        if (rs) btnRightSenshu?.setImageResource(R.drawable.circle)
        else btnRightSenshu?.setImageResource(R.drawable.circle_empty)
    }

    private fun printLeftChui(lc1: Int) {
        when (lc1) {
            0 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle_empty)
                btnLeftChui2?.setImageResource(R.drawable.circle_empty)
                btnLeftChui3?.setImageResource(R.drawable.circle_empty)
                btnLeftHC?.setImageResource(R.drawable.circle_empty)
                btnLeftH?.setImageResource(R.drawable.circle_empty)
            }

            1 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle)
                btnLeftChui2?.setImageResource(R.drawable.circle_empty)
                btnLeftChui3?.setImageResource(R.drawable.circle_empty)
                btnLeftHC?.setImageResource(R.drawable.circle_empty)
                btnLeftH?.setImageResource(R.drawable.circle_empty)
            }

            2 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle)
                btnLeftChui2?.setImageResource(R.drawable.circle)
                btnLeftChui3?.setImageResource(R.drawable.circle_empty)
                btnLeftHC?.setImageResource(R.drawable.circle_empty)
                btnLeftH?.setImageResource(R.drawable.circle_empty)
            }

            3 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle)
                btnLeftChui2?.setImageResource(R.drawable.circle)
                btnLeftChui3?.setImageResource(R.drawable.circle)
                btnLeftHC?.setImageResource(R.drawable.circle_empty)
                btnLeftH?.setImageResource(R.drawable.circle_empty)
            }

            4 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle)
                btnLeftChui2?.setImageResource(R.drawable.circle)
                btnLeftChui3?.setImageResource(R.drawable.circle)
                btnLeftHC?.setImageResource(R.drawable.circle)
                btnLeftH?.setImageResource(R.drawable.circle_empty)
            }

            5 -> {
                btnLeftChui1?.setImageResource(R.drawable.circle)
                btnLeftChui2?.setImageResource(R.drawable.circle)
                btnLeftChui3?.setImageResource(R.drawable.circle)
                btnLeftHC?.setImageResource(R.drawable.circle)
                btnLeftH?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printRightChui(rc1: Int) {
        when (rc1) {
            0 -> {
                btnRightChui1?.setImageResource(R.drawable.circle_empty)
                btnRightChui2?.setImageResource(R.drawable.circle_empty)
                btnRightChui3?.setImageResource(R.drawable.circle_empty)
                btnRightHC?.setImageResource(R.drawable.circle_empty)
                btnRightH?.setImageResource(R.drawable.circle_empty)
            }

            1 -> {
                btnRightChui1?.setImageResource(R.drawable.circle)
                btnRightChui2?.setImageResource(R.drawable.circle_empty)
                btnRightChui3?.setImageResource(R.drawable.circle_empty)
                btnRightHC?.setImageResource(R.drawable.circle_empty)
                btnRightH?.setImageResource(R.drawable.circle_empty)
            }

            2 -> {
                btnRightChui1?.setImageResource(R.drawable.circle)
                btnRightChui2?.setImageResource(R.drawable.circle)
                btnRightChui3?.setImageResource(R.drawable.circle_empty)
                btnRightHC?.setImageResource(R.drawable.circle_empty)
                btnRightH?.setImageResource(R.drawable.circle_empty)
            }

            3 -> {
                btnRightChui1?.setImageResource(R.drawable.circle)
                btnRightChui2?.setImageResource(R.drawable.circle)
                btnRightChui3?.setImageResource(R.drawable.circle)
                btnRightHC?.setImageResource(R.drawable.circle_empty)
                btnRightH?.setImageResource(R.drawable.circle_empty)
            }

            4 -> {
                btnRightChui1?.setImageResource(R.drawable.circle)
                btnRightChui2?.setImageResource(R.drawable.circle)
                btnRightChui3?.setImageResource(R.drawable.circle)
                btnRightHC?.setImageResource(R.drawable.circle)
                btnRightH?.setImageResource(R.drawable.circle_empty)
            }

            5 -> {
                btnRightChui1?.setImageResource(R.drawable.circle)
                btnRightChui2?.setImageResource(R.drawable.circle)
                btnRightChui3?.setImageResource(R.drawable.circle)
                btnRightHC?.setImageResource(R.drawable.circle)
                btnRightH?.setImageResource(R.drawable.circle)
            }
        }
    }

    private fun printBackgroundColors(lc: String, rc: String) {
        if (lc.contentEquals("B")) {
            leftSideBackground?.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.kumite_blue
                )
            )
            btnLeftPointsMain?.efabColor = ContextCompat.getColor(this, R.color.kumite_blue_dark)
            btnLeftIppon?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnLeftWaza?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnLeftYuko?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnLeftEdit?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
        } else if (lc.contentEquals("R")) {
            leftSideBackground?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.kumite_red)
            )
            btnLeftPointsMain?.efabColor = ContextCompat.getColor(this, R.color.kumite_red_dark)
            btnLeftIppon?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnLeftWaza?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnLeftYuko?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnLeftEdit?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
        }

        if (rc.contentEquals("B")) {
            rightSideBackground?.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.kumite_blue
                )
            )
            btnRightPointsMain?.efabColor = ContextCompat.getColor(this, R.color.kumite_blue_dark)
            btnRightIppon?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnRightWaza?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnRightYuko?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
            btnRightEdit?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_blue)
        } else if (rc.contentEquals("R")) {
            rightSideBackground?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.kumite_red)
            )
            btnRightPointsMain?.efabColor = ContextCompat.getColor(this, R.color.kumite_red_dark)
            btnRightIppon?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnRightWaza?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnRightYuko?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
            btnRightEdit?.fabOptionColor = ContextCompat.getColor(this, R.color.kumite_red)
        }
    }

    private fun printTimer(min: Int, sec: Int) {
        val minutes = min.toString()
        var seconds = sec.toString()
        if (sec < 10) seconds = "0".plus(seconds)
        txtTimer?.text = getString(R.string.time_left, minutes, seconds)
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
        leftIppons = 0
        leftWazas = 0
        leftYukos = 0
        rightIppons = 0
        rightWazas = 0
        rightYukos = 0
        printLeftScore(leftScore)
        printRightScore(rightScore)
        printSenshu(leftSenshu, rightSenshu)
        printLeftIppons(leftIppons)
        printLeftWazas(leftWazas)
        printLeftYukos(leftYukos)
        printRightIppons(rightIppons)
        printRightWazas(rightWazas)
        printRightYukos(rightYukos)
    }

    private fun resetFaults() {
        leftChui = 0
        rightChui = 0
        printLeftChui(leftChui)
        printRightChui(rightChui)
    }

    private fun resetTime() {
        timerStop()
        readDefaultTime()
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
                    if (timerMin == 0 && timerSec == atoShibaraku && sound) playSoundShort()
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

    private fun readDefaultTime() {
        timerMin = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defMin", 2)
        timerSec = getSharedPreferences("KumiteScore", MODE_PRIVATE).getInt("defSec", 0)
        printTimer(timerMin, timerSec)
    }

    private fun playSound() {
        timerPlayer?.start()
    }

    private fun playSoundShort() {
        timerPlayerShort?.start()
    }
}
