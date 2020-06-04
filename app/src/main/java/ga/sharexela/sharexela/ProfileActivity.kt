package ga.sharexela.sharexela

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File



class ProfileActivity : AppCompatActivity(),
    ProfileListFragment.OnFragmentInteractionListener,
    EditProfileBasicFragment.OnFragmentInteractionListener,
    EditAreaInfoFragment.OnFragmentInteractionListener,
    EditMailPasswordFragment.OnFragmentInteractionListener,
    EditSexFragment.OnFragmentInteractionListener,
    GetCoordinatesFragment.OnFragmentInteractionListener {


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

                supportActionBar?.title = getString(R.string.user_profile_title)
                supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutProfile, ProfileListFragment.newInstance("", ""), FragmentTag.PROFILE_LIST.name)
                .commit()
            }
        }

        //intentKeyを取得(intentKeyはユーザー登録画面またはMasterActivityのサイドバーから発行される)
        val intentKey: String = intent.extras!!.getString(IntentKey.FragmentTag.name)!!

        //intentKeyがFragmentTag.PROFILE_EDIT_AREA.name -> 取引エリア設定画面を表示する
        if (intentKey == FragmentTag.PROFILE_EDIT_AREA.name){

            supportActionBar?.title = getString(R.string.set_area_title)//"取引エリア設定"

            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutProfile, EditAreaInfoFragment.newInstance("",""), FragmentTag.PROFILE_EDIT_AREA_NEW.name)
                .commit()
            return
        }


        //intentKeyがFragmentTag.PROFILE_LIST.name場合 -> Profileの一覧を表示する
        if (intentKey == FragmentTag.PROFILE_LIST.name){

            supportActionBar?.title = getString(R.string.user_profile_title)

            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutProfile, ProfileListFragment.newInstance("", ""), FragmentTag.PROFILE_LIST.name)
                .commit()
            return
        }
    }


    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        //super.onCreateOptionsMenu(menu, inflater)
        menuInflater.inflate(R.menu.main, menu)

        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false
        }
        return true
    }





    override fun editProfileBasic() {
        //新たなフラグメントを上から起動する
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditProfileBasicFragment.newInstance("param1", "param2"))
            .commit()
    }


    override fun editAreaInfo() {
        //新たなフラグメントを上から起動する
        supportActionBar?.title = getString(R.string.set_area_title)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditAreaInfoFragment.newInstance("", ""), FragmentTag.PROFILE_EDIT_AREA_CHANGE.name)
            .commit()
    }

    override fun launchGetCoordinatesFragment() {

        //タイトルを編集
        toolbar.title = getString(R.string.title_set_point_and_radius) //"取引場所の設定"

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, GetCoordinatesFragment.newInstance(null, ""), FragmentTag.FROM_EDIT_AREA_INFO_FRAGMENAT.name)
            .commit()
    }

    override fun editMailPassword() {
        //新たなフラグメントを上から起動する
        supportActionBar!!.title = getString(R.string.set_mail_password_title) //"メールアドレス、パスワード設定"

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutProfile, EditMailPasswordFragment.newInstance("", ""))
            .commit()
    }

    override fun editSex() {
        //新たなフラグメントを上から起動する
        supportActionBar!!.title = getString(R.string.set_sex_title)//"性別設定"//Elección del género

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
                    makeToast(this@ProfileActivity, getString(R.string.success_change_profile_image))

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

    //GetCoordinatesFragment.OnFragmentInteractionListener#updateItemObj
    override fun sendCrearArticuloFragmentAgain(itemObj: ItemSerializerModel?) {
        //使わない CrearArticuloFragmentで使うもの
    }

    override fun sendEditarArticuloFragmentAgain(itemObj: ItemSerializerModel?) {
        //使わない EditarArticuloFragmentで使うもの
    }

}
