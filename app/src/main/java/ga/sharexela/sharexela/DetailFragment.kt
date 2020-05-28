package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.tvItemTitle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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



/*
BTN_CHOICEの種類

BtnChoice.ANONYMOUS_USER_ACCESS 済
BtnChoice.NO_SOLICITUDES 済
BtnChoice.SELECT_SOLICITUDES 済
BtnChoice.GO_TRANSACTION 済

BtnChoice.SOLICITAR 済
BtnChoice.SOLICITADO 済
BtnChoice.GO_TRANSACTION 済
BtnChoice.CANNOT_TRANSACTION


*/



class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemId: String = ""
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var authTokenHeader: String? = null
    var btnFavColor:String = "WHITE"

    lateinit var itemObj: ItemSerializerModel
    lateinit var solicitud_objects: ArrayList<SolicitudSerializerModel>
    lateinit var favorite_users: ArrayList<UserSerializerModel>

    //カルーセルテスト
    lateinit var imageUrls:ArrayList<String>




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

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = true
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false
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


        btnDetailFavorite.setOnClickListener {
            onClickFavBtn()
        }

    }


    override fun onResume() {
        super.onResume()

        //サーバーとやり取りして画面を描画する
        //itemIdを起点にItemオブジェクトデータを取得する

        val service = setService()
        service.getItemDetailSerializerAPIView(itemId, sessionData.authTokenHeader).enqueue(object :Callback<ItemDetailSerializerAPIViewModel>{
            override fun onResponse(call: Call<ItemDetailSerializerAPIViewModel>, response: Response<ItemDetailSerializerAPIViewModel>) {

                println("onResponseを通過")
                println(response.isSuccessful)
                println(response.message())
                if (response.isSuccessful){
                    //val itemId = response.body()!!.item_obj_serializer.id
                    itemObj = response.body()!!.item_obj_serializer
                    val itemTitle = itemObj.title
                    val itemDescription = itemObj.description
                    val itemCreatedAt = itemObj.created_at
                    val d = dateFormat.parse(itemCreatedAt)
                    val strDate = showDateFormat.format(d)

                    favorite_users = itemObj.favorite_users!!

                    //価格は後で実装する
                    val itemImage1: String? = itemObj.image1
                    val itemImage2: String? = itemObj.image2
                    val itemImage3: String? = itemObj.image3
                    val itemAdm0 = itemObj.adm0
                    val itemAdm1 = itemObj.adm1
                    val itemAdm2 = itemObj.adm2



                    //記事作成者Profileデータ

                    val postUserProfile = response.body()!!.profile_obj_serializer
                    val postUserUsername = postUserProfile.user!!.username

                    val postUserFeedbackPoints = getFeedbackTotalPoints(postUserProfile)//関数
                    val postUserFeedbackAve = getFeedbackAve(postUserProfile)//関数
                    val postUserFeedbackFormat = "$postUserFeedbackPoints 平均 $postUserFeedbackAve"


                    val postUserProfileImage = postUserProfile.image
                    val postUserProfileImageUrl = BASE_URL + postUserProfileImage!!.substring(1)



                    //pointは今後実装する
                    //val itemPoint = response.body()!!.item_obj_serializer.

                    val itemCategory = itemObj!!.category!!.name

                    //val ItemContactObjects = response.body()!!.item_contact_objects_serializer
                    val ItemContactObjects = itemObj.item_contacts
                    println("itemContact   " + ItemContactObjects)

                    solicitud_objects = itemObj.solicitudes!!

                    val btnChoice = response.body()!!.BTN_CHOICE
                    println("BTNcHOICE  : "+btnChoice)


                    //画面にデータを描画する
                    tvItemTitle.text = itemTitle
                    tvItemDescription.text = itemDescription
                    //tvCreatedAt.text = itemCreatedAt
                    tvCreatedAt.text = strDate
                    tvCategoryContent.text = itemCategory
                    tvDealAreaAdm1.text = itemAdm1
                    tvDealAreaAdm2.text = itemAdm2

                    /*
                    val imageUrl = BASE_URL + itemImage1!!.substring(1)
                    println(imageUrl)
                    Glide.with(MyApplication.appContext).load(Uri.parse(imageUrl)).into(ivItemDetail)
                    */



                    //カルーセル実装テスト





                    imageUrls = ArrayList()
                    if (itemImage1 != null)  imageUrls.add(BASE_URL + itemImage1.substring(1))
                    if (itemImage2 != null)  imageUrls.add(BASE_URL + itemImage2.substring(1))
                    if (itemImage3 != null)  imageUrls.add(BASE_URL + itemImage3.substring(1))


                    val carouselView = activity!!.findViewById(R.id.carouselView) as CarouselView;

                    carouselView.setImageListener(object : ImageListener {

                        override fun setImageForPosition(position: Int, imageView: ImageView) {
                            // You can use Glide or Picasso here
                            //imageView.setImageResource(sampleImages[position])
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            GlideApp.with(MyApplication.appContext).load(imageUrls[position]).centerInside().into(imageView)
                        }
                    });
                    carouselView.setPageCount(imageUrls.size);

                    /*
                    var imageListener: ImageListener = object : ImageListener {
                        override fun setImageForPosition(position: Int, imageView: ImageView) {
                            // You can use Glide or Picasso here
                            //imageView.setImageResource(sampleImages[position])
                            Glide.with(MyApplication.appContext).load(imageUrls[position]).into(imageView)
                        }
                    }

                     */






                    //btnDetailFavを押してるなら赤色に変更する
                    favSetting()


                    //postUserのデータを表示
                    tvDetailUsername.text = postUserUsername
                    tvDetailFeedback.text = postUserFeedbackFormat
                    //Glide.with(MyApplication.appContext).load(postUserProfileImageUrl).into(tvDetailUserProfileImage)
                    Glide.with(MyApplication.appContext).load(postUserProfileImageUrl).circleCrop().into(tvDetailUserProfileImage)

                    //ItemContactのカウントを表示する
                    if (ItemContactObjects!!.size != 0) {
                        tvDetailCommentCount.text = ItemContactObjects.size.toString()
                    }

                    //ItemContactObjectsの表示
                    if (ItemContactObjects.size != 0){

                        //新しいものから上に表示
                        for (numero in 0..ItemContactObjects.size-1){
                            when (numero){
                                0 -> {
                                    tvItemContact0.text = ItemContactObjects[0].message
                                    tvItemContactUser0.text = ItemContactObjects[0].post_user!!.user!!.username
                                }
                                1 -> {
                                    tvItemContact1.text = ItemContactObjects[1].message
                                    tvItemContactUser1.text = ItemContactObjects[1].post_user!!.user!!.username
                                }
                                2 -> {
                                    tvItemContact2.text = ItemContactObjects[2].message
                                    tvItemContactUser1.text = ItemContactObjects[1].post_user!!.user!!.username
                                }
                                3 -> {
                                    tvItemContact3.text = ItemContactObjects[3].message
                                    tvItemContactUser3.text = ItemContactObjects[3].post_user!!.user!!.username
                                }
                                4 -> {
                                    tvItemContact4.text = ItemContactObjects[4].message
                                    tvItemContactUser4.text = ItemContactObjects[4].post_user!!.user!!.username
                                }
                            }
                        }
                    }


                    //btnChoiceによって下部バーボタンの内容を表示する
                    if (btnChoice == BtnChoice.ANONYMOUS_USER_ACCESS.name){
                        //描画内容 -> "申請する"を表示する
                        //コメントは表示のみ行う
                        showForANONYMOUS_USER_ACCESS()

                    }
                    //ユーザーが出品者以外である場合&&取引相手がまだ決まっていない場合&&未申請
                    if (btnChoice == BtnChoice.SOLICITAR.name){
                        //描画内容 -> "申請する"を表示する
                        showForSOLICITAR()

                    }

                    //ユーザーが出品者以外である場合&&取引相手がまだ決まっていない場合&&申請済み
                    if (btnChoice == BtnChoice.SOLICITADO.name){

                        //描画内容 -> "申請する"を表示するがボタン押せない(申請済みだから)
                        showForSOLICITADO()

                    }

                    //ユーザー認証され、ユーザーが出品者以外の場合 && 取引相手が他人に決まっている場合
                    if (btnChoice == BtnChoice.CANNOT_TRANSACTION.name){


                        //描画内容 -> "申請する"を表示するがボタン押せない(申請済みだから)
                        showForCANNOT_TRANSACTION()

                    }

                    //ユーザーが出品者である場合&&申請者が現れていない場合
                    if (btnChoice == BtnChoice.NO_SOLICITUDES.name){
                        //"コメント"を表示させる
                        showForNO_SOLICITUDES()

                    }

                    //ユーザーが出品者である場合&&申請者が現われ、申請者を選ぶ場合
                    if (btnChoice == BtnChoice.SELECT_SOLICITUDES.name){
                        //"申請者を選ぶボタン"を表示させる
                        showForSELECT_SOLICITUDES()


                    }

                    if (btnChoice == BtnChoice.GO_TRANSACTION.name){
                        //"取引画面へ移動するボタン"を表示させる

                        showForGO_TRANSACTION()

                    }

                }

            }

            override fun onFailure(call: Call<ItemDetailSerializerAPIViewModel>, t: Throwable) {
                println("onFailureを通過")
                println(t)
            }
        })
    }







    private fun getFeedbackAve(postUserProfile: ProfileSerializerModel): String {
        var points: Int = 0
        val feedbackList = postUserProfile.feedback
        if (feedbackList!!.size == 0) return "0.0"
        for (ele in feedbackList){
            points += ele.level!!
        }
        var ave = points/feedbackList!!.size
        return ave.toString()



    }

    private fun getFeedbackTotalPoints(postUserProfile: ProfileSerializerModel): String {
        var points: Int = 0
        val feedbackList = postUserProfile.feedback
        if (feedbackList!!.size == 0) return points.toString()
        for (ele in feedbackList){
            points += ele.level!!
        }
        return points.toString()
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

        //DetailActivityでMainActivityに画面遷移させるためのコールバック
        fun onSearchMenuSelected()

        //SolicitarActivityに画面遷移させるためのコールバック
        fun launchSolicitarActivity(solicitud_objects: ArrayList<SolicitudSerializerModel>, tag: String)

        //DirectMessageActivityに画面遷移させるためのコールバック
        fun launchDirectMessageActivity(itemObj:ItemSerializerModel)

        // ItemContactActivityに画面遷移させるためのコールバック
        fun launchItemContactActivity(itemObj: ItemSerializerModel)

        //fun launchSolicitar
        fun launchSolicitarMessageMakingFragment(itemObj:ItemSerializerModel, tag: String)
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


    private fun favSetting(){
        //favのカウントを表示
        if (favorite_users.size == 0) return
        tvDetailFavCount.text = favorite_users.size.toString()
        // favボタンの色を変更するか判断する
        for (num in 0..favorite_users!!.size-1){

            if (favorite_users[num].email == sessionData.profileObj!!.user!!.email ){
                //自分のメールアドレスが含まれていれば赤色のハートに変更する
                btnDetailFavorite.setImageResource(R.drawable.ic_favorite_black_36dp)
                btnFavColor = "RED"
            }
        }
    }


    //btnDetailFavoriteを押したらサーバーに送信する -> 色変更, カウント変更, サーバーデータ変更
    private fun onClickFavBtn(){
        if (btnFavColor == "RED" ){
            btnDetailFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp)
            if (favorite_users.size != 0){
                tvDetailFavCount.text = (favorite_users.size -1).toString()
            }
            btnFavColor = "WHITE"

        }else if(btnFavColor == "WHITE"){
            btnDetailFavorite.setImageResource(R.drawable.ic_favorite_black_36dp)
            tvDetailFavCount.text = (favorite_users.size +1).toString()
            btnFavColor = "RED"
        }


        //データの送信
        val service = setService()
        service.patchItemFavoriteAPIView(itemObj.id.toString(), sessionData.authTokenHeader).enqueue(object:Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("onResponseを通る")
                //特にやることはないと思われる。
            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
                println(t.message)
                //変更したカラーをもとに戻す
                if (btnFavColor == "RED" ){
                    btnDetailFavorite.setImageResource(R.drawable.ic_favorite_border_black_36dp)
                    tvDetailFavCount.text = (favorite_users.size -1).toString()
                    btnFavColor = "WHITE"


                }else if(btnFavColor == "WHITE"){
                    btnDetailFavorite.setImageResource(R.drawable.ic_favorite_black_36dp)
                    tvDetailFavCount.text = (favorite_users.size +1).toString()
                    btnFavColor = "RED"
                }
            }

        })


    }



    private fun showForANONYMOUS_USER_ACCESS(){

        //描画内容 -> "申請する"を表示する
        //コメントは表示のみ行う
        btnTransaction.visibility = View.GONE
        //ここはアイテムの状態によって表示する内容を変えるように改善の余地あり。
        btnShowFail.visibility = View.GONE
        btnSolicitado.visibility = View.GONE
        btnSelectSolicitudes.visibility = View.GONE

        //コメントを一覧するビューを作成するそして、その画面へ遷移させる
        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }

        //申請するボタンを押すとwarningをToastで表示する
        btnSolicitar.setOnClickListener { makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn)) }
    }


    private fun showForSOLICITAR(){

        //描画内容 -> "申請する"を表示する
        btnTransaction.visibility = View.GONE
        btnShowFail.visibility = View.GONE
        btnSolicitado.visibility = View.GONE
        btnSelectSolicitudes.visibility = View.GONE
        btnSolicitar.visibility = View.VISIBLE
        btnCommentBottom.visibility = View.VISIBLE

        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }

        //申請するボタンを押すと、申請メッセージフォーム画面遷移する。
        btnSolicitar.setOnClickListener{
            //要するにsolicitudオブジェクトを生成するAPIを叩き画面遷移する。

            //以下の引数が異なる。これは各アイテムのオブジェクトIDに対応するものだから。
            listener!!.launchSolicitarMessageMakingFragment(itemObj, getString(R.string.fragment_tag_make_solicitud_message))

        }
    }


    private fun showForSOLICITADO(){
        //描画内容 -> "申請する"を表示するがボタン押せない(申請済みだから)

        btnTransaction.visibility = View.GONE
        btnShowFail.visibility = View.GONE
        btnSolicitado.visibility = View.VISIBLE
        btnSelectSolicitudes.visibility = View.GONE
        btnSolicitar.visibility = View.GONE
        btnCommentBottom.visibility = View.VISIBLE

        btnSolicitado.setOnClickListener { makeToast(MyApplication.appContext, "すでに申請済みです。") }

        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
    }


    private fun showForCANNOT_TRANSACTION(){

        //描画内容 -> "申請する"を表示するがボタン押せない(申請済みだから)
        btnTransaction.visibility = View.GONE
        btnShowFail.visibility = View.VISIBLE
        btnSolicitado.visibility = View.GONE
        btnSelectSolicitudes.visibility = View.GONE
        btnSolicitar.visibility = View.GONE
        btnCommentBottom.visibility = View.VISIBLE

        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
    }

    private fun showForNO_SOLICITUDES(){
        //"コメント"を表示させる
        btnTransaction.visibility = View.GONE
        btnShowFail.visibility = View.GONE
        btnSolicitar.visibility = View.GONE
        btnSolicitado.visibility = View.GONE
        btnSelectSolicitudes.visibility = View.VISIBLE
        btnSelectSolicitudes.isEnabled = false
        btnCommentBottom.visibility = View.VISIBLE

        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }

    }

    private fun showForSELECT_SOLICITUDES(){
        //"申請者を選ぶボタン"を表示させる


        btnTransaction.visibility = View.GONE
        btnShowFail.visibility = View.GONE
        btnSolicitar.visibility = View.GONE
        btnSolicitado.visibility = View.GONE
        btnSelectSolicitudes.visibility = View.VISIBLE
        btnCommentBottom.visibility = View.VISIBLE

        btnSelectSolicitudes.setOnClickListener {
            //SolicitarActivityを起動する
            listener!!.launchSolicitarActivity(solicitud_objects, getString(R.string.fragment_tag_choose_solicitud))
        }
    }

    private fun showForGO_TRANSACTION(){
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

        btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
    }



}
