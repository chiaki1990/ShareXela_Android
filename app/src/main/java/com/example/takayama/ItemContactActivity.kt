package com.example.takayama

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
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

        //val notificationTag = intent.extras!!.getString("notificationTag",null)
        /*
        if (notificationTag != null){
            val itemContactObjects = intent.extras!!.getSerializable("itemContactObjects")
        }

        */

        val itemContactObjects = intent.extras!!.getSerializable("itemContactObjects") as ItemContactListAPIViewModel
        println("ItemContactActivity内でitemContactObjectsをprintln")
        println(itemContactObjects)

        //println("クラスチェック")
        //println(itemContactObjects!!::class.java)


        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutItemContact, ItemContactFragment.newInstance(itemContactObjects!!, ""))
            .commit()




    }

}
