package nesat.thewordgame

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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

class LogicWordActivityK : AppCompatActivity(), View.OnClickListener {

    //Intents
    private val toMainMenu = Intent()
    private val toStore = Intent()

    //UI Components
    private var answerEditText: EditText? = null
    private var checkButton: Button? = null
    private var hint1Button: Button? = null
    private var hint2Button: Button? = null
    private var hint3Button: Button? = null
    private var backButtonPlay: Button? = null
    private var levelTextView: TextView? = null
    private var currentLevelTextView: TextView? = null
    private var bucksValueView: TextView? = null
    private var bucksTextView: TextView? = null
    private var adView: AdView? = null
    private val interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null

    private var shadowsintolight: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var braditc: Typeface? = null

    //AlertDialog.Builder
    private var builder: AlertDialog.Builder? = null
    private var hint: AlertDialog.Builder? = null

    //MediaPlayers
    private var logicWordMusic: MediaPlayer? = null
    private var correctSound: MediaPlayer? = null
    private var wrongSound: MediaPlayer? = null
    private var mediaPlayer: MediaPlayer? = null

    //Animations
    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    //Handlers
    private var handler: Handler? = null

    //Vibratores
    private var vibrator: Vibrator? = null

    //Integers
    private var bucksSave: Int = 0
    private var gametheme: Int = 0
    private var gameFont: Int = 0
    private val price_hint1 = 10
    private val price_hint2 = 25
    private val price_hint3 = 100
    private var adCounter: Int = 0
    private var dontrememberagain: Int = 0
    private var gameModeStartCounter: Int = 0
    private var currentLevel: Int = 0
    private var emptyArrayPos: Int = 0
    private var soundsAllowed: Int = 0

    //Strings
    private var firstLetter: String? = null
    private var hint1AlreadyBought: Int = 0
    private var hint2AlreadyBought: Int = 0
    private var hint3AlreadyBought: Int = 0
    private var h1ABFormat: String? = null
    private var h2ABFormat: String? = null
    private var h3ABFormat: String? = null

    //Arrays
    private val array_leveltext = arrayOfNulls<String>(52)
    private val array_answer = arrayOfNulls<String>(52)
    private val array_hint2 = arrayOfNulls<String>(52)

    private val mainMenuActivityk: MainMenuActivityK? = null

    //AdRequest
    private val adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/LogicWord", "@Override onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logicword)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolight = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        logicWordMusic = MediaPlayer.create(this, R.raw.gamemode)
        correctSound = MediaPlayer.create(this, R.raw.correct)
        wrongSound = MediaPlayer.create(this, R.raw.wrong)
        logicWordMusic!!.isLooping = true

        val settings = getSharedPreferences("settings", 0)
        if (settings.getInt("musicAllowed", 0) == 1) {
            logicWordMusic!!.start()
        }

        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        toMainMenu.action = Intent.ACTION_CALL
        toMainMenu.setClass(applicationContext, CategorySelectorActivityK::class.java)
        toStore.action = Intent.ACTION_VIEW
        toStore.data = Uri.parse("https://play.google.com/store/apps/details?id=nesat.thewordgame")
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.click_2)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        handler = Handler()
        builder = AlertDialog.Builder(this)
        hint = AlertDialog.Builder(this)
        val savegame = getSharedPreferences("savegame", 0)

        fill_arrays()
        findEmptyPosition()
        findIds()
        getData()
        setTextFirstStart()

        if (savegame.getInt("level_logicword", 0) == 0) {
            currentLevel = 1
            writeData()
            setTextFirstStart()
        } else {
            currentLevel = savegame.getInt("level_logicword", 0)
            setTextFirstStart()
        }

        currentLevelTextView!!.text = applicationContext.getString(R.string.logicword_levelview_level) + " " + currentLevel

        setTheme()
        setFont()
        setOnClickListeners()
        adView!!.loadAd(adRequest)
        increaseStartCounter()
        handler!!.postDelayed({ startCounter() }, 750)

        loadFirstLetter()
        loadAlreadyBought()
    }

    override fun onPause() {
        Log.d("TheWordGame/LogicWord", "@Override onPause() started")
        super.onPause()
        logicWordMusic!!.pause()
    }

    override fun onResume() {
        Log.d("TheWordGame/LogicWord", "@Override onResume() started")
        super.onResume()
        val settings = getSharedPreferences("settings", 0)
        if (settings.getInt("musicAllowed", 0) == 1) {
            logicWordMusic!!.start()
        }
    }

    override fun onStop() {
        Log.d("TheWordGame/LogicWord", "@Override onStop() started")
        super.onStop()
        logicWordMusic!!.pause()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/LogicWord", "@Override onClick(View view) started")
        when (view.id) {

            R.id.hint1Button -> {
                playSound()
                if (h1AB() && bucksSave >= price_hint1) {
                    loadFirstLetter()
                    hint!!.setCancelable(false)
                            .setTitle(getString(R.string.logicword_hint1_title))
                            .setMessage(getString(R.string.logicword_hint1_message) + " " + firstLetter)
                            .setPositiveButton(getString(R.string.logicword_hint1_positivebutton)) { dialogInterface, i -> }
                            .create()
                            .show()
                } else if (bucksSave >= price_hint1) {
                    bucksSave = bucksSave - price_hint1
                    bucksValueView!!.text = bucksSave.toString()
                    writeData()
                    val savegame = getSharedPreferences("savegame", 0)
                    val editor = savegame.edit()
                    editor.putInt(h1ABFormat, 1)
                    editor.apply()
                    loadFirstLetter()
                    hint!!.setCancelable(false)
                            .setTitle(getString(R.string.logicword_hint1_title))
                            .setMessage(getString(R.string.logicword_hint1_message) + " " + firstLetter)
                            .setPositiveButton(getString(R.string.logicword_hint1_positivebutton)) { dialogInterface, i -> }
                            .create()
                            .show()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.logicword_hint_purchase_notpossible), Toast.LENGTH_SHORT).show()
                }
            }


            R.id.hint2Button -> {
                playSound()
                if (h2AB() && bucksSave >= price_hint2) {
                    loadFirstLetter()
                    hint!!.setCancelable(false)
                            .setTitle(getString(R.string.logicword_hint2_title))
                            .setMessage(array_hint2[currentLevel])
                            .setPositiveButton(getString(R.string.logicword_hint2_positivebutton)) { dialogInterface, i -> }
                            .create()
                            .show()
                } else {
                    if (bucksSave >= price_hint2) {
                        bucksSave = bucksSave - price_hint2
                        bucksValueView!!.text = bucksSave.toString()
                        writeData()
                        val savegame = getSharedPreferences("savegame", 0)
                        val editor = savegame.edit()
                        editor.putInt(h2ABFormat, 1)
                        editor.apply()
                        loadFirstLetter()
                        hint!!.setCancelable(false)
                                .setTitle(getString(R.string.logicword_hint2_title))
                                .setMessage(array_hint2[currentLevel])
                                .setPositiveButton(getString(R.string.logicword_hint2_positivebutton)) { dialogInterface, i -> }
                                .create()
                                .show()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.logicword_hint_purchase_notpossible), Toast.LENGTH_SHORT).show()
                    }
                }
            }


            R.id.hint3Button -> {
                playSound()
                if (bucksSave >= price_hint3) {
                    bucksSave = bucksSave - price_hint3
                    bucksValueView!!.text = bucksSave.toString()
                    writeData()
                    Toast.makeText(applicationContext, getString(R.string.logicword_hint3_toast), Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, getString(R.string.logicword_hint3_toast_answer) + " " + array_answer[currentLevel], Toast.LENGTH_SHORT).show()
                    currentLevel++
                    currentLevelTextView!!.text = getString(R.string.logicword_textview_headline) + " " + currentLevel
                    levelTextView!!.text = array_leveltext[currentLevel].toString()
                    writeData()
                } else {
                    Toast.makeText(applicationContext, R.string.logicword_hint_purchase_notpossible, Toast.LENGTH_SHORT).show()
                }
            }


            R.id.checkButton -> {
                playSound()
                checkAnswer_v2()
                adCounter++
                //loadInterstitial();
                Log.d("TheWordGame/LogicWord", "adCounter: $adCounter")
                if (adCounter >= 10) {
                    showInterstitial()
                    adCounter = 0
                    Log.d("TheWordGame/LogicWord", "adCounter: $adCounter")
                }
            }

            R.id.backButtonPlay -> {
                playSound()
                logicWordMusic!!.pause()
                try {
                    if (MainMenuActivityK().player.isPlaying) {
                        MainMenuActivityK().player.reset()
                    }
                } catch (n: NullPointerException) {
                    Log.d("ERROR", "NullPointerExeption onClick()")
                } catch (i: IllegalStateException) {
                    Log.d("ERROR", "IllegalStateExeption onClick()")
                }

                val settings = getSharedPreferences("settings", 0)
                val i = Intent(this, CategorySelectorActivityK::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun loadAlreadyBought() {
        val savegame = getSharedPreferences("savegame", 0)
        h1ABFormat = "savegame_" + "level_" + currentLevel + "_hint1_" + "alreadyBought"
        h2ABFormat = "savegame_" + "level_" + currentLevel + "_hint2_" + "alreadyBought"
        h3ABFormat = "savegame_" + "level_" + currentLevel + "_hint3_" + "alreadyBought"
        hint1AlreadyBought = savegame.getInt(h1ABFormat, 0)
        hint2AlreadyBought = savegame.getInt(h2ABFormat, 0)
        hint3AlreadyBought = savegame.getInt(h3ABFormat, 0)
    }

    private fun h1AB(): Boolean {
        var b = false
        loadAlreadyBought()
        if (hint1AlreadyBought == 1) {
            b = true
        }
        return b
    }

    private fun h2AB(): Boolean {
        var b = false
        loadAlreadyBought()
        if (hint2AlreadyBought == 1) {
            b = true
        }
        return b
    }

    private fun h3AB(): Boolean {
        var b = false
        loadAlreadyBought()
        if (hint3AlreadyBought == 1) {
            b = true
        }
        return b
    }

    private fun loadFirstLetter() {
        firstLetter = array_answer[currentLevel]!!.get(0).toString()
    }

    private fun loadRewardedVideoAd() {
        rewardedVideoAd!!.loadAd("ca-app-pub-3940256099942544/5224354917", AdRequest.Builder().build())
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-8865326875373213/5790758949"
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {

            }

            override fun onAdClosed() {

            }
        }
        return interstitialAd
    }

    private fun showInterstitial() {
        if (interstitialAd != null && interstitialAd.isLoaded) {
            interstitialAd.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build()
        interstitialAd!!.loadAd(adRequest)
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    private fun setTextFirstStart() {
        Log.d("TheWordGame/LogicWord", "setTextFirstStart() started")
        currentLevelTextView!!.text = currentLevel.toString()
        levelTextView!!.text = array_leveltext[currentLevel]
    }

    private fun fill_arrays() {
        Log.d("TheWordGame/LogicWord", "fill_arrays() started")
        fill_array_leveltext()
        fill_array_answer()
        fill_array_hint2()
    }

    private fun checkAnswer_v2() {
        Log.d("TheWordGame/LogicWord", "checkAnswer_v2() started")
        if (currentLevel >= 49) {
            builder!!.setCancelable(true)
                    .setTitle("No more levels")
                    .setMessage("Please wait for an update")
                    .create()
                    .show()
            currentLevel = 45
            writeData()
        }
        if (answerEditText!!.editableText.toString().equals(array_answer[currentLevel], ignoreCase = true)) {
            if (currentLevel < 45) {
                answerEditText!!.text = null
                currentLevel++
                writeData()
                increaseBucks(10)
                playCorrect()
                currentLevelTextView!!.text = applicationContext.getString(R.string.logicword_levelview_level) + " " + currentLevel
                levelTextView!!.text = array_leveltext[currentLevel]
            }
        } else {
            wrongAnswer()
            answerEditText!!.text = null
        }
    }

    private fun findEmptyPosition() {
        Log.d("TheWordGame/LogicWord", "findEmptyPosition() started")
        emptyArrayPos = 0
        for (i in 0..array_leveltext.size) {
            if (array_leveltext[i] != null) {
                Log.d("TheWordGame/LogicWord", "$i : not null")
            } else {
                Log.d("TheWordGame/LogicWord", "null")
                emptyArrayPos = i
                break
            }
        }
        Log.d("TheWordGame/LogicWord", "Empty position: $emptyArrayPos")
    }

    private fun fill_array_leveltext() {
        Log.d("LogicWord", "fill_array_leveltext() started")
        array_leveltext[0] = "0"
        array_leveltext[1] = applicationContext.getString(R.string.logicword_level_level_1)
        array_leveltext[2] = applicationContext.getString(R.string.logicword_level_level_2)
        array_leveltext[3] = applicationContext.getString(R.string.logicword_level_level_3)
        array_leveltext[4] = applicationContext.getString(R.string.logicword_level_level_4)
        array_leveltext[5] = applicationContext.getString(R.string.logicword_level_level_5)
        array_leveltext[6] = applicationContext.getString(R.string.logicword_level_level_6)
        array_leveltext[7] = applicationContext.getString(R.string.logicword_level_level_7)
        array_leveltext[8] = applicationContext.getString(R.string.logicword_level_level_8)
        array_leveltext[9] = applicationContext.getString(R.string.logicword_level_level_9)
        array_leveltext[10] = applicationContext.getString(R.string.logicword_level_level_10)
        array_leveltext[11] = applicationContext.getString(R.string.logicword_level_level_11)
        array_leveltext[12] = applicationContext.getString(R.string.logicword_level_level_12)
        array_leveltext[13] = applicationContext.getString(R.string.logicword_level_level_13)
        array_leveltext[14] = applicationContext.getString(R.string.logicword_level_level_14)
        array_leveltext[15] = applicationContext.getString(R.string.logicword_level_level_15)
        array_leveltext[16] = applicationContext.getString(R.string.logicword_level_level_16)
        array_leveltext[17] = applicationContext.getString(R.string.logicword_level_level_17)
        array_leveltext[18] = applicationContext.getString(R.string.logicword_level_level_18)
        array_leveltext[19] = applicationContext.getString(R.string.logicword_level_level_19)
        array_leveltext[20] = applicationContext.getString(R.string.logicword_level_level_20)
        array_leveltext[21] = applicationContext.getString(R.string.logicword_level_level_21)
        array_leveltext[22] = applicationContext.getString(R.string.logicword_level_level_22)
        array_leveltext[23] = applicationContext.getString(R.string.logicword_level_level_23)
        array_leveltext[24] = applicationContext.getString(R.string.logicword_level_level_24)
        array_leveltext[25] = applicationContext.getString(R.string.logicword_level_level_25)
        array_leveltext[26] = applicationContext.getString(R.string.logicword_level_level_26)
        array_leveltext[27] = applicationContext.getString(R.string.logicword_level_level_27)
        array_leveltext[28] = applicationContext.getString(R.string.logicword_level_level_28)
        array_leveltext[29] = applicationContext.getString(R.string.logicword_level_level_29)
        array_leveltext[30] = applicationContext.getString(R.string.logicword_level_level_30)
        array_leveltext[31] = applicationContext.getString(R.string.logicword_level_level_31)
        array_leveltext[32] = applicationContext.getString(R.string.logicword_level_level_32)
        array_leveltext[33] = applicationContext.getString(R.string.logicword_level_level_33)
        array_leveltext[34] = applicationContext.getString(R.string.logicword_level_level_34)
        array_leveltext[35] = applicationContext.getString(R.string.logicword_level_level_35)
        array_leveltext[36] = applicationContext.getString(R.string.logicword_level_level_36)
        array_leveltext[37] = applicationContext.getString(R.string.logicword_level_level_37)
        array_leveltext[38] = applicationContext.getString(R.string.logicword_level_level_38)
        array_leveltext[39] = applicationContext.getString(R.string.logicword_level_level_39)
        array_leveltext[40] = applicationContext.getString(R.string.logicword_level_level_40)
        array_leveltext[41] = applicationContext.getString(R.string.logicword_level_level_41)
        array_leveltext[42] = applicationContext.getString(R.string.logicword_level_level_42)
        array_leveltext[43] = applicationContext.getString(R.string.logicword_level_level_43)
        array_leveltext[44] = applicationContext.getString(R.string.logicword_level_level_44)
        array_leveltext[45] = applicationContext.getString(R.string.logicword_level_level_45)
        array_leveltext[46] = applicationContext.getString(R.string.logicword_level_level_46)
        array_leveltext[47] = applicationContext.getString(R.string.logicword_level_level_47)
        array_leveltext[48] = applicationContext.getString(R.string.logicword_level_level_48)
        array_leveltext[49] = applicationContext.getString(R.string.logicword_level_level_49)
        array_leveltext[50] = applicationContext.getString(R.string.logicword_level_level_50)
    }

    private fun fill_array_answer() {
        Log.d("LogicWord", "fill_array_answer() started")
        array_answer[1] = applicationContext.getString(R.string.logicword_answer_level_1)
        array_answer[2] = applicationContext.getString(R.string.logicword_answer_level_2)
        array_answer[3] = applicationContext.getString(R.string.logicword_answer_level_3)
        array_answer[4] = applicationContext.getString(R.string.logicword_answer_level_4)
        array_answer[5] = applicationContext.getString(R.string.logicword_answer_level_5)
        array_answer[6] = applicationContext.getString(R.string.logicword_answer_level_6)
        array_answer[7] = applicationContext.getString(R.string.logicword_answer_level_7)
        array_answer[8] = applicationContext.getString(R.string.logicword_answer_level_8)
        array_answer[9] = applicationContext.getString(R.string.logicword_answer_level_9)
        array_answer[10] = applicationContext.getString(R.string.logicword_answer_level_10)
        array_answer[11] = applicationContext.getString(R.string.logicword_answer_level_11)
        array_answer[12] = applicationContext.getString(R.string.logicword_answer_level_12)
        array_answer[13] = applicationContext.getString(R.string.logicword_answer_level_13)
        array_answer[14] = applicationContext.getString(R.string.logicword_answer_level_14)
        array_answer[15] = applicationContext.getString(R.string.logicword_answer_level_15)
        array_answer[16] = applicationContext.getString(R.string.logicword_answer_level_16)
        array_answer[17] = applicationContext.getString(R.string.logicword_answer_level_17)
        array_answer[18] = applicationContext.getString(R.string.logicword_answer_level_18)
        array_answer[19] = applicationContext.getString(R.string.logicword_answer_level_19)
        array_answer[20] = applicationContext.getString(R.string.logicword_answer_level_20)
        array_answer[21] = applicationContext.getString(R.string.logicword_answer_level_21)
        array_answer[22] = applicationContext.getString(R.string.logicword_answer_level_22)
        array_answer[23] = applicationContext.getString(R.string.logicword_answer_level_23)
        array_answer[24] = applicationContext.getString(R.string.logicword_answer_level_24)
        array_answer[25] = applicationContext.getString(R.string.logicword_answer_level_25)
        array_answer[26] = applicationContext.getString(R.string.logicword_answer_level_26)
        array_answer[27] = applicationContext.getString(R.string.logicword_answer_level_27)
        array_answer[28] = applicationContext.getString(R.string.logicword_answer_level_28)
        array_answer[29] = applicationContext.getString(R.string.logicword_answer_level_29)
        array_answer[30] = applicationContext.getString(R.string.logicword_answer_level_30)
        array_answer[31] = applicationContext.getString(R.string.logicword_answer_level_31)
        array_answer[32] = applicationContext.getString(R.string.logicword_answer_level_32)
        array_answer[33] = applicationContext.getString(R.string.logicword_answer_level_33)
        array_answer[34] = applicationContext.getString(R.string.logicword_answer_level_34)
        array_answer[35] = applicationContext.getString(R.string.logicword_answer_level_35)
        array_answer[36] = applicationContext.getString(R.string.logicword_answer_level_36)
        array_answer[37] = applicationContext.getString(R.string.logicword_answer_level_37)
        array_answer[38] = applicationContext.getString(R.string.logicword_answer_level_38)
        array_answer[39] = applicationContext.getString(R.string.logicword_answer_level_39)
        array_answer[40] = applicationContext.getString(R.string.logicword_answer_level_40)
        array_answer[41] = applicationContext.getString(R.string.logicword_answer_level_41)
        array_answer[42] = applicationContext.getString(R.string.logicword_answer_level_42)
        array_answer[43] = applicationContext.getString(R.string.logicword_answer_level_43)
        array_answer[44] = applicationContext.getString(R.string.logicword_answer_level_44)
        array_answer[45] = applicationContext.getString(R.string.logicword_answer_level_45)
        array_answer[46] = applicationContext.getString(R.string.logicword_answer_level_45)
        array_answer[47] = applicationContext.getString(R.string.logicword_answer_level_46)
        array_answer[48] = applicationContext.getString(R.string.logicword_answer_level_47)
        array_answer[49] = applicationContext.getString(R.string.logicword_answer_level_48)
        array_answer[50] = applicationContext.getString(R.string.logicword_answer_level_49)
    }

    private fun fill_array_hint2() {
        Log.d("TheWordGame/LogicWord", "fill_array_hint2_v2() started")
        array_hint2[1] = getString(R.string.logicword_hint2_level_1)
        array_hint2[2] = getString(R.string.logicword_hint2_level_2)
        array_hint2[3] = getString(R.string.logicword_hint2_level_3)
        array_hint2[4] = getString(R.string.logicword_hint2_level_4)
        array_hint2[5] = getString(R.string.logicword_hint2_level_5)
        array_hint2[6] = getString(R.string.logicword_hint2_level_6)
        array_hint2[7] = getString(R.string.logicword_hint2_level_7)
        array_hint2[8] = getString(R.string.logicword_hint2_level_8)
        array_hint2[9] = getString(R.string.logicword_hint2_level_9)
        array_hint2[10] = getString(R.string.logicword_hint2_level_10)
        array_hint2[11] = getString(R.string.logicword_hint2_level_11)
        array_hint2[12] = getString(R.string.logicword_hint2_level_12)
        array_hint2[13] = getString(R.string.logicword_hint2_level_13)
        array_hint2[14] = getString(R.string.logicword_hint2_level_14)
        array_hint2[15] = getString(R.string.logicword_hint2_level_15)
        array_hint2[16] = getString(R.string.logicword_hint2_level_16)
        array_hint2[17] = getString(R.string.logicword_hint2_level_17)
        array_hint2[18] = getString(R.string.logicword_hint2_level_18)
        array_hint2[19] = getString(R.string.logicword_hint2_level_19)
        array_hint2[20] = getString(R.string.logicword_hint2_level_20)
        array_hint2[21] = getString(R.string.logicword_hint2_level_21)
        array_hint2[22] = getString(R.string.logicword_hint2_level_22)
        array_hint2[23] = getString(R.string.logicword_hint2_level_23)
        array_hint2[24] = getString(R.string.logicword_hint2_level_24)
        array_hint2[25] = getString(R.string.logicword_hint2_level_25)
        array_hint2[26] = getString(R.string.logicword_hint2_level_26)
        array_hint2[27] = getString(R.string.logicword_hint2_level_27)
        array_hint2[28] = getString(R.string.logicword_hint2_level_28)
        array_hint2[29] = getString(R.string.logicword_hint2_level_29)
        array_hint2[30] = getString(R.string.logicword_hint2_level_30)
        array_hint2[31] = getString(R.string.logicword_hint2_level_31)
        array_hint2[32] = getString(R.string.logicword_hint2_level_32)
        array_hint2[33] = getString(R.string.logicword_hint2_level_33)
        array_hint2[34] = getString(R.string.logicword_hint2_level_34)
        array_hint2[35] = getString(R.string.logicword_hint2_level_35)
        array_hint2[36] = getString(R.string.logicword_hint2_level_36)
        array_hint2[37] = getString(R.string.logicword_hint2_level_37)
        array_hint2[38] = getString(R.string.logicword_hint2_level_38)
        array_hint2[39] = getString(R.string.logicword_hint2_level_39)
        array_hint2[40] = getString(R.string.logicword_hint2_level_40)
        array_hint2[41] = getString(R.string.logicword_hint2_level_41)
        array_hint2[42] = getString(R.string.logicword_hint2_level_42)
        array_hint2[43] = getString(R.string.logicword_hint2_level_43)
        array_hint2[44] = getString(R.string.logicword_hint2_level_44)
        array_hint2[45] = getString(R.string.logicword_hint2_level_45)
        array_hint2[46] = getString(R.string.logicword_hint2_level_46)
        array_hint2[47] = getString(R.string.logicword_hint2_level_47)
        array_hint2[48] = getString(R.string.logicword_hint2_level_48)
        array_hint2[49] = getString(R.string.logicword_hint2_level_49)
        array_hint2[50] = getString(R.string.logicword_hint2_level_50)
    }

    private fun writeData() {
        Log.d("TheWordGame/LogicWord", "writeData() started")
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("level_logicword", currentLevel)
        editor.putInt("bucks", bucksSave)
        editor.apply()
        bucksValueView!!.text = bucksSave.toString()
    }

    private fun getData() {
        Log.d("TheWordGame/LogicWord", "getData() started")
        val savegame = getSharedPreferences("savegame", 0)
        bucksSave = savegame.getInt("bucks", 0)
        bucksValueView!!.text = bucksSave.toString()
        dontrememberagain = savegame.getInt("dontrememberagain", 0)
        gameModeStartCounter = savegame.getInt("startCounter", 0)
        Log.d("Debug/LogicWord", gameModeStartCounter.toString())
        val settings = getSharedPreferences("settings", 0)
        gametheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
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

    private fun setTheme() {
        Log.d("TheWordGame/LogicWord", "setTheme() started")
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

    private fun setTheme1() {
        Log.d("TheWordGame/LogicWord", "setTheme1() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/LogicWord", "setTheme2() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/LogicWord", "setTheme3() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/LogicWord", "setTheme4() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/LogicWord", "setTheme5() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/LogicWord", "setTheme6() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/LogicWord", "setTheme7() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/LogicWord", "setTheme8() started")
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        currentLevelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        checkButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        checkButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint1Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint1Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint2Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint2Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        hint3Button!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        hint3Button!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButtonPlay!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        backButtonPlay!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        answerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        answerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setFont() {
        Log.d("TheWordGame/ThemeActivi", "setFont() started")
        if (gameFont == 1) {
            setFont1()
        } else if (gameFont == 2) {
            setFont2()
        } else if (gameFont == 3) {
            setFont3()
        } else if (gameFont == 4) {
            setFont4()
        }
    }

    private fun setFont1() {
        Log.d("TheWordGame/ThemeActivi", "setFont1() started")
        levelTextView!!.typeface = shadowsintolight
        bucksTextView!!.typeface = shadowsintolight
        bucksValueView!!.typeface = shadowsintolight
        currentLevelTextView!!.typeface = shadowsintolight
        checkButton!!.typeface = shadowsintolight
        answerEditText!!.typeface = shadowsintolight
        hint1Button!!.typeface = shadowsintolight
        hint2Button!!.typeface = shadowsintolight
        hint3Button!!.typeface = shadowsintolight
        backButtonPlay!!.typeface = shadowsintolight
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        levelTextView!!.typeface = delius
        bucksTextView!!.typeface = delius
        bucksValueView!!.typeface = delius
        currentLevelTextView!!.typeface = delius
        checkButton!!.typeface = delius
        answerEditText!!.typeface = delius
        hint1Button!!.typeface = delius
        hint2Button!!.typeface = delius
        hint3Button!!.typeface = delius
        backButtonPlay!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        levelTextView!!.typeface = arial
        bucksTextView!!.typeface = arial
        bucksValueView!!.typeface = arial
        currentLevelTextView!!.typeface = arial
        checkButton!!.typeface = arial
        answerEditText!!.typeface = arial
        hint1Button!!.typeface = arial
        hint2Button!!.typeface = arial
        hint3Button!!.typeface = arial
        backButtonPlay!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        levelTextView!!.typeface = braditc
        bucksTextView!!.typeface = braditc
        bucksValueView!!.typeface = braditc
        currentLevelTextView!!.typeface = braditc
        checkButton!!.typeface = braditc
        answerEditText!!.typeface = braditc
        hint1Button!!.typeface = braditc
        hint2Button!!.typeface = braditc
        hint3Button!!.typeface = braditc
        backButtonPlay!!.typeface = braditc
    }

    private fun playCorrect() {
        Log.d("TheWordGame/LogicWord", "playCorrect() started")
        correctSound!!.start()
    }

    private fun playWrong() {
        Log.d("TheWordGame/LogicWord", "playWrong() started")
        wrongSound!!.start()
    }

    private fun increaseBucks(plusBucks: Int) {
        Log.d("TheWordGame/LogicWord", "increaseBucks(int plusBucks) started")
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("bucks", bucksSave + plusBucks)
        editor.apply()
        bucksSave = savegame.getInt("bucks", 0)
        bucksValueView!!.text = bucksSave.toString()
    }

    private fun decreaseBucks(minusBucks: Int) {
        Log.d("TheWordGame/LogicWord", "decreaseBucks(int minusBucks) started")
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("bucks", bucksSave - minusBucks)
        editor.apply()
        bucksSave = savegame.getInt("bucks", 0)
        bucksValueView!!.text = bucksSave.toString()
    }

    private fun findIds() {
        Log.d("TheWordGame/LogicWord", "findIds() started")
        answerEditText = findViewById(R.id.answerEditText)
        checkButton = findViewById(R.id.checkButton)
        hint1Button = findViewById(R.id.hint1Button)
        hint2Button = findViewById(R.id.hint2Button)
        hint3Button = findViewById(R.id.hint3Button)
        backButtonPlay = findViewById(R.id.backButtonPlay)
        levelTextView = findViewById(R.id.levelTextView)
        currentLevelTextView = findViewById(R.id.currentLevelTextView)
        bucksValueView = findViewById(R.id.bucksValueView)
        bucksTextView = findViewById(R.id.bucksTextView)
        adView = findViewById(R.id.logicWordBanner)
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/LogicWord", "setOnClickListeners() started")
        hint1Button!!.setOnClickListener(this)
        hint2Button!!.setOnClickListener(this)
        hint3Button!!.setOnClickListener(this)
        checkButton!!.setOnClickListener(this)
        backButtonPlay!!.setOnClickListener(this)
    }

    private fun wrongAnswer() {
        Log.d("TheWordGame/LogicWord", "wrongAnswer() started")
        vibrator!!.vibrate(200)
        handler!!.postDelayed({ vibrator!!.vibrate(200) }, 400)
        val toast = Toast.makeText(applicationContext, "WRONG ANSWER", Toast.LENGTH_SHORT)
        toast.show()
        playWrong()
    }

}
