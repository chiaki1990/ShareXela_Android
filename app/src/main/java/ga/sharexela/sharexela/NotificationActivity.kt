package ga.sharexela.sharexela

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class NotificationActivity : AppCompatActivity(),
    NotificationFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }




        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutNotification, NotificationFragment.newInstance("", ""))
            .commit()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main, menu)
        menu.apply {
            findItem(R.id.menuSync).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false
            findItem(R.id.menuSearch).isVisible = false
        }
        return true
    }

    override fun launchNotificationSolicitar(objectId: String) {
        //通知に応じたFragmentを起動させるためにデータを取得しActivityを起動する

        val service = setService()
        service.getSolicitudAPIView(sessionData.authTokenHeader, objectId.toInt()).enqueue(object :Callback<SolicitudAPIViewModel>{

            override fun onResponse(call: Call<SolicitudAPIViewModel>, response: Response<SolicitudAPIViewModel>) {
                println("onResponseを通る")
                val solicitudObj = response.body()!!.SOLICITUD_OBJECT

                //println(solicitudObj)

                val intent = Intent(this@NotificationActivity, SolicitarActivity::class.java).apply{
                    putExtra("solicitudObj", solicitudObj)
                    putExtra("launchFragmentTag","notification")
                }
                startActivity(intent)
            }

            override fun onFailure(call: Call<SolicitudAPIViewModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
            }
        })

    }

    override fun launchNotificationItemContact(itemContactobjId: String) {

        val service = setService()

        service.getItemContactListByContactObjPKAPIView(sessionData.authTokenHeader!!, itemContactobjId.toInt()).enqueue(object :Callback<ItemContactListAPIViewModel>{

            override fun onResponse(call: Call<ItemContactListAPIViewModel>, response: Response<ItemContactListAPIViewModel>) {
                println("onResponseを通る")

                val itemContactObjects = response.body()!!.ITEM_CONTACT_OBJECTS
                val itemObj = response.body()!!.ITEM_OBJECT
                println(itemContactObjects)
                println(itemObj)
                val intent = Intent(this@NotificationActivity, ItemContactActivity::class.java).apply {
                    putExtra("itemContactObjects", itemContactObjects as Serializable)
                    putExtra("itemObj", itemObj)
                    putExtra("notificationTag", "notificationTag")
                }
                startActivity(intent)
            }

            override fun onFailure(call: Call<ItemContactListAPIViewModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
            }
        })
    }

    override fun launchNotificationDirectMessageContent(directMessageContentObjId: String) {

        //内容的にはItemObjの取引に移る画面を表示することを目的とする。

        //必要なデータをメモしておく

        val service = setService()
        service.getItemObjByDirectMessageContentObjPKAPIView(sessionData.authTokenHeader, directMessageContentObjId.toInt()).enqueue(object :Callback<ItemSerializerModel> {

            override fun onResponse(call: Call<ItemSerializerModel>, response: Response<ItemSerializerModel>) {
                println("onResponseを通る")
                val itemObj = response.body()
                println(itemObj)
                //intentを画面に渡す

                val intent = Intent(this@NotificationActivity, DirectMessageActivity::class.java).apply {
                    putExtra("itemObj", itemObj)
                }
                startActivity(intent)
            }

            override fun onFailure(call: Call<ItemSerializerModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
            }
        })
    }
    
}


