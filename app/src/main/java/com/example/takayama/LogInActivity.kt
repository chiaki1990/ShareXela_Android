package com.example.takayama

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_log_in.*



class LogInActivity : AppCompatActivity(), LogInFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        setSupportActionBar(toolbar)

        //バックボタンを実装
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }

        //アクティビティタイトルの設定
        setTitle(R.string.toolbar_login_title)

        //フラグメントの起動
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutLogIn, LogInFragment.newInstance("param1", "param2"),"TAGの設定")
            .commit()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

    }

    //LogInFragment.OnFragmentInteractionListener#resultSuccessLogIn()
    override fun resultSuccessLogIn() {
        makeToast(this@LogInActivity, getString(R.string.login_success_message))

        //ログインのインスタンスを生成する
        MyApplication.loginStatus = true
        finish()
    }



}
