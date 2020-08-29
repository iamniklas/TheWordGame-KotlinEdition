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
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import java.util.*

class MainMenuActivityK : AppCompatActivity(), View.OnClickListener {

    //UI Components
    private var theView: TextView? = null
    private var wordView: TextView? = null
    private var gameView: TextView? = null
    private var play: Button? = null
    private var credits: Button? = null
    private var options: Button? = null
    private var info: TextView? = null

    private var braditc: Typeface? = null
    private var delius: Typeface? = null
    private var arial: Typeface? = null
    private var shadowsintolighttwo: Typeface? = null

    //Intents
    private val toPlay = Intent()
    private val toOptions = Intent()
    private val toCredits = Intent()

    //MediaPlayers
    private var mediaPlayer: MediaPlayer? = null

    //Animations
    private var fadeOut: Animation? = null
    private var blinkonce: Animation? = null

    //Handlers
    private var handler: Handler? = null

    //Integer
    private var gameTheme: Int = 0
    private var gameFont: Int = 0
    private var soundsAllowed: Int = 0
    private var fS: Int = 0
    private var cB: Int = 0

    //AdRequest
    private val adRequest = AdRequest.Builder().build()

    //Dialog.Builder
    private var dialog: AlertDialog.Builder? = null

    lateinit internal var instance: MainMenuActivityK

    public var player = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TheWordGame/MainMenuAct", "onCreate(Bundle savedInstanceState) started")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fadeOut = AnimationUtils.loadAnimation(applicationContext, R.anim.fadeout)
        blinkonce = AnimationUtils.loadAnimation(applicationContext, R.anim.blinkonce)
        handler = Handler()
        dialog = AlertDialog.Builder(this)

        braditc = Typeface.createFromAsset(assets, "AmaticSC.ttf")
        delius = Typeface.createFromAsset(assets, "ArchitectsDaughter.ttf")
        shadowsintolighttwo = Typeface.createFromAsset(assets, "ComingSoon.ttf")
        arial = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")

        instance = this

        toPlay.action = Intent.ACTION_DIAL
        toPlay.setClass(applicationContext, CategorySelectorActivityK::class.java)
        toOptions.action = Intent.ACTION_DIAL
        toOptions.setClass(applicationContext, SettingsActivityK::class.java)
        toCredits.action = Intent.ACTION_DIAL
        toCredits.setClass(applicationContext, CreditActivityK::class.java)

        mediaPlayer = MediaPlayer.create(this, R.raw.click_2)

        val firstStart = getSharedPreferences("firstStart", 0)
        val settings = getSharedPreferences("settings", 0)
        fS = firstStart.getInt("firstStarted", 0)
        if (fS != 1) {
            fS = 1
            val editor = firstStart.edit()
            editor.putInt("firstStarted", 1)
            editor.apply()
            val savegame = getSharedPreferences("savegame", 0)
            cB = savegame.getInt("bucks", 0)
            val editor2 = savegame.edit()
            editor2.putInt("bucks", cB + 250)
            editor2.apply()
            val editor3 = settings.edit()
            editor3.putInt("gameTheme", 1)
            editor3.putInt("gameFont", 1)
            editor3.apply()
            translationwarningdialog()
        }

        if (settings.getInt("musicAllowed", 0) == 1) {
            try {
                if (!player.isPlaying) {
                    player.reset()
                    startPlayer()
                }
            } catch (i: IllegalStateException) {
                Log.d("ERROR", "ERROR")
                player.release()
                player = MediaPlayer.create(applicationContext, R.raw.firsthint)
                startPlayer()
            }

        }
        findIds()
        getData()
        setTheme()
        setFont()
        setOnClickListeners()
        a()
    }

    override fun onPause() {
        Log.d("TheWordGame/MainMenuAct", "onPause() started")
        super.onPause()
        if(player.isPlaying) {
            player.pause();
        }
    }

    override fun onStop() {
        Log.d("TheWordGame/MainMenuAct", "onStop() started")
        super.onStop()
    }

    override fun onResume() {
        Log.d("TheWordGame/MainMenuAct", "onResume() started")
        super.onResume()
        val settings = getSharedPreferences("settings", 0)
        if (settings.getInt("musicAllowed", 0) == 1) {
            try {
                if (!player.isPlaying) {
                    val length = player.currentPosition
                    player.seekTo(length)
                    player.start()
                }
            } catch (i: IllegalStateException) {
                Log.d("TheWordGame/MainMenuAct", "NullPointerExeption at MainMenuActivity (onResume())")
            }

        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else super.dispatchKeyEvent(event)
    }

    override fun onClick(view: View) {
        Log.d("TheWordGame/MainMenuAct", "onClick(View view) started")
        when (view.id) {

            R.id.play -> {
                val i = Intent(this, CategorySelectorActivityK::class.java)
                startActivity(i)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.options -> {
                //player.release()
                val o = Intent(this, SettingsActivityK::class.java)
                startActivity(o)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }

            R.id.creditsButton -> {
               // player.release()
                val c = Intent(this, CreditActivityK::class.java)
                startActivity(c)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun a() {
        player.setOnCompletionListener {
            player.release()
            player = MediaPlayer.create(applicationContext, R.raw.firsthint)
            player.start()
            a()
        }
    }

    public fun stopMusic() {
        player.start()
        player.stop()
    }

    private fun getData() {
        Log.d("TheWordGame/MainMenuAct", "getData() started")
        val settings = getSharedPreferences("settings", 0)
        gameTheme = settings.getInt("gameTheme", 0)
        gameFont = settings.getInt("gameFont", 0)
        soundsAllowed = settings.getInt("soundsAllowed", 0)
    }

    private fun translationwarningdialog() {
        dialog!!.setTitle(getString(R.string.main_dialog_translationwarning_title))
                .setMessage(
                        getString(R.string.main_dialog_translationwarning_content) + " " + Locale.getDefault().displayLanguage + "\n" + getString(R.string.main_dialog_translationwarning_content2) + "\n" + getString(R.string.main_dialog_translationwarning_content3)
                )
                .setCancelable(false)
                .setPositiveButton(getString(R.string.main_dialog_translationwarning_button_pos)) { dialog, which -> }
                .create()
                .show()
    }

    private fun playSound() {
        if (soundsAllowed == 1) {
            mediaPlayer!!.start()
        }
    }

    fun startPlayer() {
        player = MediaPlayer.create(applicationContext, R.raw.firsthint)
        player.start()
    }

    private fun setTheme() {
        Log.d("TheWordGame/MainMenuAct", "setTheme() started")
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
        Log.d("TheWordGame/MainMenuAct", "setTheme1() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
    }

    private fun setTheme2() {
        Log.d("TheWordGame/MainMenuAct", "setTheme2() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
    }

    private fun setTheme3() {
        Log.d("TheWordGame/MainMenuAct", "setTheme3() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
    }

    private fun setTheme4() {
        Log.d("TheWordGame/MainMenuAct", "setTheme4() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
    }

    private fun setTheme5() {
        Log.d("TheWordGame/MainMenuAct", "setTheme5() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
    }

    private fun setTheme6() {
        Log.d("TheWordGame/MainMenuAct", "setTheme6() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
    }

    private fun setTheme7() {
        Log.d("TheWordGame/MainMenuAct", "setTheme7() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme5TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme8() {
        Log.d("TheWordGame/MainMenuAct", "setTheme8() started")
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme6TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme9() {
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme7TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme10() {
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme8TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme11() {
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme9TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
    }

    private fun setTheme12() {
        theView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        wordView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        gameView!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        play!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        options!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
        info!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        play!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        options!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        credits!!.setTextColor(ContextCompat.getColor(applicationContext, R.color.theme10TextColor))
        credits!!.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.black))
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
        theView!!.typeface = shadowsintolighttwo
        wordView!!.typeface = shadowsintolighttwo
        gameView!!.typeface = shadowsintolighttwo
        play!!.typeface = shadowsintolighttwo
        credits!!.typeface = shadowsintolighttwo
        options!!.typeface = shadowsintolighttwo
        info!!.typeface = shadowsintolighttwo
    }

    private fun setFont2() {
        Log.d("TheWordGame/ThemeActivi", "setFont2() started")
        theView!!.typeface = delius
        wordView!!.typeface = delius
        gameView!!.typeface = delius
        play!!.typeface = delius
        credits!!.typeface = delius
        options!!.typeface = delius
        info!!.typeface = delius
    }

    private fun setFont3() {
        Log.d("TheWordGame/ThemeActivi", "setFont3() started")
        theView!!.typeface = arial
        wordView!!.typeface = arial
        gameView!!.typeface = arial
        play!!.typeface = arial
        credits!!.typeface = arial
        options!!.typeface = arial
        info!!.typeface = arial
    }

    private fun setFont4() {
        Log.d("TheWordGame/ThemeActivi", "setFont4() started")
        theView!!.typeface = braditc
        wordView!!.typeface = braditc
        gameView!!.typeface = braditc
        play!!.typeface = braditc
        credits!!.typeface = braditc
        options!!.typeface = braditc
        info!!.typeface = braditc
    }

    private fun findIds() {
        Log.d("TheWordGame/MainMenuAct", "findIds() started")
        theView = findViewById(R.id.viewThe)
        wordView = findViewById(R.id.wordView)
        gameView = findViewById(R.id.gameView)
        play = findViewById(R.id.play)
        options = findViewById(R.id.options)
        info = findViewById(R.id.info)
        credits = findViewById(R.id.creditsButton)
    }

    private fun setOnClickListeners() {
        Log.d("TheWordGame/MainMenuAct", "setOnClickListeners() started")
        play!!.setOnClickListener(this)
        options!!.setOnClickListener(this)
        credits!!.setOnClickListener(this)
    }

}
