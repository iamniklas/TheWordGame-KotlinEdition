package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class SettingsActivityK : AppCompatActivity(), View.OnClickListener {

    //Intents
    private val toMainMenu = Intent()
    private val toThemes = Intent()
    private val toHelp = Intent()
    private val toReset = Intent()

    //UI Widgets
    private var headline: TextView? = null
    private var help: Button? = null
    private var optionsBackButton: Button? = null
    private var setThemeButton: Button? = null
    private var resetButton: Button? = null
    private var musicCheckBox: CheckBox? = null
    private var soundsCheckBox: CheckBox? = null
    private var adView: AdView? = null

    //Animations
    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    //MediaPlayer
    private var mediaPlayer: MediaPlayer? = null

    //Handler
    private var handler: Handler? = null

    //AlertDialog.Builder
    private var dialog: AlertDialog.Builder? = null

    //AdRequest
    private val adRequest = AdRequest.Builder().build()

    //Integers
    private var bucksSave: Int = 0
    private var musicAllowed: Int = 0
    private var soundsAllowed: Int = 0
    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private var savelevel_logicword: Int = 0
    private var savelevel_2pics1word: Int = 0
    private var savelevel_justoneword: Int = 0
    private var fS: Int = 0

    //Strings
    private val h1ABFormat: String? = null
    private val h2ABFormat: String? = null
    private val h3ABFormat: String? = null

    private var braditc: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var shadowsintolighttwo: Typeface? = null

    private val a: Int = 0
    private val b: Int = 0
    private val c: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/SettingsAct", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.click_2)
        handler = Handler()

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        toHelp.action = Intent.ACTION_DIAL
        toHelp.setClass(applicationContext, HelpActivityK::class.java)
        toMainMenu.action = Intent.ACTION_CALL
        toMainMenu.setClass(applicationContext, MainMenuActivityK::class.java)
        toThemes.action = Intent.ACTION_CALL
        toThemes.setClass(applicationContext, ThemeActivityK::class.java)
        toReset.action = Intent.ACTION_CALL
        toReset.setClass(applicationContext, ResetActivityK::class.java)
        dialog = AlertDialog.Builder(this)

        findIds()
        getData()
        setTheme()
        setFont()
        setOnClickListeners()

        adView!!.loadAd(adRequest)
    }

    override fun onPause() {
        Log.d("TheWordGame/SettingsAct", "onPause() started")
        super.onPause()
    }

    override fun onStop() {
        Log.d("TheWordGame/SettingsAct", "onStop() started")
        super.onStop()
    }

    override fun onResume() {
        Log.d("TheWordGame/SettingsAct", "onResume() started")
        super.onResume()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/SettingsAct", "onClick(View view) started")
        when (view.id) {

            R.id.help -> {
                playSound()
                val h = Intent(this, HelpActivityK::class.java)
                startActivity(h)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.optionsBackButton -> {
                playSound()
                val b = Intent(this, MainMenuActivityK::class.java)
                startActivity(b)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.setThemeButton -> {
                playSound()
                val t = Intent(this, ThemeActivityK::class.java)
                startActivity(t)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.resetButton -> {
                playSound()
                startActivity(toReset)
            }

            R.id.musicCheckBox -> {
                setMusic()
                getData()
            }

            R.id.soundsCheckBox -> {
                setSound()
                getData()
            }
        }
    }

    private fun findIds() {
        Log.d("TheWordGame/SettingsAct", "findIds() started")
        headline = findViewById(R.id.settingsheadline)
        help = findViewById(R.id.help)
        optionsBackButton = findViewById(R.id.optionsBackButton)
        setThemeButton = findViewById(R.id.setThemeButton)
        resetButton = findViewById(R.id.resetButton)
        musicCheckBox = findViewById(R.id.musicCheckBox)
        soundsCheckBox = findViewById(R.id.soundsCheckBox)
        adView = findViewById(R.id.settingsBanner)
    }

    private fun getData() {
        Log.d("TheWordGame/SettingsAct", "getData() started")
        val savegame = getSharedPreferences("savegame", 0)
        bucksSave = savegame.getInt("bucks", 0)
        val settings = getSharedPreferences("settings", 0)
        bucksSave = savegame.getInt("bucks", 0)
        savelevel_logicword = savegame.getInt("level_logicword", 0)
        savelevel_2pics1word = savegame.getInt("level_2pics1word", 0)
        savelevel_justoneword = savegame.getInt("level_justoneword", 0)
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)

        musicAllowed = settings.getInt("musicAllowed", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
        val firstStart = getSharedPreferences("firstStarted", 0)
        fS = firstStart.getInt("firstStarted", 0)

        when(musicAllowed) {
            0 -> musicCheckBox!!.isChecked = false
            1 -> musicCheckBox!!.isChecked = true
            else -> Toast.makeText(applicationContext, "Error with MusicSwitch: Wrong value", Toast.LENGTH_SHORT).show()
        }

        when(soundsAllowed) {
            0 -> soundsCheckBox!!.isChecked = false
            1 -> soundsCheckBox!!.isChecked = true
            else -> Toast.makeText(applicationContext, "Error caused by Sounds CheckBox: Wrong Value", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    private fun setMusic() {
        Log.d("TheWordGame/SettingsAct", "setMusic() started")
        if (musicCheckBox!!.isChecked) {
            musicAllowed = 1
            writeData()
        } else if (!musicCheckBox!!.isChecked) {
            musicAllowed = 0
            writeData()
        }
    }

    private fun setSound() {
        Log.d("TheWordGame/SettingsAct", "setSound() started")
        if (soundsCheckBox!!.isChecked) {
            soundsAllowed = 1
            writeData()
        } else if (!soundsCheckBox!!.isChecked) {
            soundsAllowed = 0
            writeData()
        }
    }

    private fun writeData() {
        Log.d("TheWordGame/SettingsAct", "writeData() started")
        val settings = getSharedPreferences("settings", 0)
        val editor = settings.edit()
        editor.putInt("musicAllowed", musicAllowed)
        editor.putInt("soundsAllowed", soundsAllowed)
        editor.apply()
    }

    private fun setTheme() {
        Log.d("TheWordGame/SettingsAct", "setTheme() started")
        when (gameTheme) {
            1 -> setTheme1()
            2 -> setTheme2()
            3 -> setTheme3()
            4 -> setTheme4()
            5 -> setTheme5()
            6 -> setTheme6()
            7 -> setTheme7()
            8 -> setTheme8()
            9 -> setTheme9()
            10 -> setTheme10()
            11 -> setTheme11()
            12 -> setTheme12()
        }
    }

    private fun setTheme1() {
        Log.d("TheWordGame/SettingsAct", "setTheme1() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/SettingsAct", "setTheme2() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/SettingsAct", "setTheme3() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/SettingsAct", "setTheme4() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/SettingsAct", "setTheme6() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/SettingsAct", "setTheme7() started")
        Log.d("TheWordGame/SettingsAct", "setTheme5() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/SettingsAct", "setTheme8() started")
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        headline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        setThemeButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        setThemeButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        resetButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        resetButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        help!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        help!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        optionsBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        optionsBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        soundsCheckBox!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setFont() {
        Log.d("TheWordGame/ThemeActivi", "setFont() started")
        when(gameFont) {
            1 -> setFont1()
            2 -> setFont2()
            3 -> setFont3()
            4 -> setFont4()
        }
    }

    private fun setFont1() {
        Log.d("TheWordGame/ThemeActivi", "setFont1() started")
        headline!!.typeface = shadowsintolighttwo
        musicCheckBox!!.typeface = shadowsintolighttwo
        soundsCheckBox!!.typeface = shadowsintolighttwo
        setThemeButton!!.typeface = shadowsintolighttwo
        resetButton!!.typeface = shadowsintolighttwo
        optionsBackButton!!.typeface = shadowsintolighttwo
        help!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        headline!!.typeface = delius
        musicCheckBox!!.typeface = delius
        soundsCheckBox!!.typeface = delius
        setThemeButton!!.typeface = delius
        resetButton!!.typeface = delius
        optionsBackButton!!.typeface = delius
        help!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        headline!!.typeface = arial
        musicCheckBox!!.typeface = arial
        soundsCheckBox!!.typeface = arial
        setThemeButton!!.typeface = arial
        resetButton!!.typeface = arial
        optionsBackButton!!.typeface = arial
        help!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        headline!!.typeface = braditc
        musicCheckBox!!.typeface = braditc
        soundsCheckBox!!.typeface = braditc
        setThemeButton!!.typeface = braditc
        resetButton!!.typeface = braditc
        optionsBackButton!!.typeface = braditc
        help!!.typeface = braditc
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/SettingsAct", "setOnClickListeners() started")
        optionsBackButton!!.setOnClickListener(this)
        help!!.setOnClickListener(this)
        setThemeButton!!.setOnClickListener(this)
        resetButton!!.setOnClickListener(this)
        musicCheckBox!!.setOnClickListener(this)
        soundsCheckBox!!.setOnClickListener(this)
    }
}
