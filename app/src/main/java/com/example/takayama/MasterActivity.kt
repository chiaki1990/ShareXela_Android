package com.example.takayama

import android.content.Intent

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
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
        fab.setOnClickListener { view ->
            //記事作成 ただカメラ機能を実装しなければならない


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


        println("INTENT.EXTRASのテスト")
        println( intent.extras)
        if (intent.extras != null){
            val itemObjectsSerialized = intent.extras?.getSerializable("itemObjectsSerialized") as ItemObjectsSelialized
            //fragmentの起動でデータを受け渡す。。。

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutMaster, MasterFragment.newInstance(itemObjectsSerialized, ""))
                .commit()
            return
        }

        //フラグメントの起動 データを取得した後にフラグメントを開く
        firstLaunchMasterFragment()

    }





    private fun firstLaunchMasterFragment() {
        //データを取得した後にフラグメントを開く


        val service = setService()
        service.getItemListAPIView().enqueue(object : Callback<ItemListAPIViewModel> {


            override fun onResponse(call: Call<ItemListAPIViewModel>, response: Response<ItemListAPIViewModel>) {

                var itemObjects: List<ItemSerializerModel> = response.body()?.ITEM_OBJECTS!!
                val itemObjectsSelialized: ItemObjectsSelialized = ItemObjectsSelialized(itemObjects = itemObjects)

                supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayoutMaster, MasterFragment.newInstance(itemObjectsSelialized, "param2"))
                    .commit()

            }

            override fun onFailure(call: Call<ItemListAPIViewModel>, t: Throwable) {
                println("onFailureの結果　：　")
                println(t)
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

                // sessionDataを初期化する
                sessionData = SessionData()

                //SPにLOGIN_STATUSをfalseに保存する
                val sharedPreferences = getSharedPreferencesInstance()
                val editor = sharedPreferences.edit()
                editor.putBoolean(getString(R.string.SP_KEY_LOGIN_STATUS), false).apply()
                makeToast(this@MasterActivity, "ログアウトしました。")


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

    override fun launchDetailActivity(selectedItem: ItemModel) {
        //アイテム詳細を表示するDetailActivityを起動する。
        //println("ココは通らない？？")
        val intent = Intent(this@MasterActivity, DetailActivity::class.java)
        intent.putExtra(IntentKey.ItemId.name, selectedItem.id.toString())
        startActivity(intent)
        //sendDetailActivity(context=this@MasterActivity)

    }


}