package ga.sharexela.sharexela

import android.content.Intent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_bar_master.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MasterActivity : AppCompatActivity(),
    MasterFragment.OnFragmentInteractionListener,
    MyListFragment.OnFragmentInteractionListener {



    lateinit var toolbar: Toolbar




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_master)
        setContentView(R.layout.app_bar_master)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        toolbar.apply{
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                finish()
            }
        }


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {

            if (sessionData.logInStatus == false) {
                makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
                return@setOnClickListener
            }
            sendCrearArticuloActivity(context=this@MasterActivity)
        }

    }



    override fun onResume() {
        super.onResume()



        println("INTENT.EXTRASのテスト")
        println( intent.extras)
        if (intent.extras != null){
            val itemObjectsSerialized = intent.extras?.getSerializable("itemObjectsSerialized") as ItemObjectsSerialized
            val categoryNumber = intent.extras?.getString("categoryNumber")
            val localStatus = intent.extras?.getBoolean("localStatus", false)

            //タイトルの設定
            println(categoryNumber)
            val categoryDisplay = categoryDisplayMaker(categoryNumber!!)
            toolbar.title = categoryDisplay


            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutMaster, MasterFragment.newInstance(itemObjectsSerialized, categoryNumber, localStatus!!))
                .commit()
            return
        }

        //フラグメントの起動 データを取得した後にフラグメントを開く ＃以前のもの
        //firstLaunchMasterFragment()  ＃以前のもの

        //フラグメントの起動 Homeのフラグメントを起動する
        //launchHomeFragmentForApplicationStarted()
    }




    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = true
            findItem(R.id.action_settings).isVisible = true
            findItem(R.id.menuSync).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.menuDone).isVisible = false

        }
        return true
    }




    //MasterFragment.OnFragmentInteractionListener#onSearchMenuSelected
    override fun onSearchMenuSelected() {
        //sendMainActivity(context=this@MasterActivity)
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)

    }



    override fun launchDetailActivity(selectedItem: ItemSerializerModel) {
        //アイテム詳細を表示するDetailActivityを起動する。
        //println("ココは通らない？？")
        val intent = Intent(this@MasterActivity, DetailActivity::class.java)
        intent.putExtra(IntentKey.ItemId.name, selectedItem.id.toString())
        startActivity(intent)
        //sendDetailActivity(context=this@MasterActivity)

    }


}