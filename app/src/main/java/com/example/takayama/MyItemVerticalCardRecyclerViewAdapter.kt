package com.example.takayama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_vertical_card.view.*


class MyItemVerticalCardRecyclerViewAdapter(val dataArrayList:ArrayList<ItemSerializerModel>, val myListener: FavoriteItemFragment.OnFragmentInteractionListener?) : RecyclerView.Adapter<MyItemVerticalCardRecyclerViewAdapter.MyListViewHolder>(){

    class MyListViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val itemId   : TextView;
        val itemTitle: TextView;
        val itemImage: ImageView;
        val itemDescription: TextView;

        init {
            itemId          = view.tvItemIdVerticalCard
            itemTitle       = view.tvItemTitleVerticalCard
            itemImage       = view.ivItemImageVerticalCard
            itemDescription = view.tvItemDescriptionVerticalCard
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
        holder.view.tvItemDescriptionVerticalCard.text = item.description
        val imageUrl = BASE_URL + item.image1!!.substring(1)
        Glide.with(MyApplication.appContext).load(imageUrl).into(holder.view.ivItemImageVerticalCard)


        //リスナーセット
        holder.view.setOnClickListener {
            println("setOnClickListenerは使えているか？？")

            myListener!!.launchDetailActivity(item)
        }

    }

    override fun getItemCount(): Int {
        return  dataArrayList.size
    }

}