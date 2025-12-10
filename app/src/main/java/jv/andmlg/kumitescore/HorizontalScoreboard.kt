package jv.andmlg.kumitescore

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import me.angrybyte.numberpicker.view.ActualNumberPicker
import java.util.Locale

class HorizontalScoreboard : AppCompatActivity() {

    //region UI Elements
    private var leftSideBackground: LinearLayout? = null
    private var rightSideBackground: LinearLayout? = null

    private var leftFlagsLayout: LinearLayout? = null
    private var rightFlagsLayout: LinearLayout? = null
    private var leftTXTIppon: TextView? = null
    private var leftTXTWaza: TextView? = null
    private var leftTXTYuko: TextView? = null
    private var rightTXTIppon: TextView? = null
    private var rightTXTWaza: TextView? = null
    private var rightTXTYuko: TextView? = null

    private var txtTimer: AppCompatTextView? = null

    private var btnLeftPlus: ImageButton? = null
    private var btnLeftMinus: ImageButton? = null
    private var btnRightPlus: ImageButton? = null
    private var btnRightMinus: ImageButton? = null

    private var txtLeftScore: AppCompatTextView? = null
    private var txtRightScore: AppCompatTextView? = null

    private var btnLeftSenshu: ImageView? = null
    private var btnRightSenshu: ImageView? = null

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

    private var manualSystem = true
    private var sound = true
    private var leftSideColor = "B"
    private var rightSideColor = "R"

    private var leftScore = 0
    private var rightScore = 0
    private var leftChui = 0
    private var rightChui = 0
    private var leftSenshu = false
    private var rightSenshu = false

    private var lIppon = 0
    private var lWaza = 0
    private var lYuko = 0
    private var rIppon = 0
    private var rWaza = 0
    private var rYuko = 0

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

        setSenshuButtons()
        setChuiButtons()
        setMenuButtons()

        readDefaultValues()
        resetAll()
    }

    private fun findViews() {
        leftSideBackground = findViewById(R.id.leftSideScreen)
        rightSideBackground = findViewById(R.id.rightSideScreen)
        leftFlagsLayout = findViewById(R.id.leftFlagsLayout)
        rightFlagsLayout = findViewById(R.id.rightFlagsLayout)

        leftTXTIppon = findViewById(R.id.leftTextIppon)
        leftTXTWaza = findViewById(R.id.leftTextWaza)
        leftTXTYuko = findViewById(R.id.leftTextYuko)
        rightTXTIppon = findViewById(R.id.rightTextIppon)
        rightTXTWaza = findViewById(R.id.rightTextWaza)
        rightTXTYuko = findViewById(R.id.rightTextYuko)

        txtTimer = findViewById(R.id.mainMatchTimer)
        txtLeftScore = findViewById(R.id.leftScore)
        txtRightScore = findViewById(R.id.rightScore)

        btnLeftPlus = findViewById(R.id.leftPlusButton)
        btnLeftMinus = findViewById(R.id.leftMinusButton)
        btnRightPlus = findViewById(R.id.rightPlusButton)
        btnRightMinus = findViewById(R.id.rightMinusButton)

        btnLeftSenshu = findViewById(R.id.leftSenshuButton)
        btnRightSenshu = findViewById(R.id.rightSenshuButton)

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

    private fun setSenshuButtons() {
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
    }

    private fun setChuiButtons() {
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
    }

    private fun setMenuButtons() {
        btnSettings?.setOnClickListener {
            openSettingsMenu()
        }
        btnReset?.setOnClickListener {
            val popup = LayoutInflater.from(this).inflate(R.layout.reset_confirm, null)
            popup.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val confirm = popup.findViewById<Button>(R.id.popConfirm)
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
            val tmpIppon = lIppon; lIppon = rIppon; rIppon = tmpIppon
            val tmpWaza = lWaza; lWaza = rWaza; rWaza = tmpWaza
            val tmpYuko = lYuko; lYuko = rYuko; rYuko = tmpYuko

            val tmpColor = leftSideColor; leftSideColor = rightSideColor; rightSideColor = tmpColor
            printBackgroundColors(leftSideColor, rightSideColor)
            val tmpSenshu = leftSenshu; leftSenshu = rightSenshu; rightSenshu = tmpSenshu
            printSenshu(leftSenshu, rightSenshu)
            val tmpScore = leftScore; leftScore = rightScore; rightScore = tmpScore
            printLeftScore(leftScore); printRightScore(rightScore)
            val tmpChui = leftChui; leftChui = rightChui; rightChui = tmpChui
            printLeftChui(leftChui); printRightChui(rightChui)
        }
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

        manualSystem = getSharedPreferences("KumiteScore", MODE_PRIVATE).getBoolean("manual", true)
        if (manualSystem) setManualSystem()
        else setFlagSystem()
    }

    private fun openSettingsMenu() {
        val dialogView = layoutInflater.inflate(R.layout.options_dialog, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val optionTime: Button? = dialogView.findViewById(R.id.option_Clock)
        val optionAto: MaterialButtonToggleGroup? = dialogView.findViewById(R.id.AtoToggle)
        val optionSystem: MaterialButtonToggleGroup? = dialogView.findViewById(R.id.SystemToggle)
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

        if (manualSystem) optionSystem?.check(R.id.option_SysPlus)
        else optionSystem?.check(R.id.option_SysFlag)

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
        optionSystem?.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.option_SysPlus -> manualSystem = true
                    R.id.option_SysFlag -> manualSystem = false
                }
                getSharedPreferences("KumiteScore", MODE_PRIVATE).edit()
                    .putBoolean("manual", manualSystem).apply()

                if (manualSystem) setManualSystem()
                else setFlagSystem()
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

    private fun openFlagMenu(left: Boolean) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.flags_dialog, null)

        val btnIppon = dialogView.findViewById<ImageButton>(R.id.dialogIppon)
        val btnWaza = dialogView.findViewById<ImageButton>(R.id.dialogWaza)
        val btnYuko = dialogView.findViewById<ImageButton>(R.id.dialogYuko)

        if (left) {
            val color = leftSideBackground?.background as ColorDrawable
            btnIppon.backgroundTintList = ColorStateList.valueOf(color.color)
            btnWaza.backgroundTintList = ColorStateList.valueOf(color.color)
            btnYuko.backgroundTintList = ColorStateList.valueOf(color.color)
        } else {
            val color = rightSideBackground?.background as ColorDrawable
            btnIppon.backgroundTintList = ColorStateList.valueOf(color.color)
            btnWaza.backgroundTintList = ColorStateList.valueOf(color.color)
            btnYuko.backgroundTintList = ColorStateList.valueOf(color.color)
        }

        val newDialog = DialogPlus.newDialog(this)
            .setContentHolder(ViewHolder(dialogView))
            .setGravity(Gravity.CENTER)
            .setContentWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
            .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        newDialog.setOnClickListener { dialog: DialogPlus, view: View ->
            when (view) {
                btnIppon -> {
                    if (left) {
                        lIppon += 1
                        leftScore = (lIppon * 3) + (lWaza * 2) + lYuko
                        printLeftScore(leftScore)
                    } else {
                        rIppon += 1
                        rightScore = (rIppon * 3) + (rWaza * 2) + rYuko
                        printRightScore(rightScore)
                    }
                }

                btnWaza -> {
                    if (left) {
                        lWaza += 1
                        leftScore = (lIppon * 3) + (lWaza * 2) + lYuko
                        printLeftScore(leftScore)
                    } else {
                        rWaza += 1
                        rightScore = (rIppon * 3) + (rWaza * 2) + rYuko
                        printRightScore(rightScore)
                    }
                }

                btnYuko -> {
                    if (left) {
                        lYuko += 1
                        leftScore = (lIppon * 3) + (lWaza * 2) + lYuko
                        printLeftScore(leftScore)
                    } else {
                        rYuko += 1
                        rightScore = (rIppon * 3) + (rWaza * 2) + rYuko
                        printRightScore(rightScore)
                    }
                }
            }
            dialog.dismiss()
        }
        newDialog.create().show()
    }

    private fun openEditScore(left: Boolean) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.points_picker_dialog, null)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)

        val pickIppon = dialogView.findViewById<ActualNumberPicker>(R.id.pickIppon)
        val pickWaza = dialogView.findViewById<ActualNumberPicker>(R.id.pickWaza)
        val pickYuko = dialogView.findViewById<ActualNumberPicker>(R.id.pickYuko)

        if (left) {
            pickIppon.value = lIppon; pickWaza.value = lWaza; pickYuko.value = lYuko
        } else {
            pickIppon.value = rIppon; pickWaza.value = rWaza; pickYuko.value = rYuko
        }

        dialogBuilder.setNegativeButton(resources.getString(R.string.picker_cancel)) { _, _ -> }
        dialogBuilder.setPositiveButton(resources.getString(R.string.picker_save)) { _, _ ->
            if (left) {
                lIppon = pickIppon.value; lWaza = pickWaza.value; lYuko = pickYuko.value
                leftScore = (lIppon * 3) + (lWaza * 2) + (lYuko)
                printLeftScore(leftScore)
            } else {
                rIppon = pickIppon.value; rWaza = pickWaza.value; rYuko = pickYuko.value
                rightScore = (rIppon * 3) + (rWaza * 2) + (rYuko)
                printRightScore(rightScore)
            }
        }
        dialogBuilder.create().show()
    }

    private fun printLeftScore(ls: Int) {
        var score: Int = ls
        if (ls > 99) score = 99
        txtLeftScore?.text = String.format(Locale.US, "%d", score)

        if (!manualSystem) {
            leftTXTIppon?.text = "$lIppon"
            leftTXTWaza?.text = "$lWaza"
            leftTXTYuko?.text = "$lYuko"
        }
    }

    private fun printRightScore(rs: Int) {
        var score: Int = rs
        if (rs > 99) score = 99
        txtRightScore?.text = String.format(Locale.US, "%d", score)

        if (!manualSystem) {
            rightTXTIppon?.text = "$rIppon"
            rightTXTWaza?.text = "$rWaza"
            rightTXTYuko?.text = "$rYuko"
        }
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
                ContextCompat.getColor(this, R.color.kumite_blue)
            )
        } else if (lc.contentEquals("R")) {
            leftSideBackground?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.kumite_red)
            )
        }

        if (rc.contentEquals("B")) {
            rightSideBackground?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.kumite_blue)
            )
        } else if (rc.contentEquals("R")) {
            rightSideBackground?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.kumite_red)
            )
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
        lIppon = 0
        lWaza = 0
        lYuko = 0
        rIppon = 0
        rWaza = 0
        rYuko = 0
        leftScore = 0
        rightScore = 0
        leftSenshu = false
        rightSenshu = false
        printLeftScore(leftScore)
        printRightScore(rightScore)
        printSenshu(leftSenshu, rightSenshu)
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

    private fun setManualSystem() {
        resetAll()
        leftFlagsLayout?.visibility = View.INVISIBLE
        rightFlagsLayout?.visibility = View.INVISIBLE

        btnLeftPlus?.setImageResource(R.drawable.arrow_up)
        btnRightPlus?.setImageResource(R.drawable.arrow_up)
        btnLeftMinus?.setImageResource(R.drawable.arrow_down)
        btnRightMinus?.setImageResource(R.drawable.arrow_down)

        btnLeftPlus?.setOnClickListener {
            if (leftScore < 99) leftScore += 1
            printLeftScore(leftScore)
        }
        btnLeftMinus?.setOnClickListener {
            if (leftScore > 0) leftScore -= 1
            printLeftScore(leftScore)
        }
        btnRightPlus?.setOnClickListener {
            if (rightScore < 99) rightScore += 1
            printRightScore(rightScore)
        }
        btnRightMinus?.setOnClickListener {
            if (rightScore > 0) rightScore -= 1
            printRightScore(rightScore)
        }
    }

    private fun setFlagSystem() {
        resetAll()
        leftFlagsLayout?.visibility = View.VISIBLE
        rightFlagsLayout?.visibility = View.VISIBLE

        btnLeftPlus?.setImageResource(R.drawable.plus)
        btnRightPlus?.setImageResource(R.drawable.plus)
        btnLeftMinus?.setImageResource(R.drawable.pencil_left)
        btnRightMinus?.setImageResource(R.drawable.pencil_left)

        btnLeftPlus?.setOnClickListener {
            openFlagMenu(true)
        }
        btnLeftMinus?.setOnClickListener {
            openEditScore(true)
        }
        btnRightPlus?.setOnClickListener {
            openFlagMenu(false)
        }
        btnRightMinus?.setOnClickListener {
            openEditScore(false)
        }
    }
}
