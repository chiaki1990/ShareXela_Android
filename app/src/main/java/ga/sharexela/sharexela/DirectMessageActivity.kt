package ga.sharexela.sharexela

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_direct_message.*

class DirectMessageActivity : AppCompatActivity(),
    DirectMessageFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_message)
        setSupportActionBar(toolbar)



        // Todo アローバックを実装する
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        // intentからデータを取得
        val itemObj = intent.extras!!.getSerializable("itemObj") as ItemSerializerModel


        // DirectMessageFragmentを起動する
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutDirectMessage, DirectMessageFragment.newInstance(itemObj,""))
            .commit()

    }

}
