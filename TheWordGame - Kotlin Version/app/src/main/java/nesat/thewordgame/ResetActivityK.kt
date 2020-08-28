package nesat.thewordgame

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ResetActivityK : AppCompatActivity(), View.OnClickListener {

    private var reset_headline: TextView? = null
    private var reset_logicword: TextView? = null
    private var reset_twopicsoneword: TextView? = null
    private var reset_justoneword: TextView? = null
    private var reset_bucks: TextView? = null
    private var reset_confirm: Button? = null
    private var reset_back: Button? = null

    private var savegame: SharedPreferences? = null

    private var level_logicword: Int = 0
    private var level_twopicsoneword: Int = 0
    private var level_justoneword: Int = 0
    private var level_bucks: Int = 0

    private val toSettings = Intent()

    private var a: Int = 0
    private var b: Int = 0
    private var c: Int = 0
    private var fS: Int = 0
    private var h1ABFormat: String? = null
    private var h2ABFormat: String? = null
    private var h3ABFormat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_reset)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        toSettings.action = Intent.ACTION_CALL
        toSettings.setClass(applicationContext, SettingsActivityK::class.java)

        findIds()
        setOnClickListener()
        getData()
        setResetText()

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.reset_confirm -> {
                reset()
                setResetText()
            }


            R.id.reset_back -> {
                startActivity(toSettings)
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            }
        }
    }

    private fun findIds() {
        reset_headline = findViewById(R.id.reset_headline)
        reset_logicword = findViewById(R.id.reset_logicword)
        reset_twopicsoneword = findViewById(R.id.reset_twopicsoneword)
        reset_justoneword = findViewById(R.id.reset_justoneword)
        reset_bucks = findViewById(R.id.reset_bucks)
        reset_confirm = findViewById(R.id.reset_confirm)
        reset_back = findViewById(R.id.reset_back)
    }

    private fun setOnClickListener() {
        reset_confirm!!.setOnClickListener(this)
        reset_back!!.setOnClickListener(this)
    }

    private fun getData() {
        savegame = getSharedPreferences("savegame", 0)
        level_logicword = savegame!!.getInt("level_logicword", 0)
        level_twopicsoneword = savegame!!.getInt("level_twopicsoneword", 0)
        level_justoneword = savegame!!.getInt("level_justoneword", 0)
        level_bucks = savegame!!.getInt("bucks", 0)
    }

    private fun setResetText() {
        reset_logicword!!.text = getString(R.string.reset_logicword_1)
        reset_twopicsoneword!!.text = getString(R.string.reset_twopicsoneword_1)
        reset_justoneword!!.text = getString(R.string.reset_justoneword_1)
        reset_bucks!!.text = getString(R.string.reset_bucks_1)
        reset_logicword!!.text = reset_logicword!!.text.toString() + " " + level_logicword.toString()
        reset_twopicsoneword!!.text = reset_twopicsoneword!!.text.toString() + " " + level_twopicsoneword.toString()
        reset_justoneword!!.text = reset_justoneword!!.text.toString() + " " + level_justoneword.toString()
        reset_bucks!!.text = reset_bucks!!.text.toString() + " " + level_bucks.toString() + " " + getString(R.string.reset_bucks_2)
    }

    private fun reset() {
        val savegame = getSharedPreferences("savegame", 0)
        val firstStart = getSharedPreferences("firstStart", 0)
        val editor = savegame.edit()
        val editor1 = firstStart.edit()
        editor.putInt("bucks", 0)
        editor.putInt("level_logicword", 0)
        editor.putInt("level_2pics1word", 0)
        editor.putInt("level_justoneword", 0)
        editor.putInt("startCounter", 0)
        editor.putInt("dontrememberagain", 0)
        for (i in 0..99) {
            h1ABFormat = "savegame_" + "level_" + i + "_hint1_" + "alreadyBought"
            h2ABFormat = "savegame_" + "level_" + i + "_hint2_" + "alreadyBought"
            h3ABFormat = "savegame_" + "level_" + i + "_hint3_" + "alreadyBought"
            editor.putInt(h1ABFormat, 0)
            editor.putInt(h2ABFormat, 0)
            editor.putInt(h3ABFormat, 0)
            a = savegame.getInt(h1ABFormat, 0)
            b = savegame.getInt(h2ABFormat, 0)
            c = savegame.getInt(h3ABFormat, 0)
            Log.d("SettingsAct", a.toString())
            Log.d("SettingsAct", b.toString())
            Log.d("SettingsAct", c.toString())
        }
        editor.apply()
        editor1.putInt("firstStarted", 0)
        editor1.apply()
        level_bucks = savegame.getInt("bucks", 0)
        level_twopicsoneword = savegame.getInt("level_2pics1word", 0)
        level_justoneword = savegame.getInt("level_justoneword", 0)
        level_logicword = savegame.getInt("level_logicword", 0)
        fS = firstStart.getInt("firstStarted", 0)
        Toast.makeText(applicationContext, getString(R.string.options_dialog_reset_toast), Toast.LENGTH_LONG).show()
    }
}