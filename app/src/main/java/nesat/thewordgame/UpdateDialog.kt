package nesat.thewordgame

import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button


class UpdateDialog(var c: Activity) : Dialog(c), android.view.View.OnClickListener {

    var d: Dialog? = null
    lateinit var yes: Button
    lateinit var no: Button

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialogupdate)
    }

    override fun onClick(v: View) {

    }
}