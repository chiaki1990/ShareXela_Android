package ga.sharexela.sharexela

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity(), SignUpFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_activity_signUp)//"ユーザー登録"

        //バックボタンを実装
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }



        //フラグメントの起動
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSignUp, SignUpFragment.newInstance("",""), "TAG")
            .commit()
    }


    override fun sendEditAreaInfoFragment() {
        val intent = Intent(this@SignUpActivity, ProfileActivity::class.java).apply {
            putExtra(IntentKey.FragmentTag.name, FragmentTag.PROFILE_EDIT_AREA.name)
        }
        startActivity(intent)
        finish()
    }




}
