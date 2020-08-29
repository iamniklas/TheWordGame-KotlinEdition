package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardedVideoAd

class JustOneWordActivityK : AppCompatActivity(), View.OnClickListener {

    //Widgets
    private var bucksValueView: TextView? = null
    private var bucksText: TextView? = null
    private var levelview: TextView? = null
    private var startword: TextView? = null
    private var answer1: TextView? = null
    private var answer2: TextView? = null
    private var answer3: TextView? = null
    private var answer4: TextView? = null
    private var answer5: TextView? = null
    private var answeredittext: EditText? = null
    private var buttoncheck: Button? = null
    private var buttonback: Button? = null
    private var adView: AdView? = null
    private val interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null

    private var shadowsintolight: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var braditc: Typeface? = null

    //Intents
    private val toCategory = Intent()
    private val toStore = Intent()

    private val adRequest = AdRequest.Builder().build()

    //Strings
    /*var answer1FirstLetter: String? = null
    var answer2FirstLetter: String? = null
    var answer3FirstLetter: String? = null
    var answer4FirstLetter: String? = null
    var answer5FirstLetter: String? = null*/
    private val level = "Level "

    //Integers
    private var bucksSave: Int = 0
    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private var currentlevel: Int = 0
    private var musicAllowed: Int = 0
    private var emptyArrayPos: Int = 0
    private var adCounter: Int = 0
    private var gameModeStartCounter: Int = 0
    private var dontrememberagain: Int = 0
    private var soundsAllowed: Int = 0

    //MediaPlayers
    private var lastcorrect: MediaPlayer? = null
    private var correct: MediaPlayer? = null
    private var gamemodemusic: MediaPlayer? = null
    private var mediaPlayer: MediaPlayer? = null

    //Handlers
    private var handler = Handler()

    //Arrays
    private val arrayLeveltext = arrayOfNulls<String>(51)
    private val arrayAnswer1 = arrayOfNulls<String>(51)
    private val arrayAnswer2 = arrayOfNulls<String>(51)
    private val arrayAnswer3 = arrayOfNulls<String>(51)
    private val arrayAnswer4 = arrayOfNulls<String>(51)
    private val arrayAnswer5 = arrayOfNulls<String>(51)

    //AlertDialog.Builder
    private var builder: AlertDialog.Builder? = null

    internal var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/JustOneWord", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_justoneword)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolight = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        fill_array_levelText()
        fill_array_answer1()
        fill_array_answer2()
        fill_array_answer3()
        fill_array_answer4()
        fill_array_answer5()

        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.click_2)
        lastcorrect = MediaPlayer.create(applicationContext, R.raw.bigwinsound)
        correct = MediaPlayer.create(applicationContext, R.raw.correct)
        gamemodemusic = MediaPlayer.create(applicationContext, R.raw.gamemode)
        gamemodemusic!!.isLooping = true
        val settings = getSharedPreferences("settings", 0)
        musicAllowed = settings.getInt("musicAllowed", 0)
        if (musicAllowed == 1) {
            gamemodemusic!!.start()
        }

        toCategory.action = Intent.ACTION_CALL
        toCategory.setClass(applicationContext, CategorySelectorActivityK::class.java)
        toStore.action = Intent.ACTION_VIEW
        toStore.data = Uri.parse("https://play.google.com/store/apps/details?id=nesat.thewordgame")

        handler = Handler()
        builder = AlertDialog.Builder(this)
        findIds()
        getData()
        setTheme()
        setFont()
        if (currentlevel == 0) {
            currentlevel = 1
            writeData()
        }
        setHeadline()
        setAnswer1_v2()
        setAnswer2_v2()
        setAnswer3_v2()
        setAnswer4_v2()
        setAnswer5_v2()
        answer1!!.visibility = View.INVISIBLE
        answer2!!.visibility = View.INVISIBLE
        answer3!!.visibility = View.INVISIBLE
        answer4!!.visibility = View.INVISIBLE
        answer5!!.visibility = View.INVISIBLE
        setOnClickListeners()
        setLevelAndAnswers()
        setMainText_v2()
        adView!!.loadAd(adRequest)
        increaseStartCounter()
        startCounter()
    }

    override fun onPause() {
        Log.d("TheWordGame/JustOneWord", "onPause() started")
        super.onPause()
        gamemodemusic!!.pause()
    }

    override fun onStop() {
        Log.d("TheWordGame/JustOneWord", "onStop() started")
        super.onStop()
        gamemodemusic!!.pause()
    }

    override fun onResume() {
        Log.d("TheWordGame/JustOneWord", "onResume() started")
        super.onResume()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/JustOneWord", "onClick(View view) started")
        when (view.id) {

            R.id.buttoncheck -> {
                checkAnswer_v2()
                setHeadline()
                adCounter++
                //loadInterstitial();
                Log.d("TheWordGame/LogicWord", "adCounter: $adCounter")
                /*
                if(adCounter >= 10){
                    showInterstitial();
                    adCounter = 0;
                    Log.d("TheWordGame/LogicWord", "adCounter: " + adCounter);
                }
                */
                resetedittext()
            }

            R.id.buttonback -> {
                val settings = getSharedPreferences("settings", 0)
                playSound()
                mediaPlayer!!.pause()
                val i = Intent(this, CategorySelectorActivityK::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    private fun findEmptyPosition() {
        Log.d("TheWordGame/JustOneWord", "findEmptyPosition() started")
        emptyArrayPos = 0
        for (i in 0..arrayLeveltext.size) {
            if (arrayLeveltext[i] != null) {
                Log.d("TheWordGame/JustOneWord", "$i : not null")
            } else {
                Log.d("TheWordGame/JustOneWord", "null")
                emptyArrayPos = i
                break
            }
        }
        Log.d("TheWordGame/JustOneWord", "Empty position: $emptyArrayPos")
    }

    private fun findIds() {
        Log.d("TheWordGame/JustOneWord", "findIds() started")
        levelview = findViewById(R.id.levelview)
        startword = findViewById(R.id.startword)
        answer1 = findViewById(R.id.answer1)
        answer2 = findViewById(R.id.answer2)
        answer3 = findViewById(R.id.answer3)
        answer4 = findViewById(R.id.answer4)
        answer5 = findViewById(R.id.answer5)
        buttoncheck = findViewById(R.id.buttoncheck)
        answeredittext = findViewById(R.id.answeredittext)
        buttonback = findViewById(R.id.buttonback)
        bucksValueView = findViewById(R.id.bucksValueView)
        bucksText = findViewById(R.id.bucksTextView)
        adView = findViewById(R.id.justonewordBanner)
    }

    private fun getData() {
        Log.d("TheWordGame/JustOneWord", "getData() started")
        val savegame = getSharedPreferences("savegame", 0)
        val settings = getSharedPreferences("settings", 0)
        bucksSave = savegame.getInt("bucks", 0)
        gameModeStartCounter = savegame.getInt("startCounter", 0)
        dontrememberagain = savegame.getInt("dontrememberagain", 0)
        bucksValueView!!.text = bucksSave.toString()
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
        currentlevel = savegame.getInt("level_justoneword", 0)
    }

    private fun writeData() {
        Log.d("TheWordGame/JustOneWord", "writeData() started")
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("level_justoneword", currentlevel)
        editor.putInt("bucks", bucksSave)
        editor.apply()
        bucksValueView!!.text = bucksSave.toString()
    }

    private fun setTheme() {
        Log.d("TheWordGame/JustOneWord", "setTheme() started")
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
        Log.d("TheWordGame/JustOneWord", "setTheme1() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/JustOneWord", "setTheme2() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/JustOneWord", "setTheme3() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/JustOneWord", "setTheme4() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/JustOneWord", "setTheme5() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/JustOneWord", "setTheme6() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/JustOneWord", "setTheme7() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/JustOneWord", "setTheme8() started")
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme9() {
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme10() {
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme11() {
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme12() {
        levelview!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        startword!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answeredittext!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answeredittext!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttoncheck!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        buttoncheck!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttonback!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        buttonback!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
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
        levelview!!.typeface = shadowsintolight
        bucksText!!.typeface = shadowsintolight
        bucksValueView!!.typeface = shadowsintolight
        buttoncheck!!.typeface = shadowsintolight
        startword!!.typeface = shadowsintolight
        answeredittext!!.typeface = shadowsintolight
        answer1!!.typeface = shadowsintolight
        answer2!!.typeface = shadowsintolight
        answer3!!.typeface = shadowsintolight
        answer4!!.typeface = shadowsintolight
        answer5!!.typeface = shadowsintolight
        buttonback!!.typeface = shadowsintolight
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        levelview!!.typeface = delius
        bucksText!!.typeface = delius
        bucksValueView!!.typeface = delius
        buttoncheck!!.typeface = delius
        startword!!.typeface = delius
        answeredittext!!.typeface = delius
        answer1!!.typeface = delius
        answer2!!.typeface = delius
        answer3!!.typeface = delius
        answer4!!.typeface = delius
        answer5!!.typeface = delius
        buttonback!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        levelview!!.typeface = arial
        bucksText!!.typeface = arial
        bucksValueView!!.typeface = arial
        buttoncheck!!.typeface = arial
        startword!!.typeface = arial
        answeredittext!!.typeface = arial
        answer1!!.typeface = arial
        answer2!!.typeface = arial
        answer3!!.typeface = arial
        answer4!!.typeface = arial
        answer5!!.typeface = arial
        buttonback!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        levelview!!.typeface = braditc
        bucksText!!.typeface = braditc
        bucksValueView!!.typeface = braditc
        buttoncheck!!.typeface = braditc
        startword!!.typeface = braditc
        answeredittext!!.typeface = braditc
        answer1!!.typeface = braditc
        answer2!!.typeface = braditc
        answer3!!.typeface = braditc
        answer4!!.typeface = braditc
        answer5!!.typeface = braditc
        buttonback!!.typeface = braditc
    }

    private fun setHeadline() {
        Log.d("TheWordGame/JustOneWord", "setHeadline() started")
        levelview!!.text = level + currentlevel
    }

    private fun fill_array_levelText() {
        Log.d("TheWordGame/JustOneWord", "fill_array_leveltext() started")
        arrayLeveltext[1] = applicationContext.getString(R.string.justoneword_level_level_1)
        arrayLeveltext[2] = applicationContext.getString(R.string.justoneword_level_level_2)
        arrayLeveltext[3] = applicationContext.getString(R.string.justoneword_level_level_3)
        arrayLeveltext[4] = applicationContext.getString(R.string.justoneword_level_level_4)
        arrayLeveltext[5] = applicationContext.getString(R.string.justoneword_level_level_5)
        arrayLeveltext[6] = applicationContext.getString(R.string.justoneword_level_level_6)
        arrayLeveltext[7] = applicationContext.getString(R.string.justoneword_level_level_7)
        arrayLeveltext[8] = applicationContext.getString(R.string.justoneword_level_level_8)
        arrayLeveltext[9] = applicationContext.getString(R.string.justoneword_level_level_9)
        arrayLeveltext[10] = applicationContext.getString(R.string.justoneword_level_level_10)
        arrayLeveltext[11] = applicationContext.getString(R.string.justoneword_level_level_11)
        arrayLeveltext[12] = applicationContext.getString(R.string.justoneword_level_level_12)
        arrayLeveltext[13] = applicationContext.getString(R.string.justoneword_level_level_13)
        arrayLeveltext[14] = applicationContext.getString(R.string.justoneword_level_level_14)
        arrayLeveltext[15] = applicationContext.getString(R.string.justoneword_level_level_15)
        arrayLeveltext[16] = applicationContext.getString(R.string.justoneword_level_level_16)
        arrayLeveltext[17] = applicationContext.getString(R.string.justoneword_level_level_17)
        arrayLeveltext[18] = applicationContext.getString(R.string.justoneword_level_level_18)
        arrayLeveltext[19] = applicationContext.getString(R.string.justoneword_level_level_19)
        arrayLeveltext[20] = applicationContext.getString(R.string.justoneword_level_level_20)
        arrayLeveltext[21] = applicationContext.getString(R.string.justoneword_level_level_21)
        arrayLeveltext[22] = applicationContext.getString(R.string.justoneword_level_level_22)
        arrayLeveltext[23] = applicationContext.getString(R.string.justoneword_level_level_23)
        arrayLeveltext[24] = applicationContext.getString(R.string.justoneword_level_level_24)
        arrayLeveltext[25] = getString(R.string.justoneword_level_level_25)
        arrayLeveltext[26] = getString(R.string.justoneword_level_level_26)
        arrayLeveltext[27] = getString(R.string.justoneword_level_level_27)
        arrayLeveltext[28] = getString(R.string.justoneword_level_level_28)
        arrayLeveltext[29] = getString(R.string.justoneword_level_level_29)
        arrayLeveltext[30] = getString(R.string.justoneword_level_level_30)
    }

    private fun fill_array_answer1() {
        Log.d("TheWordGame/JustOneWord", "fill_array_answer1() started")
        arrayAnswer1[1] = applicationContext.getString(R.string.justoneword_level_answer1_level_1)
        arrayAnswer1[2] = applicationContext.getString(R.string.justoneword_level_answer1_level_2)
        arrayAnswer1[3] = applicationContext.getString(R.string.justoneword_level_answer1_level_3)
        arrayAnswer1[4] = applicationContext.getString(R.string.justoneword_level_answer1_level_4)
        arrayAnswer1[5] = applicationContext.getString(R.string.justoneword_level_answer1_level_5)
        arrayAnswer1[6] = applicationContext.getString(R.string.justoneword_level_answer1_level_6)
        arrayAnswer1[7] = applicationContext.getString(R.string.justoneword_level_answer1_level_7)
        arrayAnswer1[8] = applicationContext.getString(R.string.justoneword_level_answer1_level_8)
        arrayAnswer1[9] = applicationContext.getString(R.string.justoneword_level_answer1_level_9)
        arrayAnswer1[10] = applicationContext.getString(R.string.justoneword_level_answer1_level_10)
        arrayAnswer1[11] = applicationContext.getString(R.string.justoneword_level_answer1_level_11)
        arrayAnswer1[12] = applicationContext.getString(R.string.justoneword_level_answer1_level_12)
        arrayAnswer1[13] = applicationContext.getString(R.string.justoneword_level_answer1_level_13)
        arrayAnswer1[14] = applicationContext.getString(R.string.justoneword_level_answer1_level_14)
        arrayAnswer1[15] = applicationContext.getString(R.string.justoneword_level_answer1_level_15)
        arrayAnswer1[16] = applicationContext.getString(R.string.justoneword_level_answer1_level_16)
        arrayAnswer1[17] = applicationContext.getString(R.string.justoneword_level_answer1_level_17)
        arrayAnswer1[18] = applicationContext.getString(R.string.justoneword_level_answer1_level_18)
        arrayAnswer1[19] = applicationContext.getString(R.string.justoneword_level_answer1_level_19)
        arrayAnswer1[20] = applicationContext.getString(R.string.justoneword_level_answer1_level_20)
        arrayAnswer1[21] = applicationContext.getString(R.string.justoneword_level_answer1_level_21)
        arrayAnswer1[22] = applicationContext.getString(R.string.justoneword_level_answer1_level_22)
        arrayAnswer1[23] = applicationContext.getString(R.string.justoneword_level_answer1_level_23)
        arrayAnswer1[24] = applicationContext.getString(R.string.justoneword_level_answer1_level_24)
        arrayAnswer1[25] = applicationContext.getString(R.string.justoneword_level_answer1_level_25)
        arrayAnswer1[26] = applicationContext.getString(R.string.justoneword_level_answer1_level_26)
        arrayAnswer1[27] = applicationContext.getString(R.string.justoneword_level_answer1_level_27)
        arrayAnswer1[28] = applicationContext.getString(R.string.justoneword_level_answer1_level_28)
        arrayAnswer1[29] = applicationContext.getString(R.string.justoneword_level_answer1_level_29)
        arrayAnswer1[30] = applicationContext.getString(R.string.justoneword_level_answer1_level_30)
    }

    private fun fill_array_answer2() {
        Log.d("TheWordGame/JustOneWord", "fill_array_answer2() started")
        arrayAnswer2[1] = applicationContext.getString(R.string.justoneword_level_answer2_level_1)
        arrayAnswer2[2] = applicationContext.getString(R.string.justoneword_level_answer2_level_2)
        arrayAnswer2[3] = applicationContext.getString(R.string.justoneword_level_answer2_level_3)
        arrayAnswer2[4] = applicationContext.getString(R.string.justoneword_level_answer2_level_4)
        arrayAnswer2[5] = applicationContext.getString(R.string.justoneword_level_answer2_level_5)
        arrayAnswer2[6] = applicationContext.getString(R.string.justoneword_level_answer2_level_6)
        arrayAnswer2[7] = applicationContext.getString(R.string.justoneword_level_answer2_level_7)
        arrayAnswer2[8] = applicationContext.getString(R.string.justoneword_level_answer2_level_8)
        arrayAnswer2[9] = applicationContext.getString(R.string.justoneword_level_answer2_level_9)
        arrayAnswer2[10] = applicationContext.getString(R.string.justoneword_level_answer2_level_10)
        arrayAnswer2[11] = applicationContext.getString(R.string.justoneword_level_answer2_level_11)
        arrayAnswer2[12] = applicationContext.getString(R.string.justoneword_level_answer2_level_12)
        arrayAnswer2[13] = applicationContext.getString(R.string.justoneword_level_answer2_level_13)
        arrayAnswer2[14] = applicationContext.getString(R.string.justoneword_level_answer2_level_14)
        arrayAnswer2[15] = applicationContext.getString(R.string.justoneword_level_answer2_level_15)
        arrayAnswer2[16] = applicationContext.getString(R.string.justoneword_level_answer2_level_16)
        arrayAnswer2[17] = applicationContext.getString(R.string.justoneword_level_answer2_level_17)
        arrayAnswer2[18] = applicationContext.getString(R.string.justoneword_level_answer2_level_18)
        arrayAnswer2[19] = applicationContext.getString(R.string.justoneword_level_answer2_level_19)
        arrayAnswer2[20] = applicationContext.getString(R.string.justoneword_level_answer2_level_20)
        arrayAnswer2[21] = applicationContext.getString(R.string.justoneword_level_answer2_level_21)
        arrayAnswer2[22] = applicationContext.getString(R.string.justoneword_level_answer2_level_22)
        arrayAnswer2[23] = applicationContext.getString(R.string.justoneword_level_answer2_level_23)
        arrayAnswer2[24] = applicationContext.getString(R.string.justoneword_level_answer2_level_24)
        arrayAnswer2[25] = applicationContext.getString(R.string.justoneword_level_answer2_level_25)
        arrayAnswer2[26] = applicationContext.getString(R.string.justoneword_level_answer2_level_26)
        arrayAnswer2[27] = applicationContext.getString(R.string.justoneword_level_answer2_level_27)
        arrayAnswer2[28] = applicationContext.getString(R.string.justoneword_level_answer2_level_28)
        arrayAnswer2[29] = applicationContext.getString(R.string.justoneword_level_answer2_level_29)
        arrayAnswer2[30] = applicationContext.getString(R.string.justoneword_level_answer2_level_30)
    }

    private fun fill_array_answer3() {
        Log.d("TheWordGame/JustOneWord", "fill_array_answer3() started")
        arrayAnswer3[1] = applicationContext.getString(R.string.justoneword_level_answer3_level_1)
        arrayAnswer3[2] = applicationContext.getString(R.string.justoneword_level_answer3_level_2)
        arrayAnswer3[3] = applicationContext.getString(R.string.justoneword_level_answer3_level_3)
        arrayAnswer3[4] = applicationContext.getString(R.string.justoneword_level_answer3_level_4)
        arrayAnswer3[5] = applicationContext.getString(R.string.justoneword_level_answer3_level_5)
        arrayAnswer3[6] = applicationContext.getString(R.string.justoneword_level_answer3_level_6)
        arrayAnswer3[7] = applicationContext.getString(R.string.justoneword_level_answer3_level_7)
        arrayAnswer3[8] = applicationContext.getString(R.string.justoneword_level_answer3_level_8)
        arrayAnswer3[9] = applicationContext.getString(R.string.justoneword_level_answer3_level_9)
        arrayAnswer3[10] = applicationContext.getString(R.string.justoneword_level_answer3_level_10)
        arrayAnswer3[11] = applicationContext.getString(R.string.justoneword_level_answer3_level_11)
        arrayAnswer3[12] = applicationContext.getString(R.string.justoneword_level_answer3_level_12)
        arrayAnswer3[13] = applicationContext.getString(R.string.justoneword_level_answer3_level_13)
        arrayAnswer3[14] = applicationContext.getString(R.string.justoneword_level_answer3_level_14)
        arrayAnswer3[15] = applicationContext.getString(R.string.justoneword_level_answer3_level_15)
        arrayAnswer3[16] = applicationContext.getString(R.string.justoneword_level_answer3_level_16)
        arrayAnswer3[17] = applicationContext.getString(R.string.justoneword_level_answer3_level_17)
        arrayAnswer3[18] = applicationContext.getString(R.string.justoneword_level_answer3_level_18)
        arrayAnswer3[19] = applicationContext.getString(R.string.justoneword_level_answer3_level_19)
        arrayAnswer3[20] = applicationContext.getString(R.string.justoneword_level_answer3_level_20)
        arrayAnswer3[21] = applicationContext.getString(R.string.justoneword_level_answer3_level_21)
        arrayAnswer3[22] = applicationContext.getString(R.string.justoneword_level_answer3_level_22)
        arrayAnswer3[23] = applicationContext.getString(R.string.justoneword_level_answer3_level_23)
        arrayAnswer3[24] = applicationContext.getString(R.string.justoneword_level_answer3_level_24)
        arrayAnswer3[25] = applicationContext.getString(R.string.justoneword_level_answer3_level_25)
        arrayAnswer3[26] = applicationContext.getString(R.string.justoneword_level_answer3_level_26)
        arrayAnswer3[27] = applicationContext.getString(R.string.justoneword_level_answer3_level_27)
        arrayAnswer3[28] = applicationContext.getString(R.string.justoneword_level_answer3_level_28)
        arrayAnswer3[29] = applicationContext.getString(R.string.justoneword_level_answer3_level_29)
        arrayAnswer3[30] = applicationContext.getString(R.string.justoneword_level_answer3_level_30)
    }

    private fun fill_array_answer4() {
        Log.d("TheWordGame/JustOneWord", "fill_array_answer4() started")
        arrayAnswer4[1] = applicationContext.getString(R.string.justoneword_level_answer4_level_1)
        arrayAnswer4[2] = applicationContext.getString(R.string.justoneword_level_answer4_level_2)
        arrayAnswer4[3] = applicationContext.getString(R.string.justoneword_level_answer4_level_3)
        arrayAnswer4[4] = applicationContext.getString(R.string.justoneword_level_answer4_level_4)
        arrayAnswer4[5] = applicationContext.getString(R.string.justoneword_level_answer4_level_5)
        arrayAnswer4[6] = applicationContext.getString(R.string.justoneword_level_answer4_level_6)
        arrayAnswer4[7] = applicationContext.getString(R.string.justoneword_level_answer4_level_7)
        arrayAnswer4[8] = applicationContext.getString(R.string.justoneword_level_answer4_level_8)
        arrayAnswer4[9] = applicationContext.getString(R.string.justoneword_level_answer4_level_9)
        arrayAnswer4[10] = applicationContext.getString(R.string.justoneword_level_answer4_level_10)
        arrayAnswer4[11] = applicationContext.getString(R.string.justoneword_level_answer4_level_11)
        arrayAnswer4[12] = applicationContext.getString(R.string.justoneword_level_answer4_level_12)
        arrayAnswer4[13] = applicationContext.getString(R.string.justoneword_level_answer4_level_13)
        arrayAnswer4[14] = applicationContext.getString(R.string.justoneword_level_answer4_level_14)
        arrayAnswer4[15] = applicationContext.getString(R.string.justoneword_level_answer4_level_15)
        arrayAnswer4[16] = applicationContext.getString(R.string.justoneword_level_answer4_level_16)
        arrayAnswer4[17] = applicationContext.getString(R.string.justoneword_level_answer4_level_17)
        arrayAnswer4[18] = applicationContext.getString(R.string.justoneword_level_answer4_level_18)
        arrayAnswer4[19] = applicationContext.getString(R.string.justoneword_level_answer4_level_19)
        arrayAnswer4[20] = applicationContext.getString(R.string.justoneword_level_answer4_level_20)
        arrayAnswer4[21] = applicationContext.getString(R.string.justoneword_level_answer4_level_21)
        arrayAnswer4[22] = applicationContext.getString(R.string.justoneword_level_answer4_level_22)
        arrayAnswer4[23] = applicationContext.getString(R.string.justoneword_level_answer4_level_23)
        arrayAnswer4[24] = applicationContext.getString(R.string.justoneword_level_answer4_level_24)
        arrayAnswer4[25] = applicationContext.getString(R.string.justoneword_level_answer4_level_25)
        arrayAnswer4[26] = applicationContext.getString(R.string.justoneword_level_answer4_level_26)
        arrayAnswer4[27] = applicationContext.getString(R.string.justoneword_level_answer4_level_27)
        arrayAnswer4[28] = applicationContext.getString(R.string.justoneword_level_answer4_level_28)
        arrayAnswer4[29] = applicationContext.getString(R.string.justoneword_level_answer4_level_29)
        arrayAnswer4[30] = applicationContext.getString(R.string.justoneword_level_answer4_level_30)
    }

    private fun fill_array_answer5() {
        Log.d("TheWordGame/JustOneWord", "fill_array_answer5() started")
        arrayAnswer5[1] = applicationContext.getString(R.string.justoneword_level_answer5_level_1)
        arrayAnswer5[2] = applicationContext.getString(R.string.justoneword_level_answer5_level_2)
        arrayAnswer5[3] = applicationContext.getString(R.string.justoneword_level_answer5_level_3)
        arrayAnswer5[4] = applicationContext.getString(R.string.justoneword_level_answer5_level_4)
        arrayAnswer5[5] = applicationContext.getString(R.string.justoneword_level_answer5_level_5)
        arrayAnswer5[6] = applicationContext.getString(R.string.justoneword_level_answer5_level_6)
        arrayAnswer5[7] = applicationContext.getString(R.string.justoneword_level_answer5_level_7)
        arrayAnswer5[8] = applicationContext.getString(R.string.justoneword_level_answer5_level_8)
        arrayAnswer5[9] = applicationContext.getString(R.string.justoneword_level_answer5_level_9)
        arrayAnswer5[10] = applicationContext.getString(R.string.justoneword_level_answer5_level_10)
        arrayAnswer5[11] = applicationContext.getString(R.string.justoneword_level_answer5_level_11)
        arrayAnswer5[12] = applicationContext.getString(R.string.justoneword_level_answer5_level_12)
        arrayAnswer5[13] = applicationContext.getString(R.string.justoneword_level_answer5_level_13)
        arrayAnswer5[14] = applicationContext.getString(R.string.justoneword_level_answer5_level_14)
        arrayAnswer5[15] = applicationContext.getString(R.string.justoneword_level_answer5_level_15)
        arrayAnswer5[16] = applicationContext.getString(R.string.justoneword_level_answer5_level_16)
        arrayAnswer5[17] = applicationContext.getString(R.string.justoneword_level_answer5_level_17)
        arrayAnswer5[18] = applicationContext.getString(R.string.justoneword_level_answer5_level_18)
        arrayAnswer5[19] = applicationContext.getString(R.string.justoneword_level_answer5_level_19)
        arrayAnswer5[20] = applicationContext.getString(R.string.justoneword_level_answer5_level_20)
        arrayAnswer5[21] = applicationContext.getString(R.string.justoneword_level_answer5_level_21)
        arrayAnswer5[22] = applicationContext.getString(R.string.justoneword_level_answer5_level_22)
        arrayAnswer5[23] = applicationContext.getString(R.string.justoneword_level_answer5_level_23)
        arrayAnswer5[24] = applicationContext.getString(R.string.justoneword_level_answer5_level_24)
        arrayAnswer5[25] = applicationContext.getString(R.string.justoneword_level_answer5_level_25)
        arrayAnswer5[26] = applicationContext.getString(R.string.justoneword_level_answer5_level_26)
        arrayAnswer5[27] = applicationContext.getString(R.string.justoneword_level_answer5_level_27)
        arrayAnswer5[28] = applicationContext.getString(R.string.justoneword_level_answer5_level_28)
        arrayAnswer5[29] = applicationContext.getString(R.string.justoneword_level_answer5_level_29)
        arrayAnswer5[30] = applicationContext.getString(R.string.justoneword_level_answer5_level_30)
    }

    private fun increaseStartCounter() {
        Log.d("Debug/LogicWord", gameModeStartCounter.toString())
        gameModeStartCounter++
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("startCounter", gameModeStartCounter)
        editor.apply()
        Log.d("Debug/LogicWord", gameModeStartCounter.toString())
    }

    private fun startCounter() {
        if (dontrememberagain != 1) {
            if (gameModeStartCounter >= 5) {
                builder!!.setTitle(getString(R.string.div_ratedialog_title))
                        .setMessage(getString(R.string.div_ratedialog_message) + "\n" + getString(R.string.div_ratedialog_message_p2))
                        .setPositiveButton(getString(R.string.div_ratedialog_positivebutton)) { dialogInterface, i ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=nesat.thewordgame"))
                            startActivity(intent)
                            bucksSave = bucksSave + 200
                            writeData()
                            val savegame = getSharedPreferences("savegame", 0)
                            val editor = savegame.edit()
                            editor.putInt("dontrememberagain", 1)
                            editor.apply()
                            startActivity(toStore)
                        }
                        .setNegativeButton(getString(R.string.div_ratedialog_negativebutton)) { dialogInterface, i ->
                            val savegame = getSharedPreferences("savegame", 0)
                            val editor = savegame.edit()
                            editor.putInt("startCounter", 0)
                            editor.apply()
                        }
                        .setNeutralButton(getString(R.string.div_ratedialog_neutralbutton)) { dialogInterface, i ->
                            val savegame = getSharedPreferences("savegame", 0)
                            val editor = savegame.edit()
                            editor.putInt("dontrememberagain", 1)
                            editor.apply()
                        }
                        .create().show()
            }
        }
    }

    private fun setMainText_v2() {
        Log.d("TheWordGame/JustOneWord", "setMainText_v2() started")
        startword!!.text = arrayLeveltext[currentlevel]
    }

    private fun checkAnswer_v2() {
        Log.d("TheWordGame/JustOneWord", "checkAnswer_v2() started")
        if (currentlevel >= 30) {
            builder!!.setCancelable(true)
                    .setTitle("No more levels")
                    .setMessage("Please wait for an update")
                    .create().show()
            currentlevel = 25
            writeData()

        }
        if (answeredittext!!.editableText.toString().equals(arrayAnswer1[currentlevel], ignoreCase = true)) {
            correct!!.start()
            answer1!!.visibility = View.VISIBLE
        } else if (answeredittext!!.editableText.toString().equals(arrayAnswer2[currentlevel], ignoreCase = true)) {
            correct!!.start()
            answer2!!.visibility = View.VISIBLE
        } else if (answeredittext!!.editableText.toString().equals(arrayAnswer3[currentlevel], ignoreCase = true)) {
            correct!!.start()
            answer3!!.visibility = View.VISIBLE
        } else if (answeredittext!!.editableText.toString().equals(arrayAnswer4[currentlevel], ignoreCase = true)) {
            correct!!.start()
            answer4!!.visibility = View.VISIBLE
        } else if (answeredittext!!.editableText.toString().equals(arrayAnswer5[currentlevel], ignoreCase = true)) {
            correct!!.start()
            answer5!!.visibility = View.VISIBLE
        }
        setLevelAndAnswers()
    }

    private fun setAnswer1_v2() {
        Log.d("TheWordGame/JustOneWord", "setAnswer1_v2() started")
        answer1!!.text = arrayAnswer1[currentlevel]
    }

    private fun setAnswer2_v2() {
        Log.d("TheWordGame/JustOneWord", "setAnswer2_v2() started")
        answer2!!.text = arrayAnswer2[currentlevel]
    }

    private fun setAnswer3_v2() {
        Log.d("TheWordGame/JustOneWord", "setAnswer3_v2() started")
        answer3!!.text = arrayAnswer3[currentlevel]
    }

    private fun setAnswer4_v2() {
        Log.d("TheWordGame/JustOneWord", "setAnswer4_v2() started")
        answer4!!.text = arrayAnswer4[currentlevel]
    }

    private fun setAnswer5_v2() {
        Log.d("TheWordGame/JustOneWord", "setAnswer5_v2() started")
        answer5!!.text = arrayAnswer5[currentlevel]
    }

    private fun playLastCorrect() {
        Log.d("TheWordGame/JustOneWord", "playLastCorrect() started")
        lastcorrect!!.start()
    }

    private fun resetedittext() {
        Log.d("TheWordGame/JustOneWord", "resetedittext() started")
        answeredittext!!.setText("")
    }

    private fun setLevelAndAnswers() {
        Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers() started")
        if (answer1!!.visibility == View.VISIBLE) {
            Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Answer1 visible")
            if (answer2!!.visibility == View.VISIBLE) {
                Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Answer2 visible")
                if (answer3!!.visibility == View.VISIBLE) {
                    Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Answer3 visible")
                    if (answer4!!.visibility == View.VISIBLE) {
                        Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Answer4 visible")
                        if (answer5!!.visibility == View.VISIBLE) {
                            Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Answer5 visible")
                            buttoncheck!!.isClickable = false
                            handler.postDelayed({
                                Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/PostDelayed Handler started")
                                currentlevel++
                                writeData()
                                setHeadline()
                                setMainText_v2()
                                setAnswer1_v2()
                                setAnswer2_v2()
                                setAnswer3_v2()
                                setAnswer4_v2()
                                setAnswer5_v2()
                                buttoncheck!!.isClickable = true
                                answer1!!.visibility = View.INVISIBLE
                                answer2!!.visibility = View.INVISIBLE
                                answer3!!.visibility = View.INVISIBLE
                                answer4!!.visibility = View.INVISIBLE
                                answer5!!.visibility = View.INVISIBLE
                                playLastCorrect()
                                Log.d("TheWordGame/JustOneWord", "setLevelAndAnswers/Handler/Done")
                            }, 1500)
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/JustOneWord", "setOnClickListeners() started")
        levelview!!.setOnClickListener(this)
        buttoncheck!!.setOnClickListener(this)
        buttonback!!.setOnClickListener(this)
    }

    private fun fadeOutCorrectAnswers() {
        Log.d("TheWordGame/JustOneWord", "fadeOutCorrectAnswers() started")
        val fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        if (answer1!!.visibility == View.VISIBLE) {
            answer1!!.startAnimation(fadeOut)
        }
        if (answer2!!.visibility == View.VISIBLE) {
            answer2!!.startAnimation(fadeOut)
        }
        if (answer3!!.visibility == View.VISIBLE) {
            answer3!!.startAnimation(fadeOut)
        }
        if (answer4!!.visibility == View.VISIBLE) {
            answer4!!.startAnimation(fadeOut)
        }
        if (answer5!!.visibility == View.VISIBLE) {
            answer5!!.startAnimation(fadeOut)
        }
    }

}