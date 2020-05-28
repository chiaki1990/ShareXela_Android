package ga.sharexela.sharexela

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_card.view.*


//どこで使ってるのかわかってないのでメモしておく必要あり。
//MyListを表示するために使う。
//MyListRecyclerViewAdapter.kt, MyListFragment.kt, fragment_my_list.xml,


class MyListRecyclerViewAdapter(val dataArrayList:ArrayList<ItemSerializerModel>, val myListener: MyListFragment.OnFragmentInteractionListener?):RecyclerView.Adapter<MyListRecyclerViewAdapter.MyListViewHolder>() {

    class MyListViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val itemId   : TextView;
        val itemTitle: TextView;
        val itemImage: ImageView;

        init {
            itemId    = view.tvItemId
            itemTitle = view.tvItemTitle
            itemImage = view.ivItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_card, parent, false)
        return MyListViewHolder(v)
    }



    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {
        //一致させるの

        val item = dataArrayList[position]
        holder.view.tvItemId.text = item.id.toString()
        holder.view.tvItemTitle.text = item.title

        //確認
        println(item.image1)
        val imageUrl: String = BASE_URL+item.image1!!.substring(1)
        Glide.with(MyApplication.appContext).load(imageUrl).into(holder.view.ivItem)


        //リスナーセット
        holder.view.setOnClickListener {
            println("setOnClickListenerは使えているか？？")

            myListener!!.launchDetailActivity(item)
        }


    }


    override fun getItemCount(): Int {
        return  dataArrayList.size
    }



    /*
    interface OnMyRecyclerViewClickListener{
        fun onSelectedItem(selectedItem: ItemModel)
    }


     */












}