package com.example.takayama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URI


//var profileObj :ProfileSerializerModel? = null

/*
var username: String? = "";
var email: String? = "";
var adm0: String? = "";
var adm1: String? = "";
var adm2: String? = "";
var description: String? = "";
var imageUrl: String? = "";
var sex: Int? = 0;
var phoneNumber: String? = "";


 */



class ProfileActivity : AppCompatActivity(),
    ProfileListFragment.OnFragmentInteractionListener,
    EditProfileBasicFragment.OnFragmentInteractionListener,
    EditAreaInfoFragment.OnFragmentInteractionListener,
    EditMailPasswordFragment.OnFragmentInteractionListener,
    EditSexFragment.OnFragmentInteractionListener{


    //onCreateの場合はFragmentが起動されていないから必ずaddで起動しなければならない。
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)


        println("profileObjのプリントProfileACTIVITY")
        println(sessionData.profileObj)


        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {

                if (supportFragmentManager.findFragmentByTag(FragmentTag.PROFILE_LIST.name) != null){
                    finish()
                    return@setNavigationOnClickListener
                }

                supportActionBar?.title = "ユーザープロフィール"
                supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutProfile, ProfileListFragment.newInstance("", ""),FragmentTag.PROFILE_LIST.name)
                .commit()
            }
        }

        //intentKeyを取得(intentKeyはユーザー登録画面またはMasterActivityのサイドバーから発行される)
        val intentKey: String = intent.extras!!.getString(IntentKey.FragmentTag.name)!!

        //intentKeyがFragmentTag.PROFILE_EDIT_AREA.name -> 取引エリア設定画面を表示する
        if (intentKey == FragmentTag.PROFILE_EDIT_AREA.name){

            supportActionBar?.title = "取引エリア設定"

            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutProfile, EditAreaInfoFragment.newInstance("",""), FragmentTag.PROFILE_EDIT_AREA_NEW.name)
                .commit()
            return
        }


        //intentKeyがFragmentTag.PROFILE_LIST.name場合 -> Profileの一覧を表示する
        if (intentKey == FragmentTag.PROFILE_LIST.name){

            supportActionBar?.title = "ユーザープロフィール"

            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutProfile, ProfileListFragment.newInstance("", ""), FragmentTag.PROFILE_LIST.name)
                .commit()
            return
        }
    }

    override fun editProfileBasic() {
        //新たなフラグメントを上から起動する
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditProfileBasicFragment.newInstance("param1", "param2"))
            .commit()
    }


    override fun editAreaInfo() {
        //新たなフラグメントを上から起動する
        supportActionBar?.title = "取引エリア設定"

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditAreaInfoFragment.newInstance("", ""), FragmentTag.PROFILE_EDIT_AREA_CHANGE.name)
            .commit()
    }

    override fun editMailPassword() {
        //新たなフラグメントを上から起動する
        supportActionBar!!.title = "メールアドレス、パスワード設定"

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditMailPasswordFragment.newInstance("", ""))
            .commit()
    }

    override fun editSex() {
        //新たなフラグメントを上から起動する
        supportActionBar!!.title = "性別設定"

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditSexFragment.newInstance("", ""))
            .commit()

    }

    override fun onClickProfileImage() {
        //画像を選択させるギャラリーを開く
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }

        startActivityForResult(intent, REQUEST_CODE_IMAGE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("onActivityResultを起動")
        if (requestCode != REQUEST_CODE_IMAGE) return println("REQUEST_CODEが異なる")
        if (resultCode != Activity.RESULT_OK) return println("RESULT_OKじゃない")




        println("実際のコードを走らす")


        val uri = data!!.data

        val filePath = getPathFromUri(this, uri!!)
        println("FILEの確認   :   " + uri)
        println("FILE_PATH    :    "+ filePath)

        val imageFile = File(filePath!!)


        val reqBody = RequestBody.create(MediaType.parse("image/*"), imageFile)

        val multipartBody = MultipartBody.Part.createFormData("imageProfile", imageFile.name, reqBody)


        //val authTokeHeader = " Token " + authToken
        val service = setService()

        service.patchProfileImage(authTokenHeader= sessionData.authTokenHeader!!, file=multipartBody).enqueue(object : Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("onResponseを通る")

                if (response.isSuccessful){
                    makeToast(this@ProfileActivity, "画像を変更しました。")

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutProfile, ProfileListFragment.newInstance("", ""), FragmentTag.PROFILE_LIST.name)
                        .commit()
                }
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailereを通る")
                println(t)
                println(t.message)
                println(t.localizedMessage)
            }
        })
    }

}
