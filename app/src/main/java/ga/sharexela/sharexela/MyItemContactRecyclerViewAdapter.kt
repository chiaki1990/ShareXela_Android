package ga.sharexela.sharexela

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.my_recyclerview_direct_message_card.view.*

class MyItemContactRecyclerViewAdapter(val itemContactObjects:ArrayList<ItemContactSerializerModel>):RecyclerView.Adapter<MyItemContactRecyclerViewAdapter.MyViewHolder>() {

    class MyViewHolder(val v: View):RecyclerView.ViewHolder(v){

        val tvMessageUserName: TextView;
        val tvMessageContent: TextView;
        val ivMessageProfileImage: ImageView;

        init {
            tvMessageUserName = v.findViewById(R.id.tvMessageUserName)
            tvMessageContent  = v.findViewById(R.id.tvMessageContent)
            ivMessageProfileImage = v.findViewById(R.id.ivMessageProfileImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_direct_message_card, parent, false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvMessageUserName.text = itemContactObjects[position].post_user!!.user!!.username
        holder.tvMessageContent.text  = itemContactObjects[position].message
        val imageUrl = BASE_URL + itemContactObjects[position].post_user!!.image!!.substring(1)
        //Glide.with(MyApplication.appContext).load(imageUrl).into(holder.v.ivMessageProfileImage)
        Glide.with(MyApplication.appContext).load(imageUrl).circleCrop().into(holder.v.ivMessageProfileImage)

    }


    override fun getItemCount(): Int {
        return itemContactObjects.size
    }


}