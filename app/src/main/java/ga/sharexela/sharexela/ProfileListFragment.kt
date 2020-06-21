package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"






class ProfileListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        linerLayoutProfileBasic.setOnClickListener{
            //プロフィールBasicの編集フラグメントを起動するまたは置き換える
            listener!!.editProfileBasic()
        }

        linerLayoutAreaInfo.setOnClickListener {

            listener!!.editAreaInfo()
        }

        linerLayoutMailPassword.setOnClickListener {

            listener!!.editMailPassword()
        }

        linerLayoutSex.setOnClickListener {

            listener!!.editSex()
        }


    }

    override fun onResume() {
        super.onResume()

        //val service = setService()

        /* この部分を改修する。

        var retrofit: Retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        var service = retrofit.create(ShareXelaService::class.java)

        val authTokenHeader = " Token " + authToken
        service.readProfile(authTokenHeader).enqueue(object: Callback<ProfileSerializerModel>{
            override fun onResponse(call: Call<ProfileSerializerModel>, response: Response<ProfileSerializerModel>) {

                if (!response.isSuccessful) {
                    //エラーを表示させる
                    println("onResponseの失敗")
                    println(response.message())
                    println(response.errorBody())
                    return
                }


                println("onResponse成功")

                //プロフィールデータの取得
                username = response.body()?.user?.username
                email = response.body()?.user?.email
                adm0 = response.body()?.adm0
                adm1 = response.body()?.adm1
                adm2 = response.body()?.adm2
                description = response.body()?.description
                val image = response.body()?.image
                //imageUrl = "http://10.0.2.2:8000" + image
                imageUrl = BASE_URL + image?.substring(1)
                sex = response.body()?.sex

                //取得データをレイアウトファイルへ反映する
                tvUserName.text = username
                tvEmailAddress.text = email
                Glide.with(MyApplication.appContext).load(imageUrl).into(imageView)
                tvPais.text = adm0
                tvDepartamento.text = adm1
                tvMunicipio.text = adm2
                if (sex == 0){
                    tvSex.text = "未設定"
                }else{
                    tvSex.text = "設定済み"
                }

            }

            override fun onFailure(call: Call<ProfileSerializerModel>, t: Throwable) {
                println("onFalurre")
                println(t.message)
            }


        })

         */

        //取得データをレイアウトファイルへ反映する
        //ログインステータスがTrueでProfileObjがない場合がひょっとして存在する事によるエラーがあるのかもしれない
        println(sessionData.profileObj)
        if (sessionData.profileObj == null) return makeToast(requireContext(),"profileObjがnullになっている")
        tvUserName.text = sessionData.profileObj!!.user!!.username
        tvEmailAddress.text = sessionData.profileObj!!.user!!.email
        val imageUrl = BASE_URL + sessionData.profileObj!!.image!!.substring(1)

        //Glide.with(MyApplication.appContext).load(imageUrl).into(imageView)

        GlideApp.with(MyApplication.appContext).load(imageUrl).circleCrop().into(imageView)


        tvPais.text = sessionData.profileObj!!.adm0
        tvDepartamento.text = sessionData.profileObj!!.adm1
        tvMunicipio.text = sessionData.profileObj!!.adm2
        if (sessionData.profileObj!!.sex == 0){
            tvSex.text = getString(R.string.fragment_profile_list_no_counfigured_sex) //"未設定"
        }else{
            tvSex.text = getString(R.string.fragment_profile_list_counfigured_sex) //"設定済み"
        }

    }





    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    override fun onDetach() {
        super.onDetach()
        listener = null
    }







    interface OnFragmentInteractionListener {

        fun editProfileBasic()

        fun editAreaInfo()

        fun editMailPassword()

        fun editSex()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
