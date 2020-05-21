package com.example.takayama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_direct_message_card.view.*

class MyDirectMessageRecyclerViewAdapter(val dataArrayList: ArrayList<DirectMessageContentSerializerModel>, val listener: DirectMessageFragment.OnFragmentInteractionListener):RecyclerView.Adapter<MyDirectMessageRecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View):RecyclerView.ViewHolder(view){

        val tvMessageUserName: TextView;
        val tvMessageContent: TextView;
        val ivMessageProfileImage: ImageView;

        init {
            tvMessageUserName = view.findViewById(R.id.tvMessageUserName)
            tvMessageContent = view.findViewById(R.id.tvMessageContent)
            ivMessageProfileImage = view.findViewById(R.id.ivMessageProfileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_direct_message_card, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvMessageUserName.text = dataArrayList[position].profile!!.user!!.username
        holder.tvMessageContent.text = dataArrayList[position].content
        val imageUrl = BASE_URL + dataArrayList[position].profile!!.image!!.substring(1)
        //Glide.with(MyApplication.appContext).load(imageUrl).into(holder.view.ivMessageProfileImage)
        Glide.with(MyApplication.appContext).load(imageUrl).circleCrop().into(holder.view.ivMessageProfileImage)


    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }
}