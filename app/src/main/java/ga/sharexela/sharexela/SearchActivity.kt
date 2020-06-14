package ga.sharexela.sharexela

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_search_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity(),
    SearchMenuFragment.OnFragmentInteractionListener{  //HomeFragment.OnFragmentInteractionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)



        title = "Todas las categorías"

        toolbar.apply{
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }

        fab.setOnClickListener { view ->
            if (sessionData.logInStatus == false) {
                makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
                return@setOnClickListener
            }
            sendCrearArticuloActivity(context=this@SearchActivity)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSearch, SearchMenuFragment.newInstance("", ""))
            .commit()

    }



    override fun launchMasterActivity(itemObjectsSerialized: ItemObjectsSerialized, categoryNumber: String, localStatus:Boolean) {
        //MasterActivityを起動する、起動したらこのアクティビティは消去
        val intent = Intent(this@SearchActivity, MasterActivity::class.java)
        intent.apply {
            putExtra("itemObjectsSerialized", itemObjectsSerialized)
            putExtra("categoryNumber", categoryNumber)
            putExtra("localStatus", localStatus)
        }
        startActivity(intent)
    }



}
