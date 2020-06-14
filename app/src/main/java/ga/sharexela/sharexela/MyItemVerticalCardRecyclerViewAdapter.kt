package ga.sharexela.sharexela

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_vertical_card.view.*


class MyItemVerticalCardRecyclerViewAdapter(val dataArrayList:ArrayList<ItemSerializerModel>, val myListener: MyListFragment.OnFragmentInteractionListener?, val favListener: FavoriteItemFragment.OnFragmentInteractionListener?) : RecyclerView.Adapter<MyItemVerticalCardRecyclerViewAdapter.MyListViewHolder>(){

    class MyListViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val itemId   : TextView;
        val itemTitle: TextView;
        val itemImage: ImageView;
        val itemDescription: TextView;
        val itemCategory: TextView;
        val itemPrice: TextView;

        init {
            itemId          = view.tvItemIdVerticalCard
            itemTitle       = view.tvItemTitleVerticalCard
            itemImage       = view.ivItemImageVerticalCard
            itemDescription = view.tvItemDescriptionVerticalCard
            itemCategory    = view.tvItemCategoryVerticalCard
            itemPrice       = view.tvItemPriceVerticalCard
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_vertical_card, parent, false)
        return MyListViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyListViewHolder, position: Int) {


        val item = dataArrayList[position]
        holder.view.tvItemIdVerticalCard.text = item.id.toString()
        holder.view.tvItemTitleVerticalCard.text = item.title
        if (item.description.length >=100) holder.view.tvItemDescriptionVerticalCard.text = item.description.substring(0,100)
        else holder.view.tvItemDescriptionVerticalCard.text = item.description
        holder.view.tvItemPriceVerticalCard.text = item.price.toString()


        //カテゴリーのリストを表示する
        holder.view.tvItemCategoryVerticalCard.text = categoryDisplayMaker(item.category!!.number)


        val imageUrl = BASE_URL + item.image1!!.substring(1)
        Glide.with(MyApplication.appContext).load(imageUrl).into(holder.view.ivItemImageVerticalCard)


        //リスナーセット
        holder.view.setOnClickListener {
            println("setOnClickListenerは使えているか？？")
            if (myListener != null) myListener.launchDetailActivity(item)
            else if (favListener != null) favListener.launchDetailActivity(item)

        }

    }

    override fun getItemCount(): Int {
        return  dataArrayList.size
    }

}