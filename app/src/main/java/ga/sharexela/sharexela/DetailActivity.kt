package ga.sharexela.sharexela

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable




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

    override fun launchEditarFragment(itemObj: ItemSerializerModel) {
        // 最初にCrearArticuloActtivityに渡してから記事編集を行うフラグメントの起動 -> タイトルの変更 EditarFragmentの起動

        val intent = Intent(this@DetailActivity, CrearArticuloActivity::class.java)
        intent.putExtra(IntentKey.FragmentTag.name, FragmentTag.EDITAR_ARTICULO.name)
        intent.putExtra(IntentKey.ItemObj.name, itemObj)
        startActivity(intent)
        //toolbar.title = "Ediatar Articulo"

        //supportFragmentManager.beginTransaction()
        //    .replace(R.id.frameLayoutDetail, EditarArticuloFragment.newInstance(itemObj, ""))
        //    .commit()
    }


    //DetailFragment.OnFragmentInteractionListener#launchDirectMessageActivity
    override fun launchDirectMessageActivity(itemObj: ItemSerializerModel) {

        val intent = Intent(this@DetailActivity, DirectMessageActivity::class.java)
        intent.putExtra("itemObj", itemObj )
        startActivity(intent)
    }


    override fun launchItemContactActivity(itemObj: ItemSerializerModel) {
        //引数を送った先のActivity,Fragmentで使うのではなく、先にクエリの結果を取得しそれをFragmentに与える方法に変更する。

        //itemContactObjectsを取得し描画する。
        val service = setService()
        service.getItemContactListAPIView(sessionData.authTokenHeader!!, itemObj.id!!).enqueue(object :
            Callback<ItemContactListAPIViewModel> {

            override fun onResponse(call: Call<ItemContactListAPIViewModel>, response: Response<ItemContactListAPIViewModel>) {
                println("onResponseを通る  ")
                println(response.body())
                val itemContectObjects: ArrayList<ItemContactSerializerModel> = response.body()!!.ITEM_CONTACT_OBJECTS

                val intent = Intent(this@DetailActivity, ItemContactActivity::class.java).apply {
                    putExtra("itemContactObjects", itemContectObjects as Serializable)
                    putExtra("itemObj", itemObj)
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
