package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

class CreditActivityK : AppCompatActivity(), View.OnClickListener {

    private var creditsHeadline: TextView? = null
    private var programmingheadline: TextView? = null
    private var programmingniklasenglmeier: TextView? = null
    private var programmingassistanceheadline: TextView? = null
    private var programmingassistancenicoschott: TextView? = null
    private var programmingassistancebenediktschmitt: TextView? = null
    private var levelassistanceheadline: TextView? = null
    private var levelassistancekilianbetzl: TextView? = null
    private var levelassistancejonathandietel: TextView? = null
    private var translationsheadline: TextView? = null
    private var translationssakuramurayama: TextView? = null
    private var musicheadline: TextView? = null
    private var musicxrenon: TextView? = null
    private var musicxrenon1: TextView? = null
    private var musicxrenon2: TextView? = null
    private var back: Button? = null
    private var adView: AdView? = null
    private var interstitialAd: InterstitialAd? = null

    private var braditc: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var shadowsintolighttwo: Typeface? = null

    private var dialog: AlertDialog.Builder? = null

    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null

    private var handler: Handler? = null

    private val goBack = Intent()

    private val adRequest = AdRequest.Builder().build()

    private var setTheme: Int = 0
    private var gameFont: Int = 0
    private var musicAllowed: Int = 0
    private var soundsAllowed: Int = 0
    private var creditMusic: MediaPlayer? = null
    private var click: MediaPlayer? = null


    private val title_niklasenglmeier = "Niklas Englmeier"
    private val message_niklasenglmeier = "Hi, my name is Niklas Englmeier. \n" +
            "I am the main programmer of TheWordGame, \n" +
            "Currently I am a 18 years old student at a school \n" +
            "in Bavarian, Germany and I'm in twelfth grade."

    private val title_nicoschott = "Nico Schott"
    private val message_nicoschott = ""

    private val title_benediktschmitt = "Benedikt Schmitt"
    private val message_benediktschmitt = ""

    private val title_kilianbetzl = "Kilian Betzl"
    private val message_kilianbetzl = ""

    private val title_xrenon = "Xrenon the MusicMage (Thomas Fritz)"
    private val message_xrenon = "Thomas Fritz a.k.a Xrenon TheMusicMage is one of my friends I know from school. \n" +
            "Thomas is producing music with FruitLoops and he allowed me to use some " +
            "of his songs for my apps. I am really thankful for that. Thanks Thomas :) ."


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/CreditActiv", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        interstitialAd = newInterstitialAd()
        loadInterstitial()

        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        handler = Handler()
        dialog = AlertDialog.Builder(this)
        creditMusic = MediaPlayer.create(applicationContext, R.raw.settingsandupdate)
        click = MediaPlayer.create(applicationContext, R.raw.click_2)
        val settings = getSharedPreferences("settings", 0)
        musicAllowed = settings.getInt("musicAllowed", 0)
        if (musicAllowed == 1) {
            creditMusic!!.isLooping = true
            creditMusic!!.start()
        }

        findIds()
        getData()
        setTheme()
        setFont()
        setOnClickListeners()

        adView!!.loadAd(adRequest)
    }

    override fun onStop() {
        Log.d("TheWordGame/CreditActiv", "onStop() started")
        super.onStop()
        creditMusic!!.pause()
    }

    override fun onPause() {
        Log.d("TheWordGame/CreditActiv", "onPause() started")
        super.onPause()
        creditMusic!!.pause()
    }

    override fun onResume() {
        Log.d("TheWordGame/CreditActiv", "onResume() started")
        super.onResume()
        val settings = getSharedPreferences("musicAllowed", 0)
        musicAllowed = settings.getInt("musicAllowed", 0)
        if (musicAllowed == 1) {
            creditMusic!!.start()
        }
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/CreditActiv", "onClick(View view) started")
        when (view.id) {

            R.id.credits_programming_niklasenglmeier -> dialogsetter(title_niklasenglmeier, message_niklasenglmeier)

            R.id.credits_programmingassistance_nicoschott -> dialogsetter(title_nicoschott, message_nicoschott)

            R.id.credits_programmingassistance_benediktschmitt -> dialogsetter(title_benediktschmitt, message_benediktschmitt)

            R.id.credits_levelassistance_kilianbetzl -> dialogsetter(title_kilianbetzl, message_kilianbetzl)

            R.id.credits_levelassistance_parents_teachers -> {
            }

            R.id.credits_music_xrenon -> dialogsetter(title_xrenon, message_xrenon)

            R.id.credits_music_xrenon_2 -> dialogsetter(title_xrenon, message_xrenon)

            R.id.credits_music_xrenon_3 -> dialogsetter(title_xrenon, message_xrenon)

            R.id.credits_button_back -> {
                playSound()
                val b = Intent(this, MainMenuActivityK::class.java)
                startActivity(b)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            click!!.start()
        }
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {
                handler!!.postDelayed({
                    //startActivity(goBack);
                }, 200)
            }

            override fun onAdClosed() {
                startActivity(goBack)
            }
        }
        return interstitialAd
    }

    private fun showInterstitial() {
        if (interstitialAd != null && interstitialAd!!.isLoaded) {
            interstitialAd!!.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build()
        interstitialAd!!.loadAd(adRequest)
    }

    private fun getData() {
        Log.d("TheWordGame/CreditActiv", "getData() started")
        val settings = getSharedPreferences("settings", 0)
        setTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun setTheme() {
        Log.d("TheWordGame/CreditActiv", "setTheme() started")
        if (setTheme == 1) {
            setTheme1()
        } else if (setTheme == 2) {
            setTheme2()
        } else if (setTheme == 3) {
            setTheme3()
        } else if (setTheme == 4) {
            setTheme4()
        } else if (setTheme == 5) {
            setTheme5()
        } else if (setTheme == 6) {
            setTheme6()
        } else if (setTheme == 7) {
            setTheme7()
        } else if (setTheme == 8) {
            setTheme8()
        } else if (setTheme == 9) {
            setTheme9()
        } else if (setTheme == 10) {
            setTheme10()
        } else if (setTheme == 11) {
            setTheme11()
        } else if (setTheme == 12) {
            setTheme12()
        }
    }

    private fun setTheme1() {
        Log.d("TheWordGame/CreditActiv", "setTheme1() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))

    }

    private fun setTheme2() {
        Log.d("TheWordGame/CreditActiv", "setTheme2() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/CreditActiv", "setTheme3() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/CreditActiv", "setTheme4() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/CreditActiv", "setTheme5() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/CreditActiv", "setTheme6() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/CreditActiv", "setTheme7() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/CreditActiv", "setTheme8() started")
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        creditsHeadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingniklasenglmeier!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistancebenediktschmitt!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        programmingassistancenicoschott!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistanceheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistancekilianbetzl!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        levelassistancejonathandietel!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        translationsheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        translationssakuramurayama!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicheadline!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        musicxrenon2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        back!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        back!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun findIds() {
        Log.d("TheWordGame/CreditActiv", "findIds() started")
        creditsHeadline = findViewById(R.id.credits_headline)
        programmingheadline = findViewById(R.id.credits_programming_headline)
        programmingniklasenglmeier = findViewById(R.id.credits_programming_niklasenglmeier)
        programmingassistanceheadline = findViewById(R.id.credits_programmingassistance_headline)
        programmingassistancebenediktschmitt = findViewById(R.id.credits_programmingassistance_benediktschmitt)
        programmingassistancenicoschott = findViewById(R.id.credits_programmingassistance_nicoschott)
        levelassistanceheadline = findViewById(R.id.credits_levelassistance_headline)
        levelassistancekilianbetzl = findViewById(R.id.credits_levelassistance_kilianbetzl)
        levelassistancejonathandietel = findViewById(R.id.credits_levelassistance_jonathandietel)
        translationsheadline = findViewById(R.id.credits_translation_headline)
        translationssakuramurayama = findViewById(R.id.credits_translation_sakuramurayama)
        musicheadline = findViewById(R.id.credits_music_headline)
        musicxrenon = findViewById(R.id.credits_music_xrenon)
        musicxrenon1 = findViewById(R.id.credits_music_xrenon_2)
        musicxrenon2 = findViewById(R.id.credits_music_xrenon_3)
        back = findViewById(R.id.credits_button_back)
        adView = findViewById(R.id.creditsBanner)
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
        creditsHeadline!!.typeface = shadowsintolighttwo
        programmingheadline!!.typeface = shadowsintolighttwo
        programmingniklasenglmeier!!.typeface = shadowsintolighttwo
        programmingassistanceheadline!!.typeface = shadowsintolighttwo
        programmingassistancebenediktschmitt!!.typeface = shadowsintolighttwo
        programmingassistancenicoschott!!.typeface = shadowsintolighttwo
        levelassistanceheadline!!.typeface = shadowsintolighttwo
        levelassistancekilianbetzl!!.typeface = shadowsintolighttwo
        levelassistancejonathandietel!!.typeface = shadowsintolighttwo
        translationsheadline!!.typeface = shadowsintolighttwo
        translationssakuramurayama!!.typeface = shadowsintolighttwo
        musicheadline!!.typeface = shadowsintolighttwo
        musicxrenon!!.typeface = shadowsintolighttwo
        musicxrenon1!!.typeface = shadowsintolighttwo
        musicxrenon2!!.typeface = shadowsintolighttwo
        back!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        creditsHeadline!!.typeface = delius
        programmingheadline!!.typeface = delius
        programmingniklasenglmeier!!.typeface = delius
        programmingassistanceheadline!!.typeface = delius
        programmingassistancebenediktschmitt!!.typeface = delius
        programmingassistancenicoschott!!.typeface = delius
        levelassistanceheadline!!.typeface = delius
        levelassistancekilianbetzl!!.typeface = delius
        levelassistancejonathandietel!!.typeface = delius
        translationsheadline!!.typeface = delius
        translationssakuramurayama!!.typeface = delius
        musicheadline!!.typeface = delius
        musicxrenon!!.typeface = delius
        musicxrenon1!!.typeface = delius
        musicxrenon2!!.typeface = delius
        back!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        creditsHeadline!!.typeface = arial
        programmingheadline!!.typeface = arial
        programmingniklasenglmeier!!.typeface = arial
        programmingassistanceheadline!!.typeface = arial
        programmingassistancebenediktschmitt!!.typeface = arial
        programmingassistancenicoschott!!.typeface = arial
        levelassistanceheadline!!.typeface = arial
        levelassistancekilianbetzl!!.typeface = arial
        levelassistancejonathandietel!!.typeface = arial
        translationsheadline!!.typeface = arial
        translationssakuramurayama!!.typeface = arial
        musicheadline!!.typeface = arial
        musicxrenon!!.typeface = arial
        musicxrenon1!!.typeface = arial
        musicxrenon2!!.typeface = arial
        back!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        creditsHeadline!!.typeface = braditc
        programmingheadline!!.typeface = braditc
        programmingniklasenglmeier!!.typeface = braditc
        programmingassistanceheadline!!.typeface = braditc
        programmingassistancebenediktschmitt!!.typeface = braditc
        programmingassistancenicoschott!!.typeface = braditc
        levelassistanceheadline!!.typeface = braditc
        levelassistancekilianbetzl!!.typeface = braditc
        levelassistancejonathandietel!!.typeface = braditc
        translationsheadline!!.typeface = braditc
        translationssakuramurayama!!.typeface = braditc
        musicheadline!!.typeface = braditc
        musicxrenon!!.typeface = braditc
        musicxrenon1!!.typeface = braditc
        musicxrenon2!!.typeface = braditc
        back!!.typeface = braditc
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/CreditActiv", "setOnClickListeners() started")
        programmingniklasenglmeier!!.setOnClickListener(this)
        programmingassistancebenediktschmitt!!.setOnClickListener(this)
        programmingassistancenicoschott!!.setOnClickListener(this)
        levelassistancekilianbetzl!!.setOnClickListener(this)
        translationssakuramurayama!!.setOnClickListener(this)
        musicxrenon!!.setOnClickListener(this)
        musicxrenon1!!.setOnClickListener(this)
        musicxrenon2!!.setOnClickListener(this)
        back!!.setOnClickListener(this)
    }

    private fun dialogsetter(title: String, message: String) {
        Log.d("TheWordGame/CreditActiv", "dialogsetter() started")
        dialog!!.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { _, _ -> }
                .create().show()
    }

}