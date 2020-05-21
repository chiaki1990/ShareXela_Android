package com.example.takayama

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_sign_out.*

class SignOutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_out)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // sessionDataを初期化する
        sessionData = SessionData()

        //SPにLOGIN_STATUSをfalseに保存する
        val sharedPreferences = getSharedPreferencesInstance()
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.SP_KEY_LOGIN_STATUS), false).apply()
        makeToast(this@SignOutActivity, "ログアウトしました。")

        //アクティビティを閉じる
        finish()
    }

}
