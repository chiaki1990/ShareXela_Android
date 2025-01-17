package ga.sharexela.sharexela

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService

import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.android.synthetic.main.fragment_log_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LogInActivity : AppCompatActivity(), LogInFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        setSupportActionBar(toolbar)
        Log.d("SignInの後の調査", "LogInActivity#onCreateを通過")

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

        menu.findItem(R.id.menuSync).isVisible = false
        return true

    }



    override fun launchSignUpActivity(){

        //sendSignUpActivity()
        sendSignUpActivity(this@LogInActivity)

        finish()
    }





    //LogInFragment.OnFragmentInteractionListener#getProfileSerializerModel()
    override fun getProfileSerializerModel() {


        val service = setService()

        service.readProfile(sessionData.authTokenHeader!!).enqueue(object :Callback<ProfileSerializerModel>{
            override fun onResponse(call: Call<ProfileSerializerModel>, response: Response<ProfileSerializerModel>) {
                progressBarLogIn.visibility = View.GONE
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

                Log.d("SignInの後の調査", "LogInActivity#getProfileSerializerModelメソッドでこのアクティビティを切る")

                finish()


            }

            override fun onFailure(call: Call<ProfileSerializerModel>, t: Throwable) {
                progressBarLogIn.visibility = View.GONE
                println("onFailureを通る")
                println(t)
                println(t.message)
            }
        })

    }





    override fun onDestroy() {
        Log.d("SignInの後の調査", "LogInActivity#onDestroyを通過")
        super.onDestroy()
    }
}
