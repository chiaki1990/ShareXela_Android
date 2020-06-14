package ga.sharexela.sharexela

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_favorite_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteItemActivity : AppCompatActivity(),
    FavoriteItemFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_item)
        setSupportActionBar(toolbar)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }

    }


    override fun onResume() {
        super.onResume()
        //favのアイテムリストを取得してMasterActivityを起動する

        sendFavItemObjectsToFavItemFragment()



    }

    private fun sendFavItemObjectsToFavItemFragment(){

        val service = setService()
        service.getItemFavoriteListAPIVIiew(authTokenHeader = sessionData.authTokenHeader!!)
            .enqueue(object : Callback<ItemUniversalListAPIView> {

                override fun onResponse(
                    call: Call<ItemUniversalListAPIView>,
                    response: Response<ItemUniversalListAPIView>
                ) {

                    println("onResponseを通る : FavoriteItemActivity#getFavItemObjects()")

                    var result = response.body()?.result
                    if (result != null ) return

                    val itemObjects = response.body()!!.ITEM_OBJECTS
                    val itemObjectsSerialized: ItemObjectsSerialized =
                        ItemObjectsSerialized(itemObjects = itemObjects)


                    supportFragmentManager.beginTransaction()
                        .add(R.id.frameLayoutFavItem, FavoriteItemFragment.newInstance(itemObjectsSerialized,""))
                        .commit()


                }

                override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                    //
                    println("onFailureを通る : FavoriteItemActivity#getFavItemObjects()")

                    println(t)
                    println(t.message)

                }
            })


    }

    override fun launchDetailActivity(selectedItem: ItemSerializerModel) {
        //アイテム詳細を表示するDetailActivityを起動する。
        //println("ココは通らない？？")
        val intent = Intent(this@FavoriteItemActivity, DetailActivity::class.java)
        intent.putExtra(IntentKey.ItemId.name, selectedItem.id.toString())
        startActivity(intent)
        //sendDetailActivity(context=this@MasterActivity)
    }


}
