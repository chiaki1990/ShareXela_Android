package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_search_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



//このSearchを追加したことでMainActivity, MainFragment, activity_main.xml, app_bar_main.xml, content_main.xml, fragment_main.xml, nav_header_master.xml
//これらを消去する。。。



class SearchMenuFragment : Fragment() {
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_search_menu, container, false)
        setHasOptionsMenu(true)
        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




        //Buttonテキスト設定
        setButtons()

        //Admob設定
        setUpAdmob()

        //各buttonのリスナーをセットする
        btn1.setOnClickListener { executeGetItemCategoryListAPIView(btn1) }
        btn1Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn1)
        }

        btn2.setOnClickListener { executeGetItemCategoryListAPIView(btn2) }
        btn2Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn2)
        }

        btn3.setOnClickListener { executeGetItemCategoryListAPIView(btn3) }
        btn3Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn3)
        }

        btn4.setOnClickListener { executeGetItemCategoryListAPIView(btn4) }
        btn4Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn4Local)
        }

        btn5.setOnClickListener { executeGetItemCategoryListAPIView(btn5) }
        btn5Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn5Local)
        }

        btn6.setOnClickListener { executeGetItemCategoryListAPIView(btn6) }
        btn6Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn6Local)
        }

        btn7.setOnClickListener { executeGetItemCategoryListAPIView(btn7) }
        btn7Local.setOnClickListener {
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn7Local)
        }

        btn8.setOnClickListener { executeGetItemCategoryListAPIView(btn8) }
        btn8Local.setOnClickListener{
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn8Local)
        }

        btn9.setOnClickListener { executeGetItemCategoryListAPIView(btn9) }
        btn9Local.setOnClickListener{
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn9Local)
        }

        btn10.setOnClickListener { executeGetItemCategoryListAPIView(btn10) }
        btn10Local.setOnClickListener{
            if (sessionData.logInStatus == false) return@setOnClickListener makeToast(MyApplication.appContext, getString(R.string.toast_message_needSignIn))
            executeGetItemCategoryLocalListAPIView(btn10Local)
        }
    }




    private fun setUpAdmob() {
        val adRequest = AdRequest.Builder().build()
        //サイズ
        val adViewBottom = AdView(MyApplication.appContext)
        adViewBottom.setAdSize(AdSize.SMART_BANNER)

        //unitid
        if (devEnv == true){
            adViewBottom.setAdUnitId(getString(R.string.banner_ad_unit_id_test))
        }else if (devEnv == false){
            adViewBottom.setAdUnitId(getString(R.string.banner_ad_unit_id))
        }
        adViewBottom.loadAd(adRequest)

        linearLayoutMasterBottom.addView(adViewBottom)

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
        fun launchMasterActivity(itemObjectsSerialized:ItemObjectsSerialized, stringItemObjectsCategory: String, localStatus:Boolean)

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun executeGetItemCategoryListAPIView(selectedButton:Button){

        val textBtn: String = selectedButton.text.toString().toLowerCase()
        println(textBtn)
        val categoryList = MyApplication.appContext.resources.getStringArray(R.array.categoryListForCategoryNumber)
        var categoryDisplayList = arrayListOf<String>()
        for (categoty in categoryList){
            val display = categoty.split(":")[1]
            categoryDisplayList.add(display)
        }
        println(categoryDisplayList)
        val categoryNumber = (categoryDisplayList.indexOf(textBtn) + 1).toString()

        println(categoryNumber)

        val service = setService()
        service.getItemCategoryListAPIView(categoryNumber).enqueue(object: Callback<ItemUniversalListAPIView>{
            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#executeGetItemCategoryListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。
                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val localStatus = false
                listener!!.launchMasterActivity(itemObjectsSerialized, categoryNumber, localStatus)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#executeGetItemCategoryListAPIView")
                println(t)
            }
        })
    }



    private fun executeGetItemCategoryLocalListAPIView(selectedButton:Button){

        val textBtn: String = selectedButton.text.toString().toLowerCase()
        println(textBtn)
        val categoryList = MyApplication.appContext.resources.getStringArray(R.array.categoryListForCategoryNumber)
        var categoryDisplayList = arrayListOf<String>()
        for (categoty in categoryList){
            val display = categoty.split(":")[1]
            categoryDisplayList.add(display)
        }
        println(categoryDisplayList)
        val categoryNumber = (categoryDisplayList.indexOf(textBtn) + 1).toString()

        println(categoryNumber)

        val service = setService()
        service.getItemCategoryLocalListAPIView(sessionData.authTokenHeader!! ,categoryNumber).enqueue(object: Callback<ItemUniversalListAPIView>{
            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_SearchMenuFragment#executeGetItemCategoryListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。
                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)

                val localStatus = true
                listener!!.launchMasterActivity(itemObjectsSerialized, categoryNumber, localStatus)
            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#executeGetItemCategoryListAPIView")
                println(t)
            }
        })
    }

    fun setButtons(){
        //フラグメントのボタンの内容を設定する
        setCategoryTextToButton(btn1)
        setCategoryTextToButton(btn1Local)
        setCategoryTextToButton(btn2)
        setCategoryTextToButton(btn2Local)
        setCategoryTextToButton(btn3)
        setCategoryTextToButton(btn3Local)
        setCategoryTextToButton(btn4)
        setCategoryTextToButton(btn4Local)
        setCategoryTextToButton(btn5)
        setCategoryTextToButton(btn5Local)
        setCategoryTextToButton(btn6)
        setCategoryTextToButton(btn6Local)
        setCategoryTextToButton(btn7)
        setCategoryTextToButton(btn7Local)
        setCategoryTextToButton(btn8)
        setCategoryTextToButton(btn8Local)
        setCategoryTextToButton(btn9)
        setCategoryTextToButton(btn9Local)
        setCategoryTextToButton(btn10)
        setCategoryTextToButton(btn10Local)
    }

}





fun setCategoryTextToButton(button:Button){
    val buttonCategoryNumber = button.text
    val categoryDisplay = categoryDisplayMaker(buttonCategoryNumber.toString())
    button.text = categoryDisplay
}