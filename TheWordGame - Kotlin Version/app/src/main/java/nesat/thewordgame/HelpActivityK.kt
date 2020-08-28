package nesat.thewordgame

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
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
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import java.util.*

class HelpActivityK : AppCompatActivity(), View.OnClickListener {

    /*
        UI Widgets
     */
    private var previousButton: Button? = null
    private var nextButton: Button? = null
    private var backButton: Button? = null
    private var helpTextView: TextView? = null
    private var helpText1: TextView? = null
    private var helpText2: TextView? = null
    private var helpText3: TextView? = null
    private var helpText4: TextView? = null
    private var helpText5: TextView? = null
    private var helpText6: TextView? = null
    private var currentPage: TextView? = null
    private var statusdot: TextView? = null
    private val interstitialAd: InterstitialAd? = null

    private var shadowsintolighttwo: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var braditc: Typeface? = null

    /*
        Variables
     */
    private var position: Int = 0
    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private var stop: Int = 0
    private var soundsAllowed: Int = 0

    /*
        Intents
     */
    private val toOptions = Intent()

    /*
        Animations
     */
    private var fadeIn: Animation? = null
    private var fadeOut: Animation? = null
    private var blink: Animation? = null

    /*
        Handlers
     */
    private var setTextVerzoegern: Handler? = null

    /*
        Timer & TimerTask
     */
    private var timerTask: TimerTask? = null
    private val timer = Timer()

    private var status: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/HelpActivit", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        blink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink)
        toOptions.action = Intent.ACTION_DIAL
        toOptions.setClass(applicationContext, SettingsActivityK::class.java)

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        setTextVerzoegern = Handler()

        findIds()

        helpText1!!.startAnimation(fadeIn)
        helpText2!!.startAnimation(fadeIn)
        helpText3!!.startAnimation(fadeIn)
        helpText4!!.startAnimation(fadeIn)
        helpText5!!.startAnimation(fadeIn)
        helpText6!!.startAnimation(fadeIn)
        getData()
        setTheme()
        setFont()
        setOnClickListeners()
        setText()
        changePageAutomatic()
        statusController()
    }

    override fun onPause() {
        Log.d("TheWordGame/HelpActivit", "onPause() started")
        super.onPause()
    }

    override fun onStop() {
        Log.d("TheWordGame/HelpActivit", "onStop() started")
        super.onStop()
    }

    override fun onResume() {
        Log.d("TheWordGame/HelpActivit", "onResume() started")
        super.onResume()
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/HelpActivit", "onClick(View view) started")
        when (view.id) {

            R.id.NextButton -> {
                if (position != 5) {
                    position++
                } else {
                    position = 1
                }
                helpText1!!.startAnimation(blink)
                helpText2!!.startAnimation(blink)
                helpText3!!.startAnimation(blink)
                helpText4!!.startAnimation(blink)
                helpText5!!.startAnimation(blink)
                helpText6!!.startAnimation(blink)
                setTextVerzoegern!!.postDelayed({ setText() }, 250)
                stop = 1
            }

            R.id.PreviousButton -> {
                if (position != 1) {
                    position--
                } else {
                    position = 5
                }
                helpText1!!.startAnimation(blink)
                helpText2!!.startAnimation(blink)
                helpText3!!.startAnimation(blink)
                helpText4!!.startAnimation(blink)
                helpText5!!.startAnimation(blink)
                helpText6!!.startAnimation(blink)
                setTextVerzoegern!!.postDelayed({ setText() }, 250)
                stop = 1
            }

            R.id.BackButton -> {
                val b = Intent(this, SettingsActivityK::class.java)
                startActivity(b)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun newInterstitialAd(): InterstitialAd {
        val interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {

            }

            override fun onAdFailedToLoad(errorCode: Int) {
                startActivity(toOptions)
            }

            override fun onAdClosed() {
                startActivity(toOptions)
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

    private fun getData() {
        Log.d("TheWordGame/HelpActivit", "getData() started")
        val settings = getSharedPreferences("settings", 0)
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun setTheme() {
        Log.d("TheWordGame/HelpActivit", "setTheme() started")
        if (gameTheme == 1) {
            setTheme1()
        } else if (gameTheme == 2) {
            setTheme2()
        } else if (gameTheme == 3) {
            setTheme3()
        } else if (gameTheme == 4) {
            setTheme4()
        } else if (gameTheme == 5) {
            setTheme5()
        } else if (gameTheme == 6) {
            setTheme6()
        } else if (gameTheme == 7) {
            setTheme7()
        } else if (gameTheme == 8) {
            setTheme8()
        } else if (gameTheme == 9) {
            setTheme9()
        } else if (gameTheme == 10) {
            setTheme10()
        } else if (gameTheme == 11) {
            setTheme11()
        } else if (gameTheme == 12) {
            setTheme12()
        }
    }

    private fun changePageAutomatic() {
        Log.d("TheWordGame/HelpActivit", "changePageAutomatic() started")
        if (stop != 1) {
            timerTask = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (stop != 1) {
                            Log.d("TheWordGame/HelpActivit", "changePageAutomatic/ stop != 1")
                            if (position != 5) {
                                Log.d("TheWordGame/HelpActivit", "position != 6")
                                position++
                                helpText1!!.startAnimation(blink)
                                helpText2!!.startAnimation(blink)
                                helpText3!!.startAnimation(blink)
                                helpText4!!.startAnimation(blink)
                                helpText5!!.startAnimation(blink)
                                helpText6!!.startAnimation(blink)
                                currentPage!!.startAnimation(blink)
                            } else {
                                Log.d("TheWordGame/HelpActivit", "changePageAutomatic/ position = 6")
                                position = 1
                                helpText1!!.startAnimation(blink)
                                helpText2!!.startAnimation(blink)
                                helpText3!!.startAnimation(blink)
                                helpText4!!.startAnimation(blink)
                                helpText5!!.startAnimation(blink)
                                helpText6!!.startAnimation(blink)
                                currentPage!!.startAnimation(blink)
                                setText()
                            }
                            setTextVerzoegern!!.postDelayed({
                                setText()
                                Log.d("TheWordGame/HelpActivit", "next page")
                            }, 250)

                        }
                        status = ""
                        statusdot!!.text = status
                    }
                }
            }
            timer.scheduleAtFixedRate(timerTask!!, 0.toLong(), 10000.toLong())
        }
    }

    private fun statusController() {
        Log.d("TheWordGame/HelpActivit", "statusController() started")
        timerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (stop != 1) {
                        Log.d("TheWordGame/HelpActivit", "statusController/ stop != 1")
                        status = status!! + "⚫"
                        statusdot!!.text = status
                    } else {
                        Log.d("TheWordGame/HelpActivit", "statusController/ stop = 1")
                        timerTask!!.cancel()
                        statusdot!!.text = applicationContext.getString(R.string.help_info_stopped)
                    }
                }
            }
        }
        timer.scheduleAtFixedRate(timerTask!!, 0.toLong(), 1000.toLong())
    }

    private fun setTheme1() {
        Log.d("TheWordGame/HelpActivit", "setTheme1() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/HelpActivit", "setTheme2() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/HelpActivit", "setTheme3() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/HelpActivit", "setThem4() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/HelpActivit", "setTheme5() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/HelpActivit", "setTheme6() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/HelpActivit", "setTheme7() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/HelpActivit", "setTheme8() started")
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme9() {
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme10() {
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme11() {
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme12() {
        helpTextView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText1!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText2!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText3!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText4!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText5!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        helpText6!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        backButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        backButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        nextButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        nextButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        previousButton!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        previousButton!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        currentPage!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        statusdot!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
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
        helpTextView!!.typeface = shadowsintolighttwo
        helpText1!!.typeface = shadowsintolighttwo
        helpText2!!.typeface = shadowsintolighttwo
        helpText3!!.typeface = shadowsintolighttwo
        helpText4!!.typeface = shadowsintolighttwo
        helpText5!!.typeface = shadowsintolighttwo
        helpText6!!.typeface = shadowsintolighttwo
        nextButton!!.typeface = shadowsintolighttwo
        previousButton!!.typeface = shadowsintolighttwo
        backButton!!.typeface = shadowsintolighttwo
        currentPage!!.typeface = shadowsintolighttwo
        statusdot!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        helpTextView!!.typeface = delius
        helpText1!!.typeface = delius
        helpText2!!.typeface = delius
        helpText3!!.typeface = delius
        helpText4!!.typeface = delius
        helpText5!!.typeface = delius
        helpText6!!.typeface = delius
        nextButton!!.typeface = delius
        previousButton!!.typeface = delius
        backButton!!.typeface = delius
        currentPage!!.typeface = delius
        statusdot!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        helpTextView!!.typeface = arial
        helpText1!!.typeface = arial
        helpText2!!.typeface = arial
        helpText3!!.typeface = arial
        helpText4!!.typeface = arial
        helpText5!!.typeface = arial
        helpText6!!.typeface = arial
        nextButton!!.typeface = arial
        previousButton!!.typeface = arial
        backButton!!.typeface = arial
        currentPage!!.typeface = arial
        statusdot!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        helpTextView!!.typeface = braditc
        helpText1!!.typeface = braditc
        helpText2!!.typeface = braditc
        helpText3!!.typeface = braditc
        helpText4!!.typeface = braditc
        helpText5!!.typeface = braditc
        helpText6!!.typeface = braditc
        nextButton!!.typeface = braditc
        previousButton!!.typeface = braditc
        backButton!!.typeface = braditc
        currentPage!!.typeface = braditc
        statusdot!!.typeface = braditc
    }

    private fun findIds() {
        Log.d("TheWordGame/HelpActivit", "findIds() started")
        previousButton = findViewById(R.id.PreviousButton)
        nextButton = findViewById(R.id.NextButton)
        backButton = findViewById(R.id.BackButton)
        helpTextView = findViewById(R.id.HelpTextView)
        helpText1 = findViewById(R.id.HelpText1)
        helpText2 = findViewById(R.id.HelpText2)
        helpText3 = findViewById(R.id.HelpText3)
        helpText4 = findViewById(R.id.HelpText4)
        helpText5 = findViewById(R.id.HelpText5)
        helpText6 = findViewById(R.id.HelpText6)
        currentPage = findViewById(R.id.currentPage)
        statusdot = findViewById(R.id.statusdot)
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/HelpActivit", "setOnClickListeners() started")
        nextButton!!.setOnClickListener(this)
        previousButton!!.setOnClickListener(this)
        backButton!!.setOnClickListener(this)
    }

    private fun setText() {
        Log.d("TheWordGame/HelpActivit", "setText() started")
        if (position == 1) {
            Log.d("TheWordGame/HelpActivit", "setText/ position = 1")
            helpText1!!.setText(R.string.help_textview_page1_helpline1)
            helpText2!!.setText(R.string.help_textview_page1_helpline2)
            helpText3!!.setText(R.string.help_textview_page1_helpline3)
            helpText4!!.setText(R.string.help_textview_page1_helpline4)
            helpText5!!.setText(R.string.help_textview_page1_helpline5)
            helpText6!!.setText(R.string.help_textview_page1_helpline6)
            currentPage!!.setText(R.string.help_textview_page1)
        } else if (position == 2) {
            Log.d("TheWordGame/HelpActivit", "setText/ position = 2")
            helpText1!!.setText(R.string.help_textview_page2_helpline1)
            helpText2!!.setText(R.string.help_textview_page2_helpline2)
            helpText3!!.setText(R.string.help_textview_page2_helpline3)
            helpText4!!.setText(R.string.help_textview_page2_helpline4)
            helpText5!!.setText(R.string.help_textview_page2_helpline5)
            helpText6!!.setText(R.string.help_textview_page2_helpline6)
            currentPage!!.setText(R.string.help_textview_page2)
        } else if (position == 3) {
            Log.d("TheWordGame/HelpActivit", "setText/ position = 3")
            helpText1!!.setText(R.string.help_textview_page3_helpline1)
            helpText2!!.setText(R.string.help_textview_page3_helpline2)
            helpText3!!.setText(R.string.help_textview_page3_helpline3)
            helpText4!!.setText(R.string.help_textview_page3_helpline4)
            helpText5!!.setText(R.string.help_textview_page3_helpline5)
            helpText6!!.setText(R.string.help_textview_page3_helpline6)
            currentPage!!.setText(R.string.help_textview_page3)
        } else if (position == 4) {
            Log.d("TheWordGame/HelpActivit", "setText/ position = 4")
            helpText1!!.setText(R.string.help_textview_page4_helpline1)
            helpText2!!.setText(R.string.help_textview_page4_helpline2)
            helpText3!!.setText(R.string.help_textview_page4_helpline3)
            helpText4!!.setText(R.string.help_textview_page4_helpline4)
            helpText5!!.setText(R.string.help_textview_page4_helpline5)
            helpText6!!.setText(R.string.help_textview_page4_helpline6)
            currentPage!!.setText(R.string.help_textview_page4)
        } else if (position == 5) {
            Log.d("TheWordGame/HelpActivit", "setText/ position = 5")
            helpText1!!.setText(R.string.help_textview_page5_helpline1)
            helpText2!!.setText(R.string.help_textview_page5_helpline2)
            helpText3!!.setText(R.string.help_textview_page5_helpline3)
            helpText4!!.setText(R.string.help_textview_page5_helpline4)
            helpText5!!.setText(R.string.help_textview_page5_helpline5)
            helpText6!!.setText(R.string.help_textview_page5_helpline6)
            currentPage!!.setText(R.string.help_textview_page5)
        }
    }
}