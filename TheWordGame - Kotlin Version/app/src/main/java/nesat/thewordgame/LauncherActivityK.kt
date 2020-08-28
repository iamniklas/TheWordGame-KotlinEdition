package nesat.thewordgame

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class LauncherActivityK : AppCompatActivity() {

    private var handler: Handler? = null
    private var i: Intent? = null

    private var versionView: TextView? = null

    var version = ""

    var updateId = "thewordgameversion"
    var versionUrl = "https://www.nesatstudio.com/app-version/"

    private var tf: Typeface? = null
    lateinit var aDBuilder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        aDBuilder = AlertDialog.Builder(this)
        aDBuilder.setView(layoutInflater.inflate(R.layout.dialogupdate, null)).setCancelable(false)

        handler = Handler()

        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        versionView = findViewById(R.id.versionView)
        tf = Typeface.createFromAsset(assets, "shadowsintolighttwo.ttf")
        versionView!!.typeface = tf

        try {
            val pInfo = this.packageManager.getPackageInfo(packageName, 0)
            version = pInfo.versionName
            versionView!!.text = getString(R.string.launcher_textview_version) + " " + version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        
        handler!!.postDelayed({
            dialog = aDBuilder.create()
            dialog.show()
            val versionCheck = UpdateChecker()
            versionCheck.execute(versionUrl, updateId)
        }, 1000)
    }

    fun updateDone(uptodate: Boolean) {
        dialog.dismiss()
        Log.d("The Word Game", "uptodate = " + updateId)
        when(uptodate) {
            true -> {
                i = Intent(this, MainMenuActivityK::class.java)
                startActivity(i) }
            false -> {
                val versionNoMatch = AlertDialog.Builder(this)
                versionNoMatch.setTitle(getString(R.string.launcher_updatedialog_updateavailable_title))
                        .setMessage(getString(R.string.launcher_updatedialog_updateavailable_message)).setCancelable(false)
                        .setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=nesat.thewordgame&hl=de"))
                            startActivity(browserIntent)
                        })
                        .create().show()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class UpdateChecker: AsyncTask<String, String, String>() {
        private lateinit var document: Document
        var networkVersion = ""

        override fun doInBackground(vararg params: String?): String {
            val stringUrl: String? = params[0]
            val elementId: String? = params[1]
            try {
                document = Jsoup.connect(stringUrl).get()
                this.networkVersion = document.getElementById(elementId).text()
                Log.d("The Word Game", "Network Version = " + networkVersion)
                Log.d("The Word Game", "Local Version = " + version)
            }
            catch (ioe: IOException) {
                networkVersion = version
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (networkVersion.equals(version)) {
                Log.d("The Word Game", "Up to date")
                updateDone(true)
            }
            else {
                Log.d("The Word Game", "Not up to date")
                updateDone(false)
            }
        }
    }
}