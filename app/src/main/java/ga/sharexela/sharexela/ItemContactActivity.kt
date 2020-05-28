package ga.sharexela.sharexela

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_item_contact.*

class ItemContactActivity : AppCompatActivity(),
    ItemContactFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_contact)
        setSupportActionBar(toolbar)
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }


        //初回表示用
        val itemContactObjects = intent.extras!!.getSerializable("itemContactObjects")
        //onResume用
        val itemObj = intent.extras!!.getSerializable("itemObj") as ItemSerializerModel
        println("ItemContactActivity内でitemContactObjectsをprintln")
        println(itemContactObjects)

        //println("クラスチェック")
        //println(itemContactObjects!!::class.java)


        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutItemContact, ItemContactFragment.newInstance(itemContactObjects!!, itemObj))
            .commit()

    }

}
