package ga.sharexela.sharexela

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_log_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LogInActivity : AppCompatActivity(), LogInFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        setSupportActionBar(toolbar)

        //バックボタンを実装
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }

        //アクティビティタイトルの設定
        setTitle(R.string.toolbar_login_title)

        //フラグメントの起動
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutLogIn, LogInFragment.newInstance("", ""),"TAGの設定")
            .commit()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        //menu.findItem(R.id.menuLogIn).isVisible = true
        return true

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)

    }


    //LogInFragment.OnFragmentInteractionListener#resultSuccessLogIn()
    /*
    override fun resultSuccessLogIn() {
        makeToast(this@LogInActivity, getString(R.string.login_success_message))

        /* 改修中
        //ログインのインスタンスを生成する
        MyApplication.loginStatus = true

        println("profileObjのプリントLOGINACTIBVITY")
        println(profileObj)

        */

        finish()
    }
    */




    //LogInFragment.OnFragmentInteractionListener#getProfileSerializerModel()
    override fun getProfileSerializerModel() {


        val service = setService()

        service.readProfile(sessionData.authTokenHeader!!).enqueue(object :Callback<ProfileSerializerModel>{
            override fun onResponse(call: Call<ProfileSerializerModel>, response: Response<ProfileSerializerModel>) {
                println("onResponseを通る")

                makeToast(this@LogInActivity, getString(R.string.login_success_message))

                val profileObj = response.body()
                println("profileObjの表示")
                println(profileObj)
                sessionData.profileObj = profileObj

                //SPとSessionDataにLOGIN_STATUSを保持させる
                sessionData.logInStatus = true
                val sharedPreferences = getSharedPreferencesInstance()
                val editor = sharedPreferences.edit()
                editor.putBoolean(getString(R.string.SP_KEY_LOGIN_STATUS), true)
                editor.apply()

                finish()

            }

            override fun onFailure(call: Call<ProfileSerializerModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
                println(t.message)
            }


        })

    }
}

/*

       val service = setService()
        val authTokenHeader = getAuthTokenHeader(authToken)
        if (authTokenHeader == null) return
        service.readProfile(authTokenHeader).enqueue(object : Callback<ProfileSerializerModel>{

            override fun onResponse(call: Call<ProfileSerializerModel>, response: Response<ProfileSerializerModel>) {

                println("onResponseを通る")
                profileObj = response.body()!!
            }

            override fun onFailure(call: Call<ProfileSerializerModel>, t: Throwable) {

                println("onFailureを通る")
                println(t)
                println(t.message)
            }
        })

 */
