package ga.sharexela.sharexela

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.tvItemTitle
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




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



class DetailFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var itemId: String = ""
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var authTokenHeader: String? = null
    var btnFavColor:String = "WHITE"

    lateinit var itemObj: ItemSerializerModel
    lateinit var solicitud_objects: ArrayList<SolicitudSerializerModel>
    lateinit var favorite_users: ArrayList<UserSerializerModel>

    //カルーセル用
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
            findItem(R.id.menuSync).isVisible = false
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

        //Favボタンのリスナー設置
        btnDetailFavorite.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener
            onClickFavBtn()
        }

        //記事編集ボタンのリスナー設置
        btnDetailEditarArticulo.setOnClickListener {
            if (sessionData.profileObj!!.user!!.username != itemObj.user!!.username!!) return@setOnClickListener
            listener!!.launchEditarFragment(itemObj)
        }

        //記事のアクティブステータスを変更するリスナーを設置
        btnDetailActive.setOnClickListener {
            //println("反応すれば良い")
            changeActiveStatus()
        }

        //adMobの設定(unitidとサイズをdev,pro環境で変更する)
        setUpAdmob()

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
                    val itemPoint: String? = itemObj.point
                    val itemRadius: Int? = itemObj.radius
                    val itemPrice: Int? = itemObj.price



                    //記事作成者Profileデータ

                    val postUserProfile = response.body()!!.profile_obj_serializer
                    val postUserUsername = postUserProfile.user!!.username

                    val postUserFeedbackPoints = getFeedbackTotalPoints(postUserProfile)//関数
                    val postUserFeedbackAve = getFeedbackAve(postUserProfile)//関数
                    val postUserFeedbackFormat = "$postUserFeedbackPoints promedio $postUserFeedbackAve"


                    val postUserProfileImage = postUserProfile.image
                    val postUserProfileImageUrl = BASE_URL + postUserProfileImage!!.substring(1)



                    val itemCategoryNumber = itemObj.category!!.number
                    val itemCategoryDisplay = categoryDisplayMaker(itemCategoryNumber)

                    //val ItemContactObjects = response.body()!!.item_contact_objects_serializer
                    val ItemContactObjects = itemObj.item_contacts
                    println("itemContact   " + ItemContactObjects)

                    solicitud_objects = itemObj.solicitudes!!

                    val btnChoice = response.body()!!.BTN_CHOICE
                    println("BTNcHOICE  : "+btnChoice)


                    //画面にデータを描画する
                    tvItemTitle.text = itemTitle
                    tvItemDescription.text = itemDescription
                    tvCreatedAt.text = strDate
                    tvCategoryContent.text = itemCategoryDisplay
                    tvDealAreaAdm1.text = itemAdm1
                    tvDealAreaAdm2.text = itemAdm2
                    tvDetailPrice.text = itemPrice.toString()


                    //GoogleMapsを描画する
                    if (itemObj.point != null) {
                        val map = SupportMapFragment.newInstance()
                        childFragmentManager.beginTransaction().add(R.id.frameLayoutAreaMap, map)
                            .commit()
                        map.getMapAsync(this@DetailFragment)
                    }





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


                    //btnDetailFavを押してるなら赤色に変更する
                    favSetting()

                    //記事作成者以外のアクセスの場合には記事編集ボタンを非表示にする/作成者の場合には記事の変更表示を変更する
                    if (sessionData.logInStatus == false ) btnDetailEditarArticulo.visibility = View.GONE
                    else if (sessionData.profileObj!!.user!!.username != itemObj.user!!.username) btnDetailEditarArticulo.visibility = View.GONE





                    //記事作成者以外のアクセスの場合にはアクティブを変更するボタンを非表示にする
                    if (sessionData.logInStatus == false ) btnDetailActive.visibility = View.GONE
                    else if (sessionData.profileObj!!.user!!.username != itemObj.user!!.username) btnDetailActive.visibility = View.GONE
                    else if (sessionData.profileObj!!.user!!.username == itemObj.user!!.username){
                        if (itemObj.active == true) btnDetailActive.text = getString(R.string.fragment_detail_btn_detail_active_to_deactive) //"この記事の公開を中止する"
                        if (itemObj.active == false) btnDetailActive.text = getString(R.string.fragment_detail_btn_detail_active_to_active)  //"この記事の公開を再開する"
                    }

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

                    //プログレスバーの解除
                    progressBarDetail.visibility = View.GONE


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


    private fun setUpAdmob(){
        //unitidとサイズをdev,pro環境で変更する

        val adRequest = AdRequest.Builder().build()
        //サイズ
        val adViewCenter = AdView(MyApplication.appContext)
        val adViewBottom = AdView(MyApplication.appContext)
        adViewCenter.setAdSize(AdSize.MEDIUM_RECTANGLE)
        adViewBottom.setAdSize(AdSize.SMART_BANNER)

        //unitid
        if (devEnv == true){
            adViewCenter.setAdUnitId(getString(R.string.banner_ad_unit_id_test))
            adViewBottom.setAdUnitId(getString(R.string.banner_ad_unit_id_test))
        }else if (devEnv == false){
            adViewCenter.setAdUnitId(getString(R.string.banner_ad_unit_id))
            adViewBottom.setAdUnitId(getString(R.string.banner_ad_unit_id))
        }
        adViewCenter.loadAd(adRequest)
        adViewBottom.loadAd(adRequest)


        linearLayoutBottomLayer.addView(adViewBottom)

        linearLayoutCenterForAd.addView(adViewCenter)
        val spaceView = View(MyApplication.appContext)
        spaceView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 15)
        linearLayoutCenterForAd.addView(spaceView)

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

        // SolicitarMessageMakingFragmentを起動させるためのコールバック
        fun launchSolicitarMessageMakingFragment(itemObj:ItemSerializerModel, tag: String)

        // EditarArticuloFragmentを起動させるためのコールバック
        fun launchEditarFragment(itemObj: ItemSerializerModel)

        //fun successPatchArticulo()

    }

    companion object {

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

        if (sessionData.logInStatus == false) return

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
        //btnCommentBottom.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        //btnComment.setOnClickListener { listener!!.launchItemContactActivity(itemObj) }
        btnCommentBottom.setOnClickListener { makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn)) }
        btnComment.setOnClickListener { makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn)) }

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

        btnSolicitado.setOnClickListener { makeToast(MyApplication.appContext, getString(R.string.fragment_detail_toast_message_applicado)) }

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


    private fun changeActiveStatus(){
        val activeStatus = itemObj.active!!
        val activateTo: Boolean = !activeStatus
        val itemObjId = itemObj.id

        val itemObjData = ItemSerializerModel(id=itemObjId,title=itemObj.title, description=itemObj.description, active=activateTo)
        val part1: MultipartBody.Part? = null
        val part2: MultipartBody.Part? = null
        val part3: MultipartBody.Part? = null
        val reqBody : RequestBody = RequestBody.create(MediaType.parse("application/json"), Gson().toJson(itemObjData))

        val service = setService()
        service.patchItemDetailSerializerAPIView(itemId, sessionData.authTokenHeader!! ,part1, part2, part3, reqBody).enqueue(object :Callback<ResultModel>{

            override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                println("onResponseを通る : CrearArticuloFragment#postItemCreateAPIViewMultiPart")
                println(call.request().body())

                //更新する
                this@DetailFragment.onResume()

            }

            override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                println("onFailureを通る : EditarArticuloFragment#patchItemDetailSerializerAPIViewMultiPart")
                println(call.request().body())
                println(t)
                println(t.message)
            }
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        //描画したいこと mapの縮尺, mapの中心設定, pointまたはradiusの描画
        //mapの縮尺
        if (itemObj.point == null) return
        val latLng = getLatLng(itemObj.point!!)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true


        if (itemObj.radius != 0){
            drawingCircle(latLng, itemObj.radius.toString(), googleMap)
        }else if (itemObj.radius == 0){
            drawingPoint(latLng, googleMap)
        }
    }


}
