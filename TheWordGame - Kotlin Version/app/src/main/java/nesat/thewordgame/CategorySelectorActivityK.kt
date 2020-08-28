package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.util.*

class CategorySelectorActivityK : AppCompatActivity(), View.OnClickListener {

    //Intents
    private val toMainMenu = Intent()
    private val tologicWordPlay = Intent()
    private val totwoPicsOneWord = Intent()
    private val toJustOneWord = Intent()

    //UI Components
    private var LogicWord: Button? = null
    private var twoPicsOneWord: Button? = null
    private var justOneWord: Button? = null
    private var BackButton: Button? = null
    private var cselectorheadline: TextView? = null
    private var proLogicWord: TextView? = null
    private var proJustOneWord: TextView? = null
    private var pro2Pics1Word: TextView? = null
    private var adView: AdView? = null

    private var braditc: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var shadowsintolighttwo: Typeface? = null

    //MediaPlayers
    private var mediaPlayer: MediaPlayer? = null

    //Handlers
    private var handler: Handler? = null

    //Animations
    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    //Timer & TimerTask
    private var timerTask: TimerTask? = null
    private val timer = Timer()

    //Integers
    private var gametheme: Int = 0
    private var gameFont: Int = 0
    private var progress_logicword: Int = 0
    private var progress_2pics1word: Int = 0
    private var progress_justoneword: Int = 0
    private var soundsAllowed: Int = 0

    private val adRequest = AdRequest.Builder().build()

    var activity: Class<*>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/CategorySel", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoryselector)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        totwoPicsOneWord.action = Intent.ACTION_DIAL
        totwoPicsOneWord.setClass(applicationContext, TwoPicsOneWordActivityK::class.java)
        tologicWordPlay.action = Intent.ACTION_CALL
        tologicWordPlay.setClass(applicationContext, LogicWordActivityK::class.java)
        toMainMenu.action = Intent.ACTION_CALL
        toMainMenu.setClass(applicationContext, MainMenuActivityK::class.java)
        toJustOneWord.action = Intent.ACTION_CALL
        toJustOneWord.setClass(applicationContext, JustOneWordActivityK::class.java)

        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.click_2)

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        handler = Handler()

        val settings = getSharedPreferences("settings", 0)
        if (settings.getInt("musicAllowed", 0) == 1) {

        }

        findIds()
        getData()
        setProgress()
        setTheme()
        setFont()
        setOnClickListeners()

        adView!!.loadAd(adRequest)
    }

    override fun onPause() {
        Log.d("TheWordGame/CategorySel", "@Override onPause() started")
        super.onPause()
    }

    override fun onResume() {
        Log.d("TheWordGame/CategorySel", "@Override onResume() started")
        super.onResume()
        val settings = getSharedPreferences("settings", 0)
        if (settings.getInt("musicAllowed", 0) == 1) {

        }
    }

    override fun onStop() {
        Log.d("TheWordGame/CategorySel", "onStop() started")
        super.onStop()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/CategorySel", "onClick(View view) started")
        when (view.id) {

            R.id.LogicWordButton -> {
                val a = getSharedPreferences("settings", 0)
                if (a.getInt("musicAllowed", 0) == 1) {
                    MainMenuActivityK().player.stop()
                }
                val lw = Intent(this, LogicWordActivityK::class.java)
                startActivity(lw)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.twoPicsOneWord -> {
                val d = getSharedPreferences("settings", 0)
                if (d.getInt("musicAllowed", 0) == 1) {
                    MainMenuActivityK().player.stop()
                }
                startActivity(totwoPicsOneWord)
            }

            R.id.justOneWordButton -> {
                val c = getSharedPreferences("settings", 0)
                if (c.getInt("musicAllowed", 0) == 1) {
                    MainMenuActivityK().player.stop()
                }
                val jow = Intent(this, JustOneWordActivityK::class.java)
                startActivity(jow)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.BackButton -> {
                val b = Intent(this, MainMenuActivityK::class.java)
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

    private fun findIds() {
        Log.d("TheWordGame/CategorySel", "findIds() started")
        cselectorheadline = findViewById(R.id.category_headline)
        LogicWord = findViewById(R.id.LogicWordButton)
        twoPicsOneWord = findViewById(R.id.twoPicsOneWord)
        justOneWord = findViewById(R.id.justOneWordButton)
        BackButton = findViewById(R.id.BackButton)
        proLogicWord = findViewById(R.id.logicword_progress)
        pro2Pics1Word = findViewById(R.id.twopicsoneword_progress)
        proJustOneWord = findViewById(R.id.justoneword_progress)
        adView = findViewById(R.id.categoryAdBanner)
    }

    private fun setProgress() {
        Log.d("TheWordGame/CategorySel", "setProgress() started")
        val savegame = getSharedPreferences("savegame", 0)
        progress_logicword = savegame.getInt("level_logicword", 0)
        progress_2pics1word = savegame.getInt("level_2pics1word", 0)
        progress_justoneword = savegame.getInt("level_justoneword", 0)

        proLogicWord!!.text = applicationContext.getString(R.string.cselector_progress_1) +
                " " +
                progress_logicword + " " +
                applicationContext.getString(R.string.cselector_progress_2) +
                " 50" +
                " )"

        proJustOneWord!!.text = applicationContext.getString(R.string.cselector_progress_1) +
                " " +
                progress_justoneword + " " +
                applicationContext.getString(R.string.cselector_progress_2) +
                " 30" +
                " )"

        pro2Pics1Word!!.text = applicationContext.getString(R.string.cselector_progress_1) +
                " " +
                progress_2pics1word + " " +
                applicationContext.getString(R.string.cselector_progress_2) +
                " 10" +
                " )"

    }

    private fun blink() {
        Log.d("TheWordGame/CategorySel", "blink() started")
        timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    timerTask = object : TimerTask() {
                        override fun run() {
                            runOnUiThread { }
                        }
                    }
                    timer.schedule(timerTask!!, 500)
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask!!, 0.toLong(), 1000.toLong())
    }

    private fun setTheme() {
        Log.d("TheWordGame/CategorySel", "setTheme() started")
        when(gametheme) {
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

    private fun getData() {
        Log.d("TheWordGame/CategorySel", "getData() started")
        val settings = getSharedPreferences("settings", 0)
        gametheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun setTheme1() {
        Log.d("TheWordGame/CategorySel", "setTheme1() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/CategorySel", "setTheme2() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/CategorySel", "setTheme3() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/CategorySel", "setTheme4() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/CategorySel", "setTheme6() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/CategorySel", "setTheme7() started")
        Log.d("TheWordGame/CategorySel", "setTheme5() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/CategorySel", "setTheme8() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        Log.d("TheWordGame/CategorySel", "setTheme5() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        Log.d("TheWordGame/CategorySel", "setTheme5() started")
        BackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        BackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        LogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        LogicWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        justOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        justOneWord!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        cselectorheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        proLogicWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        proJustOneWord!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        pro2Pics1Word!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
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
        cselectorheadline!!.typeface = shadowsintolighttwo
        LogicWord!!.typeface = shadowsintolighttwo
        twoPicsOneWord!!.typeface = shadowsintolighttwo
        justOneWord!!.typeface = shadowsintolighttwo
        proLogicWord!!.typeface = shadowsintolighttwo
        pro2Pics1Word!!.typeface = shadowsintolighttwo
        proJustOneWord!!.typeface = shadowsintolighttwo
        BackButton!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        cselectorheadline!!.typeface = delius
        LogicWord!!.typeface = delius
        twoPicsOneWord!!.typeface = delius
        justOneWord!!.typeface = delius
        proLogicWord!!.typeface = delius
        pro2Pics1Word!!.typeface = delius
        proJustOneWord!!.typeface = delius
        BackButton!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        cselectorheadline!!.typeface = arial
        LogicWord!!.typeface = arial
        twoPicsOneWord!!.typeface = arial
        justOneWord!!.typeface = arial
        proLogicWord!!.typeface = arial
        pro2Pics1Word!!.typeface = arial
        proJustOneWord!!.typeface = arial
        BackButton!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        cselectorheadline!!.typeface = braditc
        LogicWord!!.typeface = braditc
        twoPicsOneWord!!.typeface = braditc
        justOneWord!!.typeface = braditc
        proLogicWord!!.typeface = braditc
        pro2Pics1Word!!.typeface = braditc
        proJustOneWord!!.typeface = braditc
        BackButton!!.typeface = braditc
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/CategorySel", "setOnClickListeners() started")
        LogicWord!!.setOnClickListener(this)
        twoPicsOneWord!!.setOnClickListener(this)
        justOneWord!!.setOnClickListener(this)
        BackButton!!.setOnClickListener(this)
    }
}