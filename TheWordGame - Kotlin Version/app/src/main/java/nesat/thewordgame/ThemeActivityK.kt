package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button

class ThemeActivityK : AppCompatActivity(), View.OnClickListener {

    //Views
    private var backButton: Button? = null
    private var blueblackButton: Button? = null
    private var redblackButton: Button? = null
    private var greenblackButton: Button? = null
    private var yellowblackButton: Button? = null
    private var pinkblackButton: Button? = null
    private var whiteblackButton: Button? = null
    private var blackblueButton: Button? = null
    private var blackredButton: Button? = null
    private var blackgreenButton: Button? = null
    private var blackyellowButton: Button? = null
    private var blackpinkButton: Button? = null
    private var blackwhiteButton: Button? = null

    private var deliusButton: Button? = null
    private var braditcButton: Button? = null
    private var arialButton: Button? = null
    private var shadowsintolightButton: Button? = null

    private var braditc: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var shadowsintolighttwo: Typeface? = null

    //MediaPlayers
    private var mediaPlayer: MediaPlayer? = null

    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private var setTheme: Int = 0
    private var setFont: Int = 0
    private var soundsAllowed: Int = 0

    private var fadeOut: Animation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/ThemeActivi", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.click_2)

        findIds()
        getData()
        setTheme()
        setFont()

        shadowsintolightButton!!.typeface = shadowsintolighttwo
        arialButton!!.typeface = arial
        braditcButton!!.typeface = braditc
        deliusButton!!.typeface = delius
        setOnClickListeners()
        val settings = getSharedPreferences("settings", 0)
        val i = settings.getInt("gameFont", 0)
        Log.d("TheWordGame/ThemeActivi", "savedValue = $i")
    }

    override fun onPause() {
        Log.d("TheWordGame/ThemeActivi", "onPause() started")
        super.onPause()
    }

    override fun onStop() {
        Log.d("TheWordGame/ThemeActivi", "onStop() started")
        super.onStop()
    }

    override fun onResume() {
        Log.d("TheWordGame/ThemeActivi", "onResume() started")
        super.onResume()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/ThemeActivi", "onClick(View view) started")
        when (view.id) {

            R.id.blueblackButton -> {
                setTheme = 1
                themeSetter()
                setTheme1()
                playSound()
            }

            R.id.redblackButton -> {
                setTheme = 2
                themeSetter()
                setTheme2()
                playSound()
            }

            R.id.greenblackButton -> {
                setTheme = 3
                themeSetter()
                setTheme3()
                playSound()
            }

            R.id.yellowblackButton -> {
                setTheme = 4
                themeSetter()
                setTheme4()
                playSound()
            }

            R.id.pinkblackButton -> {
                setTheme = 5
                themeSetter()
                setTheme5()
                playSound()
            }

            R.id.whiteblackButton -> {
                setTheme = 6
                themeSetter()
                setTheme6()
                playSound()
            }

            R.id.blackblueButton -> {
                setTheme = 7
                themeSetter()
                setTheme7()
                playSound()
            }

            R.id.blackredButton -> {
                setTheme = 8
                themeSetter()
                setTheme8()
                playSound()
            }

            R.id.blackgreenButton -> {
                setTheme = 9
                themeSetter()
                setTheme9()
                playSound()
            }

            R.id.blackyellowButton -> {
                setTheme = 10
                themeSetter()
                setTheme10()
                playSound()
            }

            R.id.blackpinkButton -> {
                setTheme = 11
                themeSetter()
                setTheme11()
                playSound()
            }

            R.id.blackwhiteButton -> {
                setTheme = 12
                themeSetter()
                setTheme12()
                playSound()
            }

            R.id.shadowsintolightButton -> {
                setFont = 1
                fontSetter()
                setFont1()
                playSound()
            }

            R.id.deliusButton -> {
                setFont = 2
                fontSetter()
                setFont2()
                playSound()
            }

            R.id.arialButton -> {
                setFont = 3
                fontSetter()
                setFont3()
                playSound()
            }

            R.id.braditcButton -> {
                setFont = 4
                fontSetter()
                setFont4()
                playSound()
            }

            R.id.BackButton -> {
                playSound()
                val b = Intent(this, SettingsActivityK::class.java)
                startActivity(b)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    private fun getData() {
        Log.d("TheWordGame/ThemeActivi", "getData() started")
        val settings = getSharedPreferences("settings", 0)
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun setTheme() {
        Log.d("TheWordGame/ThemeActivi", "setTheme() started")
        when(gameTheme) {
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
        Log.d("TheWordGame/ThemeActivi", "setTheme1() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/ThemeActivi", "setTheme2() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/ThemeActivi", "setTheme3() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/ThemeActivi", "setTheme4() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/ThemeActivi", "setTheme5() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/ThemeActivi", "setTheme6() started")
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/ThemeActivi", "setTheme7() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/ThemeActivi", "setTheme8() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        Log.d("TheWordGame/ThemeActivi", "setTheme9() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        Log.d("TheWordGame/ThemeActivi", "setTheme10() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        Log.d("TheWordGame/ThemeActivi", "setTheme11() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        Log.d("TheWordGame/ThemeActivi", "setTheme12() started")
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun themeSetter() {
        Log.d("TheWordGame/ThemeActivi", "themeSetter() started")
        val settings = getSharedPreferences("settings", 0)
        val editor = settings.edit()
        editor.putInt("gameTheme", setTheme)
        editor.apply()
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
        blueblackButton!!.typeface = shadowsintolighttwo
        redblackButton!!.typeface = shadowsintolighttwo
        greenblackButton!!.typeface = shadowsintolighttwo
        yellowblackButton!!.typeface = shadowsintolighttwo
        pinkblackButton!!.typeface = shadowsintolighttwo
        whiteblackButton!!.typeface = shadowsintolighttwo

        blackblueButton!!.typeface = shadowsintolighttwo
        blackredButton!!.typeface = shadowsintolighttwo
        blackgreenButton!!.typeface = shadowsintolighttwo
        blackyellowButton!!.typeface = shadowsintolighttwo
        blackpinkButton!!.typeface = shadowsintolighttwo
        blackwhiteButton!!.typeface = shadowsintolighttwo

        backButton!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        blueblackButton!!.typeface = delius
        redblackButton!!.typeface = delius
        greenblackButton!!.typeface = delius
        yellowblackButton!!.typeface = delius
        pinkblackButton!!.typeface = delius
        whiteblackButton!!.typeface = delius

        blackblueButton!!.typeface = delius
        blackredButton!!.typeface = delius
        blackgreenButton!!.typeface = delius
        blackyellowButton!!.typeface = delius
        blackpinkButton!!.typeface = delius
        blackwhiteButton!!.typeface = delius

        backButton!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        blueblackButton!!.typeface = arial
        redblackButton!!.typeface = arial
        greenblackButton!!.typeface = arial
        yellowblackButton!!.typeface = arial
        pinkblackButton!!.typeface = arial
        whiteblackButton!!.typeface = arial

        blackblueButton!!.typeface = arial
        blackredButton!!.typeface = arial
        blackgreenButton!!.typeface = arial
        blackyellowButton!!.typeface = arial
        blackpinkButton!!.typeface = arial
        blackwhiteButton!!.typeface = arial

        backButton!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        blueblackButton!!.typeface = braditc
        redblackButton!!.typeface = braditc
        greenblackButton!!.typeface = braditc
        yellowblackButton!!.typeface = braditc
        pinkblackButton!!.typeface = braditc
        whiteblackButton!!.typeface = braditc

        blackblueButton!!.typeface = braditc
        blackredButton!!.typeface = braditc
        blackgreenButton!!.typeface = braditc
        blackyellowButton!!.typeface = braditc
        blackpinkButton!!.typeface = braditc
        blackwhiteButton!!.typeface = braditc

        backButton!!.typeface = braditc
    }

    private fun fontSetter() {
        Log.d("TheWordGame/ThemeActivi", "fontSetter() started")
        val settings = getSharedPreferences("settings", 0)
        val editor = settings.edit()
        editor.putInt("gameFont", setFont)
        editor.apply()
        Log.d("TheWordGame/ThemeActivi", setFont.toString())
    }

    private fun findIds() {
        Log.d("TheWordGame/ThemeActivi", "findIds() started")
        backButton = findViewById(R.id.BackButton)
        blueblackButton = findViewById(R.id.blueblackButton)
        redblackButton = findViewById(R.id.redblackButton)
        greenblackButton = findViewById(R.id.greenblackButton)
        yellowblackButton = findViewById(R.id.yellowblackButton)
        pinkblackButton = findViewById(R.id.pinkblackButton)
        whiteblackButton = findViewById(R.id.whiteblackButton)
        blackblueButton = findViewById(R.id.blackblueButton)
        blackredButton = findViewById(R.id.blackredButton)
        blackgreenButton = findViewById(R.id.blackgreenButton)
        blackyellowButton = findViewById(R.id.blackyellowButton)
        blackpinkButton = findViewById(R.id.blackpinkButton)
        blackwhiteButton = findViewById(R.id.blackwhiteButton)
        shadowsintolightButton = findViewById(R.id.shadowsintolightButton)
        deliusButton = findViewById(R.id.deliusButton)
        arialButton = findViewById(R.id.arialButton)
        braditcButton = findViewById(R.id.braditcButton)
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/ThemeActivi", "setOnClickListeners() started")
        backButton!!.setOnClickListener(this)
        blueblackButton!!.setOnClickListener(this)
        redblackButton!!.setOnClickListener(this)
        greenblackButton!!.setOnClickListener(this)
        yellowblackButton!!.setOnClickListener(this)
        pinkblackButton!!.setOnClickListener(this)
        whiteblackButton!!.setOnClickListener(this)

        blackblueButton!!.setOnClickListener(this)
        blackredButton!!.setOnClickListener(this)
        blackgreenButton!!.setOnClickListener(this)
        blackyellowButton!!.setOnClickListener(this)
        blackpinkButton!!.setOnClickListener(this)
        blackwhiteButton!!.setOnClickListener(this)

        shadowsintolightButton!!.setOnClickListener(this)
        deliusButton!!.setOnClickListener(this)
        arialButton!!.setOnClickListener(this)
        braditcButton!!.setOnClickListener(this)
    }
}