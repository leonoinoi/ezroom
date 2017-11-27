package com.leonoinoi.ezroom

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    private lateinit var google: GoogleSignIn
    private val TAG = "EZROOM_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        //Init GoogleSignin
        google = GoogleSignIn(this) { message ->
            when (message) {
                "SignIn Success" -> {
                    StyleableToast.Builder(this)
                            .text(message)
                            .textColor(Color.WHITE)
                            .icon(R.drawable.ic_check_circle_white)
                            .backgroundColor(ContextCompat.getColor(this, R.color.green500))
                            .build().show()
                    startActivity(Intent(this, OwnerDashBoard::class.java))
                }
                else -> StyleableToast.Builder(this)
                        .text(message)
                        .textColor(Color.WHITE)
                        .icon(R.drawable.ic_cancel)
                        .backgroundColor(ContextCompat.getColor(this, R.color.pink500))
                        .build().show()
            }
        }

        signin_button.setOnClickListener {
            google.signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        google.onActivityResult(requestCode, data!!)
    }

    override fun onBackPressed() {}
}
