package nesat.thewordgame

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardedVideoAd

class TwoPicsOneWordActivityK : AppCompatActivity(), View.OnClickListener {

    //Intents
    private val toCategory = Intent()

    //Widgets
    private var twoPicsOneWordLevel: TextView? = null
    private var bucksText: TextView? = null
    private var bucksValueView: TextView? = null
    private var twoPicsOneWordCheckButton: Button? = null
    private var twoPicsOneWordHint1: Button? = null
    private var twoPicsOneWordHint2: Button? = null
    private var twoPicsOneWordHint3: Button? = null
    private var twoPicsOneWordBackButton: Button? = null
    private var twoPicsOneWordAnswerEditText: EditText? = null
    private var leftPicture: ImageView? = null
    private var rightPicture: ImageView? = null
    private var adView: AdView? = null
    private val interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null

    //Animations
    private var fadeOut: Animation? = null
    private var fadeIn: Animation? = null

    //AlertDialog.Builder
    private var builder: AlertDialog.Builder? = null

    //MediaPlayers
    private var twoPicsOneWordMusic: MediaPlayer? = null
    private var mediaPlayer: MediaPlayer? = null

    //Handler
    private var handler: Handler? = null

    //Integers
    private var level_save: Int = 0
    private var musicAllowed: Int = 0
    private var level: Int = 0
    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private val price_hint1 = 10
    private val price_hint2 = 25
    private val price_hint3 = 100
    private var emptyArrayPos: Int = 0
    private var adCounter: Int = 0
    private var bucksSave: Int = 0
    private var gameModeStartCounter: Int = 0
    private var dontrememberagain: Int = 0
    private var soundsAllowed: Int = 0

    //Typeface
    private var shadowsintolighttwo: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var braditc: Typeface? = null

    //Arrays
    private val array_answer = arrayOfNulls<String>(50)
    private val array_hint1 = arrayOfNulls<String>(50)
    private val array_hint2 = arrayOfNulls<String>(50)
    private val array_hint3 = arrayOfNulls<String>(50)
    private val picture_left = arrayOfNulls<Drawable>(50)
    private val picture_right = arrayOfNulls<Drawable>(50)

    //AdRequest
    private val adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/2Pics1Word", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twopicsoneword)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)

        toCategory.action = Intent.ACTION_CALL
        toCategory.setClass(applicationContext, CategorySelectorActivityK::class.java)

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        twoPicsOneWordMusic = MediaPlayer.create(this, R.raw.gamemode)
        twoPicsOneWordMusic!!.isLooping = true

        val settings = getSharedPreferences("settings", 0)
        musicAllowed = settings.getInt("musicAllowed", 0)
        if (musicAllowed == 1) {
            twoPicsOneWordMusic!!.start()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.click_2)

        handler = Handler()

        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)

        builder = AlertDialog.Builder(this)

        findIds()
        getData()
        fill_arrays()

        val savegame = getSharedPreferences("savegame", 0)
        level_save = savegame.getInt("level_2pics1word", 0)
        if (level_save == 0) {
            level++
            writeData()
            setPictures_v2()
        }

        setTheme()
        setFont()
        setOnClickListener()
        setPictures_v2()
        findEmptyPosition()
        adView!!.loadAd(adRequest)
        //interstitialAd = newInterstitialAd();
        //loadInterstitial();
        //increaseStartCounter();
        startCounter()

        Log.d("TheWordGame", "**********")
        Log.d("TheWordGame", "LEVEL = $level")
        Log.d("TheWordGame", "**********")
    }

    override fun onPause() {
        Log.d("TheWordGame/2Pics1Word", "onPause() started")
        super.onPause()
        twoPicsOneWordMusic!!.pause()
    }

    override fun onStop() {
        Log.d("TheWordGame/2Pics1Word", "onStop() started")
        super.onStop()
        twoPicsOneWordMusic!!.pause()
    }

    override fun onResume() {
        Log.d("TheWordGame/2Pics1Word", "onResume() started")
        super.onResume()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/2Pics1Word", "onClick(View view) started")
        when (view.id) {

            R.id.twoPicsOneWordCheckButton -> {
                playSound()
                checkAnswer_v2()
                adCounter++
                Log.d("TheWordGame/LogicWord", "adCounter: $adCounter")
            }

            R.id.twoPicsOneWordHint1 -> {
                playSound()
                if (bucksSave >= price_hint1) {
                    bucksSave = bucksSave - price_hint1
                    writeData()
                    showHint1_v2()
                } else {
                    /*
                    builder.setTitle("Oh no!")
                            .setMessage(
                                    "You don't have enough bucks." +
                                            "\n" +
                                            "Do you want to watch an ad to get 10 bucks?"
                            )
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loadRewardedVideoAd();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //rewardedVideoAd.show();
                                        }
                                    }, 250);
                                }
                            })
                            .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create().show();
                            */

                    Toast.makeText(applicationContext, R.string.logicword_hint_purchase_notpossible, Toast.LENGTH_SHORT).show()

                }
            }

            R.id.twoPicsOneWordHint2 -> {
                playSound()
                if (bucksSave >= price_hint2) {
                    bucksSave = bucksSave - price_hint2
                    writeData()
                    showHint2_v2()
                } else {
                    /*
                    builder.setTitle("Oh no!")
                            .setMessage(
                                    "You don't have enough bucks." +
                                            "\n" +
                                            "Do you want to watch an ad to get 10 bucks?"
                            )
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loadRewardedVideoAd();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            rewardedVideoAd.show();
                                        }
                                    }, 250);
                                }
                            })
                            .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create().show();
                            */
                    Toast.makeText(applicationContext, R.string.logicword_hint_purchase_notpossible, Toast.LENGTH_SHORT).show()
                }
            }

            R.id.twoPicsOneWordHint3 -> {
                playSound()
                if (bucksSave >= price_hint3) {
                    bucksSave = bucksSave - price_hint3
                    bucksValueView!!.text = bucksSave.toString()
                    Toast.makeText(applicationContext, getString(R.string.logicword_hint3_toast), Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, getString(R.string.logicword_hint3_toast_answer) + " " + array_answer[level], Toast.LENGTH_SHORT).show()
                    level++
                    twoPicsOneWordLevel!!.text = getString(R.string.logicword_textview_headline) + " " + level
                    writeData()
                } else {
                    Toast.makeText(applicationContext, R.string.logicword_hint_purchase_notpossible, Toast.LENGTH_SHORT).show()
                }
            }

            R.id.BackButton -> {
                val settings = getSharedPreferences("settings", 0)
                playSound()
                twoPicsOneWordMusic!!.pause()
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                startActivity(toCategory)
            }
        }/*
                if(adCounter >= 10){
                    showInterstitial();
                    adCounter = 0;
                    Log.d("TheWordGame/LogicWord", "adCounter: " + adCounter);
                }
                */
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
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

    private fun loadRewardedVideoAd() {
        rewardedVideoAd!!.loadAd("ca-app-pub-8865326875373213/5172661907", adRequest)
    }

    private fun fill_arrays() {
        Log.d("TheWordGame/2Pics1Word", "fill_arrays() started")
        fill_array_picture_left()
        fill_array_picture_right()
        fill_array_answer()
        fill_array_hint1()
        fill_array_hint2()
        fill_array_hint3()
    }

    private fun fill_array_picture_left() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_left() started")
        picture_left[1] = ResourcesCompat.getDrawable(resources, R.drawable.cams, null)
        picture_left[2] = ResourcesCompat.getDrawable(resources, R.drawable.flash, null)
        picture_left[3] = ResourcesCompat.getDrawable(resources, R.drawable.illperson, null)
        picture_left[4] = ResourcesCompat.getDrawable(resources, R.drawable.death, null)
        picture_left[5] = ResourcesCompat.getDrawable(resources, R.drawable.art, null)
        picture_left[6] = ResourcesCompat.getDrawable(resources, R.drawable.key, null)
        picture_left[7] = ResourcesCompat.getDrawable(resources, R.drawable.bat, null)
        picture_left[8] = ResourcesCompat.getDrawable(resources, R.drawable.black, null)
        picture_left[9] = ResourcesCompat.getDrawable(resources, R.drawable.car, null)
        picture_left[10] = ResourcesCompat.getDrawable(resources, R.drawable.leftsideempty, null)
    }

    private fun fill_array_picture_right() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_right() started")
        picture_right[1] = ResourcesCompat.getDrawable(resources, R.drawable.dogs, null)
        picture_right[2] = ResourcesCompat.getDrawable(resources, R.drawable.bang, null)
        picture_right[3] = ResourcesCompat.getDrawable(resources, R.drawable.pc, null)
        picture_right[4] = ResourcesCompat.getDrawable(resources, R.drawable.metall, null)
        picture_right[5] = ResourcesCompat.getDrawable(resources, R.drawable.work, null)
        picture_right[6] = ResourcesCompat.getDrawable(resources, R.drawable.board, null)
        picture_right[7] = ResourcesCompat.getDrawable(resources, R.drawable.man, null)
        picture_right[8] = ResourcesCompat.getDrawable(resources, R.drawable.surfboard, null)
        picture_right[9] = ResourcesCompat.getDrawable(resources, R.drawable.door, null)
        picture_right[10] = ResourcesCompat.getDrawable(resources, R.drawable.rightsideempty, null)
    }

    private fun fill_array_answer() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_answer() started")
        array_answer[1] = getString(R.string.twopicsoneword_level_answer_level_1)
        array_answer[2] = getString(R.string.twopicsoneword_level_answer_level_2)
        array_answer[3] = getString(R.string.twopicsoneword_level_answer_level_3)
        array_answer[4] = getString(R.string.twopicsoneword_level_answer_level_4)
        array_answer[5] = getString(R.string.twopicsoneword_level_answer_level_5)
        array_answer[6] = getString(R.string.twopicsoneword_level_answer_level_6)
        array_answer[7] = getString(R.string.twopicsoneword_level_answer_level_7)
        array_answer[8] = getString(R.string.twopicsoneword_level_answer_level_8)
        array_answer[9] = getString(R.string.twopicsoneword_level_answer_level_9)
        array_answer[10] = getString(R.string.twopicsoneword_level_answer_level_10)
    }

    private fun fill_array_hint1() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_hint1() started")
        array_hint1[1] = getString(R.string.twopicsoneword_level_answer_level_1)[0].toString()
        array_hint1[2] = getString(R.string.twopicsoneword_level_answer_level_2)[0].toString()
        array_hint1[3] = getString(R.string.twopicsoneword_level_answer_level_3)[0].toString()
        array_hint1[4] = getString(R.string.twopicsoneword_level_answer_level_4)[0].toString()
        array_hint1[5] = getString(R.string.twopicsoneword_level_answer_level_5)[0].toString()
        array_hint1[6] = getString(R.string.twopicsoneword_level_answer_level_6)[0].toString()
        array_hint1[7] = getString(R.string.twopicsoneword_level_answer_level_7)[0].toString()
        array_hint1[8] = getString(R.string.twopicsoneword_level_answer_level_8)[0].toString()
        array_hint1[9] = getString(R.string.twopicsoneword_level_answer_level_9)[0].toString()
    }

    private fun fill_array_hint2() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_hint2() started")
        array_hint2[1] = getString(R.string.twopicsoneword_hint2_level_1)
        array_hint2[2] = getString(R.string.twopicsoneword_hint2_level_2)
        array_hint2[3] = getString(R.string.twopicsoneword_hint2_level_3)
        array_hint2[4] = getString(R.string.twopicsoneword_hint2_level_4)
        array_hint2[5] = getString(R.string.twopicsoneword_hint2_level_5)
        array_hint2[6] = getString(R.string.twopicsoneword_hint2_level_6)
        array_hint2[7] = getString(R.string.twopicsoneword_hint2_level_7)
        array_hint2[8] = getString(R.string.twopicsoneword_hint2_level_8)
        array_hint2[9] = getString(R.string.twopicsoneword_hint2_level_9)
        array_hint2[10] = getString(R.string.twopicsoneword_hint2_level_10)
    }

    private fun fill_array_hint3() {
        Log.d("TheWordGame/2Pics1Word", "fill_array_hint3() started")
        array_hint3[1] = array_answer[1]
        array_hint3[2] = array_answer[2]
        array_hint3[3] = array_answer[3]
        array_hint3[4] = array_answer[4]
        array_hint3[5] = array_answer[5]
        array_hint3[6] = array_answer[6]
        array_hint3[7] = array_answer[7]
        array_hint3[8] = array_answer[8]
        array_hint3[9] = array_answer[9]
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    private fun findEmptyPosition() {
        Log.d("TheWordGame/2Pics1Word", "findEmptyPosition() started")
        emptyArrayPos = 0
        for (i in 1..picture_left.size) {
            if (picture_left[i] != null) {
                Log.d("TheWordGame/2Pics1Word", "$i : not null")
            } else {
                Log.d("TheWordGame/2Pics1Word", "null")
                emptyArrayPos = i
                break
            }
        }
        Log.d("TheWordGame/2Pics1Word", "Empty position: $emptyArrayPos")
    }

    private fun findIds() {
        Log.d("TheWordGame/2Pics1Word", "findIds() started")
        twoPicsOneWordLevel = findViewById(R.id.twoPicsOneWordLevel)
        bucksText = findViewById(R.id.twopicsonewordbucksTextView)
        bucksValueView = findViewById(R.id.twopicsonewordbucksValueView)
        twoPicsOneWordCheckButton = findViewById(R.id.twoPicsOneWordCheckButton)
        twoPicsOneWordHint1 = findViewById(R.id.twoPicsOneWordHint1)
        twoPicsOneWordHint2 = findViewById(R.id.twoPicsOneWordHint2)
        twoPicsOneWordHint3 = findViewById(R.id.twoPicsOneWordHint3)
        twoPicsOneWordBackButton = findViewById(R.id.BackButton)
        twoPicsOneWordAnswerEditText = findViewById(R.id.twoPicsOneWordAnswerEditText)
        leftPicture = findViewById(R.id.leftPic)
        rightPicture = findViewById(R.id.rightPic)
        adView = findViewById(R.id.twopicsonewordBanner)
    }

    private fun getData() {
        Log.d("TheWordGame/2Pics1Word", "getData() started")
        val savegame = getSharedPreferences("savegame", 0)
        val settings = getSharedPreferences("settings", 0)
        level = savegame.getInt("level_2pics1word", 0)
        bucksSave = savegame.getInt("bucks", 0)
        dontrememberagain = savegame.getInt("dontrememberagain", 0)
        gameModeStartCounter = savegame.getInt("startCounter", 0)
        bucksValueView!!.text = bucksSave.toString()
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun writeData() {
        Log.d("TheWordGame/2Pics1Word", "writeData() started")
        val savegame = getSharedPreferences("savegame", 0)
        val editor = savegame.edit()
        editor.putInt("level_2pics1word", level)
        editor.putInt("bucks", bucksSave)
        editor.apply()
    }

    private fun setTheme() {
        Log.d("TheWordGame/2Pics1Word", "setTheme() started")
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
        Log.d("TheWordGame/2Pics1Word", "setTheme1() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/2Pics1Word", "setTheme2() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/2Pics1Word", "setTheme3() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/2Pics1Word", "setTheme4() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/2Pics1Word", "setTheme5() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/2Pics1Word", "setTheme6() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/2Pics1Word", "setTheme7() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/2Pics1Word", "setTheme8() started")
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme9() {
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme10() {
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme11() {
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme12() {
        twoPicsOneWordLevel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        bucksValueView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordAnswerEditText!!.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordAnswerEditText!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordCheckButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordCheckButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint1!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint2!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordHint3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordHint3!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        twoPicsOneWordBackButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        twoPicsOneWordBackButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
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
        twoPicsOneWordLevel!!.typeface = shadowsintolighttwo
        twoPicsOneWordAnswerEditText!!.typeface = shadowsintolighttwo
        twoPicsOneWordCheckButton!!.typeface = shadowsintolighttwo
        twoPicsOneWordBackButton!!.typeface = shadowsintolighttwo
        twoPicsOneWordHint1!!.typeface = shadowsintolighttwo
        twoPicsOneWordHint2!!.typeface = shadowsintolighttwo
        twoPicsOneWordHint3!!.typeface = shadowsintolighttwo
        bucksText!!.typeface = shadowsintolighttwo
        bucksValueView!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        twoPicsOneWordLevel!!.typeface = delius
        twoPicsOneWordAnswerEditText!!.typeface = delius
        twoPicsOneWordCheckButton!!.typeface = delius
        twoPicsOneWordBackButton!!.typeface = delius
        twoPicsOneWordHint1!!.typeface = delius
        twoPicsOneWordHint2!!.typeface = delius
        twoPicsOneWordHint3!!.typeface = delius
        bucksText!!.typeface = delius
        bucksValueView!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        twoPicsOneWordLevel!!.typeface = arial
        twoPicsOneWordAnswerEditText!!.typeface = arial
        twoPicsOneWordCheckButton!!.typeface = arial
        twoPicsOneWordBackButton!!.typeface = arial
        twoPicsOneWordHint1!!.typeface = arial
        twoPicsOneWordHint2!!.typeface = arial
        twoPicsOneWordHint3!!.typeface = arial
        bucksText!!.typeface = arial
        bucksValueView!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        twoPicsOneWordLevel!!.typeface = braditc
        twoPicsOneWordAnswerEditText!!.typeface = braditc
        twoPicsOneWordCheckButton!!.typeface = braditc
        twoPicsOneWordBackButton!!.typeface = braditc
        twoPicsOneWordHint1!!.typeface = braditc
        twoPicsOneWordHint2!!.typeface = braditc
        twoPicsOneWordHint3!!.typeface = braditc
        bucksText!!.typeface = braditc
        bucksValueView!!.typeface = braditc
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
                        }
                        .setNegativeButton(getString(R.string.div_ratedialog_negativebutton)) { dialogInterface, i -> }
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

    private fun setPictures_v2() {
        Log.d("TheWordGame/2Pics1Word", "setPictures_v2() started")
        leftPicture!!.setImageDrawable(picture_left[level])
        rightPicture!!.setImageDrawable(picture_right[level])
        twoPicsOneWordLevel!!.text = applicationContext.getString(R.string.logicword_levelview_level) + " " + level
    }

    private fun setOnClickListener() {
        Log.d("TheWordGame/2Pics1Word", "setOnClickListeners() started")
        twoPicsOneWordCheckButton!!.setOnClickListener(this)
        twoPicsOneWordBackButton!!.setOnClickListener(this)
        twoPicsOneWordHint1!!.setOnClickListener(this)
        twoPicsOneWordHint2!!.setOnClickListener(this)
        twoPicsOneWordHint3!!.setOnClickListener(this)
    }

    private fun wrongAnswer() {
        Log.d("TheWordGame/2Pics1Word", "wrongAnswer() started")
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        try {
            v.vibrate(200)
        } catch (n: NullPointerException) {
            Log.d("TheWordGame/TwoPicsOneW", "NullPointerExeption wrongAnswer()")
        }

        val handler = Handler()
        handler.postDelayed({
            try {
                v.vibrate(200)
            } catch (n: NullPointerException) {
                Log.d("TheWordGame/TwoPicsOneW", "NullPointerExeption wrongAnswer()")
            }
        }, 400)
        Toast.makeText(applicationContext, "WRONG ANSWER", Toast.LENGTH_SHORT).show()
    }

    private fun checkAnswer_v2() {
        Log.d("TheWordGame/2Pics1Word", "checkAnswer_v2() started")
        if (level >= emptyArrayPos) {
            builder!!.setCancelable(true)
                    .setTitle("No more levels")
                    .setMessage("Please wait for an update")
                    .create().show()
            level = emptyArrayPos
            writeData()
        }
        if (twoPicsOneWordAnswerEditText!!.editableText.toString().equals(array_answer[level], ignoreCase = true) || twoPicsOneWordAnswerEditText!!.editableText.toString().contains(array_answer[level].toString())) {
            twoPicsOneWordAnswerEditText!!.setText("")
            level++
            writeData()
            setPictures_v2()
        } else {
            wrongAnswer()
            twoPicsOneWordAnswerEditText!!.setText("")
        }
    }

    private fun showHint1_v2() {
        Log.d("TheWordGame/2Pics1Word", "showHint1_v2() started")
        builder!!.setTitle("HINT 1")
                .setMessage(array_hint1[level])
                .create().show()
    }

    private fun showHint2_v2() {
        Log.d("TheWordGame/2Pics1Word", "showHint2_v2() started")
        builder!!.setTitle("HINT 2")
                .setMessage(array_hint2[level])
                .create().show()
    }

    private fun showHint3_v2() {
        Log.d("TheWordGame/2Pics1Word", "showHint3_v2() started")
        builder!!.setTitle("HINT 3")
                .setMessage(array_hint3[level])
                .create().show()
    }
}