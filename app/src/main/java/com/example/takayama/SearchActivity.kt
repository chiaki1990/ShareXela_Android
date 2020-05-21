package com.example.takayama

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(),
    SearchMenuFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        toolbar.apply{
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {

                val intent = Intent(this@SearchActivity, MasterActivity::class.java)
                startActivity(intent)
                //起動中のフラグメントによって挙動を変更する
                finish()
            }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSearch, SearchMenuFragment.newInstance("", ""))
            .commit()


    }



    override fun launchMasterActivity(itemObjectsSerialized: ItemObjectsSerialized, itemObjectsCategory: String) {
        //MasterActivityを起動する、起動したらこのアクティビティは消去
        val intent = Intent(this@SearchActivity, MasterActivity::class.java)
        intent.apply {
            putExtra("itemObjectsSerialized", itemObjectsSerialized)
            putExtra("itemObjectsCategory", itemObjectsCategory)
        }
        startActivity(intent)
    }

}
