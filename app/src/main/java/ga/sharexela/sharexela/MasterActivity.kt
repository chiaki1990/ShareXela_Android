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
import kotlinx.android.synthetic.main.app_bar_master.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MasterActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    MasterFragment.OnFragmentInteractionListener,
    MyListFragment.OnFragmentInteractionListener {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {

            //ログインユーザーかチェックするのが終わっていない。
            sendCrearArticuloActivity(context=this@MasterActivity)
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()


        nav_view.setNavigationItemSelectedListener(this)

    }



    override fun onResume() {
        super.onResume()


        //ナビゲーションドロワーの編集
        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        val h_view = nav_view.getHeaderView(0)
        val userProfileImageView = h_view.findViewById<ImageView>(R.id.iv_profile)
        val tv_userName = h_view.findViewById<TextView>(R.id.tv_userName)
        val tv_emailAddress = h_view.findViewById<TextView>(R.id.tv_emailAddress)

        if (sessionData.profileObj != null){
            //ナビゲーションドロワーヘッダーの編集
            val profileImageUrl = BASE_URL + sessionData.profileObj!!.image!!.substring(1)
            //Glide.with(MyApplication.appContext).load(profileImageUrl).into(userProfileImageView)
            Glide.with(MyApplication.appContext).load(profileImageUrl).circleCrop().into(userProfileImageView)
            tv_userName.text = sessionData.profileObj!!.user!!.username
            tv_emailAddress.visibility = View.VISIBLE
            tv_emailAddress.text = sessionData.profileObj!!.user!!.email
            //ナビゲーションドロワーメニューの編集(ログインメニューの削除)
            //val menu = nav_view.menu
            //menu.removeItem(R.id.menuSignIn)
            //menu.removeItem(R.id.menuSignUp)


        }else if (sessionData.profileObj == null){
            tv_userName.setText("未ログイン") //text = "未ログイン"
            tv_emailAddress.visibility = View.GONE
            Glide.with(MyApplication.appContext).load(R.drawable.ic_account_circle_black_72dp).into(userProfileImageView)

            //ナビゲーションドロワーメニューの編集(ログインメニューの削除)
            //val menu = nav_view.menu
            //menu.removeItem(R.id.menuSignOut)
        }




        println("INTENT.EXTRASのテスト")
        println( intent.extras)
        if (intent.extras != null){
            val itemObjectsSerialized = intent.extras?.getSerializable("itemObjectsSerialized") as ItemObjectsSerialized
            val itemObjectsCategory = intent.extras?.getString("itemObjectsCategory")
            //fragmentの起動でデータを受け渡す。。。

            //タイトルの変更
            when(itemObjectsCategory){
                ItemObjectsCategory.DONAR_GUATEMALA.name -> {toolbar.title= "DONAR O VENDER"}
                ItemObjectsCategory.DONAR_LOCAL.name -> {toolbar.title= "DONAR O VENDER"}
                ItemObjectsCategory.AYUDAR_GUATEMALA.name -> {toolbar.title= "BUSCAR AYUDA"}
                ItemObjectsCategory.AYUDAR_LOCAL.name -> {toolbar.title= "BUSCAR AYUDA"}
                ItemObjectsCategory.ANUNCIO_GUATEMALA.name -> {toolbar.title= "ANUNCIATE"}
                ItemObjectsCategory.ANUNCIO_LOCAL.name -> {toolbar.title= "ANUNCIATE"}
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutMaster, MasterFragment.newInstance(itemObjectsSerialized, itemObjectsCategory))
                .commit()
            return
        }

        //フラグメントの起動 データを取得した後にフラグメントを開く
        firstLaunchMasterFragment()

    }





    private fun firstLaunchMasterFragment() {
        //データを取得した後にフラグメントを開く

        val itemObjectsCategory = ItemObjectsCategory.ALL_GUATEMALA.name

        val service = setService()
        service.getItemListAPIView().enqueue(object : Callback<ItemListAPIViewModel> {


            override fun onResponse(call: Call<ItemListAPIViewModel>, response: Response<ItemListAPIViewModel>) {

                toolbar.title = getString(R.string.ALL_GUATEMALA)

                //修正前var itemObjects: List<ItemSerializerModel> = response.body()?.ITEM_OBJECTS!!
                var itemObjects: List<ItemSerializerModel> = response.body()?.ITEM_OBJECTS!!
                val itemObjectsSelialized: ItemObjectsSerialized = ItemObjectsSerialized(itemObjects = itemObjects)

                supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayoutMaster, MasterFragment.newInstance(itemObjectsSelialized, itemObjectsCategory))
                    //.commit()
                    .commitAllowingStateLoss( );

            }

            override fun onFailure(call: Call<ItemListAPIViewModel>, t: Throwable) {
                println("onFailureの結果　： 発生場所：MasterActivity#firstLaunchMasterFragment()　")
                println(t)
                println(t.message)
            }
        })

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
                    makeToast(this@MasterActivity, getString(R.string.login_status_true))
                    return true
                }
                //sendLogInActivity()
                sendLogInActivity(this@MasterActivity)
            }
            R.id.menuSignUp -> {
                //ログイン済みであれば実行しない
                if (sessionData.logInStatus == true){
                    makeToast(this@MasterActivity, getString(R.string.login_status_true))
                    return true
                }
                //sendSignUpActivity()
                sendSignUpActivity(this@MasterActivity)
                 }

            R.id.menuNotification -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@MasterActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                //sendNotificationActivity()
                sendNotificationActivity(this@MasterActivity)
            }

            R.id.menuMyList -> {
                //ログイン済みであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@MasterActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutMaster, MyListFragment.newInstance("",""))
                    .commit()
            }

            R.id.menuFavorite -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@MasterActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendFavoriteItemActivity(this@MasterActivity)
            }

            R.id.menuCrearArticulo ->{
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@MasterActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendCrearArticuloActivity(this@MasterActivity)
            }

            R.id.menuProfile -> {
                //未ログインであれば実行しない
                if (sessionData.logInStatus == false){
                    makeToast(this@MasterActivity, getString(R.string.toast_message_needSignIn))
                    return true
                }
                sendProfileActivity(this@MasterActivity)
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
                    makeToast(this@MasterActivity, "ログインしていません。")
                    return true
                }

                sendSignOutActivity(this@MasterActivity)

            }
        }
        return true
    }




    //MasterFragment.OnFragmentInteractionListener#onSearchMenuSelected
    override fun onSearchMenuSelected() {
        //sendMainActivity(context=this@MasterActivity)
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)

        finish()
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