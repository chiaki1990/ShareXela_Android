package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_master.*
import kotlinx.android.synthetic.main.fragment_search_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class HomeFragment : Fragment() {

    private var itemObjectsSet: ItemHomeListSerializerViewModel? = null
    private var param2: String? = null

    private var listener: OnFragmentInteractionListener? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemObjectsSet = it.getSerializable(ARG_PARAM1) as ItemHomeListSerializerViewModel
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //super.onCreateOptionsMenu(menu, inflater)
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



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //ボタンのリスナーを設定する

        btn500.setOnClickListener { executeGetItemCategoryListAPIView(btn500) }
        btn600.setOnClickListener {  }
        btn700.setOnClickListener {  }
        btn800.setOnClickListener {  }


        //var dataArrayList: ArrayList<ItemSerializerModel> = arrayListOf()

        var itemObjectsCosas = itemObjectsSet!!.ITEM_OBJECTS_COSAS
        var itemObjectsCasas = itemObjectsSet!!.ITEM_OBJECTS_HABITACION
        var itemObjectsTrabajo = itemObjectsSet!!.ITEM_OBJECTS_TRABAJO
        var itemObjectsTienda = itemObjectsSet!!.ITEM_OBJECTS_TIENDA

        //各クエリ結果をArrayListに直す関数を通す

        var dataArrayListCosas = dataArrayListMaker(itemObjectsCosas!!)
        var dataArrayListHabitacion = dataArrayListMaker(itemObjectsCasas!!)
        var dataArrayListTrabajo = dataArrayListMaker(itemObjectsTrabajo!!)
        var dataArrayListTienda = dataArrayListMaker(itemObjectsTienda!!)


        //作成したArrayListをrecyclerviewとadapterにリンクさせる関数に通す
        setUpRecyclerView(recyclerViewHomeCosas, dataArrayListCosas)
        setUpRecyclerView(recyclerViewHomeCasas, dataArrayListHabitacion)
        setUpRecyclerView(recyclerViewHomeTrabajo, dataArrayListTrabajo)
        setUpRecyclerView(recyclerViewHomeEmpresa, dataArrayListTienda)


    }

    interface OnFragmentInteractionListener {
        //fun launchMasterActivity(itemObjectsSerialized:ItemObjectsSerialized, stringItemObjectsCategory: String, localStatus:Boolean)
        fun launchDetailActivity(selectedItem: ItemSerializerModel)

        fun onSearchMenuSelected()

        fun launchMasterFragment(itemObjectsSerialized:ItemObjectsSerialized, stringItemObjectsCategory: String, localStatus:Boolean)
    }



    fun dataArrayListMaker(itemObjects:List<ItemSerializerModel>):ArrayList<ItemSerializerModel>{

        var dataArrayList:ArrayList<ItemSerializerModel> = arrayListOf()

        for (numero in 0..itemObjects!!.size-1){
            //for (numero in 0..item_objects_count){
            //println("番号 : "+ numero)
            //println(itemSerializerList[numero])
            var id    = itemObjects[numero].id
            var title = itemObjects[numero].title
            //var description = itemSerializerList[numero].description
            var categoryNumber = itemObjects[numero].category!!.number
            //var created_at = itemSerializerList[numero].created_at
            var image = itemObjects[numero].image1
            var deadline = itemObjects[numero].deadline

            dataArrayList.add(
                ItemSerializerModel(
                    id       = id,
                    title    = title,
                    //description = description,
                    category = CategorySerializerModel(categoryNumber),
                    //created_at = created_at,
                    image1   = image,
                    deadline = deadline
                )
            )
        }
        return dataArrayList
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView ,dataArrayList:ArrayList<ItemSerializerModel>){

        //リサイクラービューの枠線をつける
        val divider = androidx.recyclerview.widget.DividerItemDecoration(MyApplication.appContext, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
        recyclerView.apply { addItemDecoration(divider) }

        //横向きに並べる
        val layoutManager = LinearLayoutManager(this@HomeFragment.activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); // ここで横方向に設定
        recyclerView.layoutManager = layoutManager

        //アダプターの設定
        val adapter = MyHomeRecyclerViewAdapter(dataArrayList=dataArrayList, myListener=listener)
        recyclerView.adapter = adapter


    }

    private fun executeGetItemCategoryListAPIView(button: Button){

        val rsName = requireContext().getResources().getResourceEntryName(button.id);
        val categoryNumber = rsName.removePrefix("btn")

        println(categoryNumber)

        val service = setService()
        service.getItemCategoryListAPIView(categoryNumber).enqueue(object: Callback<ItemUniversalListAPIView> {

            override fun onResponse(call: Call<ItemUniversalListAPIView>, response: Response<ItemUniversalListAPIView>) {
                println("onResponseを通る_HomeFragment#executeGetItemCategoryListAPIView")
                //クエリ結果を取得し、それを引数としてコールバックする。
                val itemObjects:List<ItemSerializerModel> = response.body()!!.ITEM_OBJECTS
                if (itemObjects.size == 0){
                    makeToast(MyApplication.appContext, getString(R.string.toast_message_no_article))
                    return
                }
                val itemObjectsSerialized:ItemObjectsSerialized = ItemObjectsSerialized(itemObjects=itemObjects)
                val localStatus = false
                listener!!.launchMasterFragment(itemObjectsSerialized, categoryNumber, localStatus)


            }

            override fun onFailure(call: Call<ItemUniversalListAPIView>, t: Throwable) {
                println("onFailureを通る_SearchMenuFragment#executeGetItemCategoryListAPIView")
                println(t)
            }
        })
    }




    companion object {


        @JvmStatic
        fun newInstance(itemObjectsSet: ItemHomeListSerializerViewModel, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemObjectsSet)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}