package ga.sharexela.sharexela

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
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



class MyRecyclerViewAdapter(val dataArrayList:ArrayList<ItemSerializerModel>, val myListener: MasterFragment.OnFragmentInteractionListener?):RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {

    lateinit var filter:ColorMatrixColorFilter;

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
        //Grey scale
        val colorMatrix =  ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        filter =  ColorMatrixColorFilter(colorMatrix);


        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        //Grey scale
        //val colorMatrix =  ColorMatrix();
        //colorMatrix.setSaturation(0.0f);
        //val filter =  ColorMatrixColorFilter(colorMatrix);


        //一致させるの


        val item = dataArrayList[position]
        holder.view.tvItemId.text = item.id.toString()
        holder.view.tvItemTitle.text = item.title



        //確認
        val imageUrl: String = BASE_URL+item.image1!!.substring(1)
        //Glide.with(MyApplication.appContext).load(imageUrl).into(holder.view.ivItem)

        Glide.with(MyApplication.appContext).load(imageUrl)
            .override(720, 720).centerCrop().into(holder.view.ivItem)


        if (item.deadline == false){
            holder.view.tvItemTitle.typeface = Typeface.DEFAULT_BOLD
            holder.view.tvItemTitle.setTextColor(Color.BLACK)
        }
        else if(item.deadline == true){
            holder.view.ivItem.colorFilter = filter
        }




        //リスナーセット
        holder.view.setOnClickListener {
            println("setOnClickListenerは使えているか？？")
            myListener!!.launchDetailActivity(item)
        }


    }


    override fun getItemCount(): Int {
        return  dataArrayList.size
    }

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }




}