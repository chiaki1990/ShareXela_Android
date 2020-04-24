package com.example.takayama

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity(), DetailFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }




        val bundle = intent.extras!!
        var itemId:String = bundle.getString(IntentKey.ItemId.name)!!
        //Detailフラグメントの起動
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutDetail, DetailFragment.newInstance(itemId,"param2"),"TAG")
            .commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    //DetailFragment.OnFragmentInteractionListener#onSearchMenuSelected
    override fun onSearchMenuSelected() {
        sendMainActivity(this@DetailActivity)
    }

    //DetailFragment.OnFragmentInteractionListener#launchSolicitarActivity
    override fun launchSolicitarActivity(solicitud_objects: ArrayList<SolicitudSerializerModel>, launchFragmentTag :String) {

        val intent = Intent(this@DetailActivity, SolicitarActivity::class.java)
        intent.putExtra("solicitud_objects", solicitud_objects )
        intent.putExtra("launchFragmentTag", launchFragmentTag)
        startActivity(intent)
    }



    override fun launchSolicitarMessageMakingFragment(itemObj:ItemSerializerModel, tag: String){

        val launchFragmentTag = "SolicitarMessageMakingFragment"
        val intent = Intent(this@DetailActivity, SolicitarActivity::class.java).apply {
            putExtra("launchFragmentTag", launchFragmentTag)
            putExtra("itemObj",itemObj)
        }
        startActivity(intent)

    }





    //DetailFragment.OnFragmentInteractionListener#launchDirectMessageActivity
    override fun launchDirectMessageActivity(itemObj: ItemSerializerModel) {

        val intent = Intent(this@DetailActivity, DirectMessageActivity::class.java)
        intent.putExtra("itemObj", itemObj )
        startActivity(intent)
    }


    override fun launchItemContactActivity(itemObjId: Int) {
        //引数を送った先のActivity,Fragmentで使うのではなく、先にクエリの結果を取得しそれをFragmentに与える方法に変更する。

        //itemContactObjectsを取得し描画する。
        val service = setService()
        val authTokenHeader = getAuthTokenHeader(authToken)
        if (authTokenHeader == null) return
        service.getItemContactListAPIView(authTokenHeader, itemObjId!!).enqueue(object :
            Callback<ItemContactListAPIViewModel> {

            override fun onResponse(call: Call<ItemContactListAPIViewModel>, response: Response<ItemContactListAPIViewModel>) {
                println("onResponseを通る  ")
                println(response.body())
                val itemContectObjects: ItemContactListAPIViewModel = response.body()!!

                val intent = Intent(this@DetailActivity, ItemContactActivity::class.java).apply {
                    putExtra("itemContactObjects", itemContectObjects)
                }
                startActivity(intent)

            }

            override fun onFailure(call: Call<ItemContactListAPIViewModel>, t: Throwable) {
                println(" onFailureを通る  ")
                println(t)
                println(t.message)
            }
        })
    }
}
