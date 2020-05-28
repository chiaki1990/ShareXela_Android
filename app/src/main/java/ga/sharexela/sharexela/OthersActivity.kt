package ga.sharexela.sharexela

import android.os.Bundle
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
