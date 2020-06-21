package ga.sharexela.sharexela


import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.app_bar_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    MyListFragment.OnFragmentInteractionListener,
    HomeFragment.OnFragmentInteractionListener{


    lateinit var toolbar: Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Log.d("SignInの後の調査", "HomeActivity#onCreateを通過")


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {

            if (sessionData.logInStatus == false) {
                makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
                return@setOnClickListener
            }
            sendCrearArticuloActivity(context = this@HomeActivity)
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        launchHomeFragmentForApplicationStarted()
    }


    private fun launchHomeFragmentForApplicationStarted(){

        //データの取得とHomeFragmentに渡す（起動）

        //データの取得
        val service = setService()
        service.getItemHomeListAPIView().enqueue(object : Callback<ItemHomeListSerializerViewModel> {

            override fun onResponse(call: Call<ItemHomeListSerializerViewModel>, response: Response<ItemHomeListSerializerViewModel>) {

                toolbar.title = getString(R.string.ALL_GUATEMALA)


                var itemObjectsSet = response.body()!!

                //HomeFragmentに渡す（起動）

                //val transaction = supportFragmentManager.beginTransaction()

                //transaction.add(R.id.frameLayoutHome, HomeFragment.newInstance(itemObjectsSet, ""), "first")
                //transaction.add(R.id.frameLayoutHome, HomeFragment.newInstance(itemObjectsSet,""), "first")
                // バックスタックに追加
                //transaction.addToBackStack("first")
                //transaction.commitAllowingStateLoss()
                //transaction.commit()

                Log.d("SignInの後の調査", "HomeActivity内でデータを取得してHomeFragmentを起動する")
                supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayoutHome, HomeFragment.newInstance(itemObjectsSet,""))
                    .commit()
                    //.commitAllowingStateLoss();

            }

            override fun onFailure(call: Call<ItemHomeListSerializerViewModel>, t: Throwable) {
                println("onFailureの結果　： 発生場所：MasterActivity#firstLaunchMasterFragment()　")
                println(t)
                println(t.message)
            }
        })

    }




    override fun onResume() {
        super.onResume()
        Log.d("SignInの後の調査", "HomeActivity#onResumeを通過")

        setUpNavigationDrawer(this)
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
        }
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuSignIn -> {
                //ログイン済みであれば実行しない
                if (sessionData.logInStatus == true){
                    makeToast(this@HomeActivity, getString(R.string.login_status_true))
                    return true
                }
                //sendLogInActivity()
                sendLogInActivity(this@HomeActivity)
            }
            R.id.menuSignUp -> {
                //ログイン済みであれば実行しない
                if (sessionData.logInStatus == true){
                    makeToast(this@HomeActivity, getString(R.string.login_status_true))
                    return true
                }
                //sendSignUpActivity()
                sendSignUpActivity(this@HomeActivity)
            }

            R.id.menuNotification -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                //sendNotificationActivity()
                sendNotificationActivity(this@HomeActivity)
            }

            R.id.menuMyList -> {
                //ログイン済みであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                toolbar.title = getString(R.string.drawer_menu_myList)

                //launchMasterActivtyByMyArticulos()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutHome, MyListFragment.newInstance("",""))
                    .commit()
            }

            R.id.menuFavorite -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendFavoriteItemActivity(this@HomeActivity)
            }

            R.id.menuCrearArticulo ->{
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendCrearArticuloActivity(this@HomeActivity)
            }

            R.id.menuProfile -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendProfileActivity(this@HomeActivity)
            }

            R.id.menuSettings -> {
                //未設定
            }

            R.id.menuOthers -> {
                val intent = Intent(this, OthersActivity::class.java)
                startActivity(intent)
            }

            R.id.menuSignOut -> {
                //logoutを実行してToastで表示する
                if (sessionData.logInStatus == false){
                    makeToast(this@HomeActivity, getString(R.string.drawer_menu_response_no_logIn))
                    return true
                }

                sendSignOutActivity(this@HomeActivity)

            }
        }
        return true
    }


    //MasterFragment.OnFragmentInteractionListener#onSearchMenuSelected
    override fun onSearchMenuSelected() {
        //sendMainActivity(context=this@MasterActivity)
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)


    }

    override fun launchMasterActivity(itemObjectsSerialized: ItemObjectsSerialized, categoryNumber: String, localStatus: Boolean) {

        val intent = Intent(this, MasterActivity::class.java)
        intent.putExtra("itemObjectsSerialized", itemObjectsSerialized)
        intent.putExtra("categoryNumber", categoryNumber)
        intent.putExtra("localStatus",localStatus)
        startActivity(intent)
    }


    override fun reLaunchHomeFragmentforUpdate(itemObjectsSet:ItemHomeListSerializerViewModel, fragment: HomeFragment) {

        //supportFragmentManager.beginTransaction()
        //    .remove(fragment).commitNow()


        //supportFragmentManager.beginTransaction()
        //    .add(R.id.frameLayoutHome, HomeFragment.newInstance(itemObjectsSet, "again"))
        //    .commitNow()
    }


    override fun launchDetailActivity(selectedItem: ItemSerializerModel) {
        //アイテム詳細を表示するDetailActivityを起動する。
        //println("ココは通らない？？")
        val intent = Intent(this@HomeActivity, DetailActivity::class.java)
        intent.putExtra(IntentKey.ItemId.name, selectedItem.id.toString())
        startActivity(intent)

    }


    override fun onDestroy() {
        Log.d("SignInの後の調査", "HomeActivity#onDestroyを通過")
        super.onDestroy()
    }




}