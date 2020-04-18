package com.example.takayama

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_others.*

class OthersActivity : AppCompatActivity(),
    OthersMenuFragment.OnFragmentInteractionListener,
    ContactUsFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutOthers, OthersMenuFragment.newInstance("", ""))
            .commit()

    }

    override fun launchContactUsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutOthers, ContactUsFragment.newInstance("",""))
            .commit()
    }

    override fun launchHowToFragment() {
        //supportFragmentManager.beginTransaction().replace().commit()
    }

    override fun launchPoliticaFragment() {
        //supportFragmentManager.beginTransaction().replace().commit()
    }

    override fun successContactInstance() {
        //Contactモデルインスタンスを生成したので、このアクティビティを終了する。
        finish()
    }

}
