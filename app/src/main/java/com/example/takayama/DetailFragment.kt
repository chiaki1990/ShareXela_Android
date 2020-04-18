package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.tvItemTitle
import kotlinx.android.synthetic.main.my_recyclerview_card.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemId: String = ""
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var authTokenHeader: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it.getString(ARG_PARAM1)!!
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = true
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = true
            //findItem(R.id.menuBottomSolicitar).isVisible = false
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuSearch -> {
                listener!!.onSearchMenuSelected()
                }
        }
            return true
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }


    override fun onResume() {
        super.onResume()


        //サーバーとやり取りして画面を描画する
        //itemIdを起点にItemオブジェクトデータを取得する

        val service = setService()
        if (authToken != "" || authToken != null){
            authTokenHeader = " Token " + authToken
        }

        service.getItemDetailSerializerAPIView(itemId,authTokenHeader).enqueue(object :Callback<ItemDetailSerializerAPIViewModel>{
            override fun onResponse(call: Call<ItemDetailSerializerAPIViewModel>, response: Response<ItemDetailSerializerAPIViewModel>) {
                println("onResponseを通過")
                println(response.isSuccessful)
                println(response.message())
                if (response.isSuccessful){
                    //val itemId = response.body()!!.item_obj_serializer.id
                    val itemObj = response.body()!!.item_obj_serializer
                    val itemTitle = itemObj.title
                    val itemDescription = itemObj.description
                    val itemCreatedAt = itemObj.created_at
                    //価格は後で実装する
                    val itemImage1 = itemObj.image1
                    val itemAdm0 = itemObj.adm0
                    val itemAdm1 = itemObj.adm1
                    val itemAdm2 = itemObj.adm2
                    //pointは今後実装する
                    //val itemPoint = response.body()!!.item_obj_serializer.
                    val itemCategory = response.body()!!.item_obj_serializer.category.name

                    val hoge = response.body()!!.item_contact_objects_serializer
                    println("itemContact   " + hoge)
                    val solicitud_objects: ArrayList<SolicitudSerializerModel> = response.body()!!.SOLICITUD_OBJECTS_SERIALIZER
                    val btnChoice = response.body()!!.BTN_CHOICE
                    println("BTNcHOICE  : "+btnChoice)


                    //画面にデータを描画する
                    tvItemTitle.text = itemTitle
                    tvItemDescription.text = itemDescription
                    tvCreatedAt.text = itemCreatedAt
                    tvCategoryContent.text = itemCategory
                    tvDealAreaAdm1.text = itemAdm1
                    tvDealAreaAdm2.text = itemAdm2
                    val imageUrl = BASE_URL + itemImage1!!.substring(1)
                    println(imageUrl)
                    Glide.with(MyApplication.appContext).load(Uri.parse(imageUrl)).into(ivItemDetail)


                    //btnChoiceによって下部バーボタンの内容を表示する
                    if (btnChoice == BtnChoice.ANONYMOUS_USER_ACCESS.name){
                        //描画内容 -> "申請する"を表示する
                        //コメントは表示のみ行う
                        btnTransaction.visibility = View.GONE
                        //ここはアイテムの状態によって表示する内容を変えるように改善の余地あり。
                        btnShowFail.visibility = View.GONE
                        btnSolicitado.visibility = View.GONE

                        //コメントを一覧するビューを作成するそして、その画面へ遷移させる
                        btnCommentBottom.setOnClickListener {  }
                        btnComment.setOnClickListener {  }

                        //申請するボタンを押すとwarningをToastで表示する
                        btnSolicitar.setOnClickListener {
                            makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
                        }
                        return
                    }
                    if (btnChoice == BtnChoice.SOLICITAR.name){
                        //描画内容 -> "申請する"を表示する
                        btnTransaction.visibility = View.GONE
                        btnShowFail.visibility = View.GONE
                        btnSolicitado.visibility = View.GONE
                        btnSelectSolicitudes.visibility = View.GONE
                        btnSolicitar.visibility = View.VISIBLE
                        btnCommentBottom.visibility = View.VISIBLE

                        btnCommentBottom.setOnClickListener {  }
                        btnComment.setOnClickListener {  }

                        //申請するボタンを押すと、申請メッセージフォーム画面遷移する。
                        btnSolicitar.setOnClickListener{  }
                        return
                    }
                    if (btnChoice == BtnChoice.SOLICITADO.name){
                        //Todo あとで記述する
                        return
                    }
                    //ユーザーが出品者である場合&&申請者が現れていない場合
                    if (btnChoice == BtnChoice.NO_SOLICITUDES.name){
                        //"コメント"を表示させる
                        btnTransaction.visibility = View.GONE
                        btnShowFail.visibility = View.GONE
                        btnSolicitar.visibility = View.GONE
                        btnSolicitado.visibility = View.GONE
                        btnSelectSolicitudes.visibility = View.VISIBLE
                        btnSelectSolicitudes.isEnabled = false
                        btnCommentBottom.visibility = View.VISIBLE

                    }

                    //ユーザーが出品者である場合&&申請者が現われ、申請者を選ぶ場合
                    if (btnChoice == BtnChoice.SELECT_SOLICITUDES.name){
                        //"申請者を選ぶボタン"を表示させる
                        btnTransaction.visibility = View.GONE
                        btnShowFail.visibility = View.GONE
                        btnSolicitar.visibility = View.GONE
                        btnSolicitado.visibility = View.GONE
                        btnSelectSolicitudes.visibility = View.VISIBLE
                        btnCommentBottom.visibility = View.VISIBLE

                        btnSelectSolicitudes.setOnClickListener {
                            //SolicitarActivityを起動する
                            listener!!.launchSolicitarActivity(solicitud_objects)
                        }

                    }

                    if (btnChoice == BtnChoice.GO_TRANSACTION.name){
                        //"取引画面へ移動するボタン"を表示させる
                        btnTransaction.visibility = View.VISIBLE
                        btnShowFail.visibility = View.GONE
                        btnSolicitar.visibility = View.GONE
                        btnSolicitado.visibility = View.GONE
                        btnSelectSolicitudes.visibility = View.GONE
                        btnCommentBottom.visibility = View.VISIBLE

                        btnTransaction.setOnClickListener {
                            //DirectMessageActivityを起動する
                            listener!!.launchDirectMessageActivity(itemObj)
                        }

                    }

                }

            }

            override fun onFailure(call: Call<ItemDetailSerializerAPIViewModel>, t: Throwable) {
                println("onFailureを通過")
                println(t)
            }


        })


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {

        //DetailActivityでMainActivityに画面遷移させるためのコールバック
        fun onSearchMenuSelected()

        //SolicitarActivityに画面遷移させるためのコールバック
        fun launchSolicitarActivity(solicitud_objects: ArrayList<SolicitudSerializerModel>)

        //DirectMessageActivityに画面遷移させるためのコールバック
        fun launchDirectMessageActivity(itemObj:ItemSerializerModel)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(itemId: String, param2: String) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, itemId)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
