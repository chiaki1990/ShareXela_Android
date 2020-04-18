package com.example.takayama

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_card.view.*


//masterFragment.ktでアイテム一覧リストを表示するためにこのAdapterを使う.
//関連ファイル
//MasterActivity, MasterFragment.kt, fragment_master.xml, MyRecyclerViewAdapter.kt



class MyRecyclerViewAdapter(val dataArrayList:ArrayList<ItemModel>, val myListener: MasterFragment.OnFragmentInteractionListener?):RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {



    class MyViewHolder(val view: View):RecyclerView.ViewHolder(view){
        val itemId   :TextView;
        val itemTitle:TextView;
        val itemImage:ImageView;

        init {
            itemId    = view.tvItemId
            itemTitle = view.tvItemTitle
            itemImage = view.ivItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_card, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //一致させるの


        val item = dataArrayList[position]
        holder.view.tvItemId.text = item.id.toString()
        holder.view.tvItemTitle.text = item.title

        //確認
        println(item.image)
        val imageUrl: String = BASE_URL+item.image!!.substring(1)
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