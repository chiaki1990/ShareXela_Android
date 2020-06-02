package ga.sharexela.sharexela

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat


class MyNotificationRecyclerViewAdapter(val avisoObjects: ArrayList<AvisoSerializerModel>, val listener: NotificationFragment.OnFragmentInteractionListener):RecyclerView.Adapter<MyNotificationRecyclerViewAdapter.MyViewHolder>(){

    class MyViewHolder(val v: View):RecyclerView.ViewHolder(v){

        val tvNotificationStatus: TextView;
        val tvNotificationDate: TextView;
        val tvNotificationType: TextView;
        val tvModel: TextView;
        val tvObjectId: TextView;

        init {
            tvNotificationStatus = v.findViewById(R.id.tvNotificationStatus)
            tvNotificationDate = v.findViewById(R.id.tvNotificationDate)
            tvNotificationType = v.findViewById(R.id.tvNotificationType)
            tvModel = v.findViewById(R.id.tvModel)
            tvObjectId = v.findViewById(R.id.tvObjectId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_notification_card, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        println(avisoObjects[position].created_at::class.java)
        val ddd = format.parse(avisoObjects[position].created_at)

        println("日付を写す")
        println(ddd::class.java)
        //val showFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        //val showFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val showFormat = SimpleDateFormat("dd/MM")
        val showDate: String = showFormat.format(ddd)


        //holder.tvNotificationDate.text = avisoObjects[position].created_at
        holder.tvNotificationDate.text = showDate
        if (avisoObjects[position].checked){
            holder.tvNotificationStatus.text = "☑"  //"未読"
        }else{
            holder.tvNotificationStatus.text = "☐" //"🌴読"
        }
        if (avisoObjects[position].content_object?.modelName == "Solicitud"){
            holder.tvNotificationType.text = MyApplication.appContext.getString(R.string.notification_recyclerview_adapter_recieve_solicitud) //"取引申請がきました。　　　　"
        }else if (avisoObjects[position].content_object?.modelName == "ItemContact"){
            holder.tvNotificationType.text = MyApplication.appContext.getString(R.string.notification_recyclerview_adapter_recieve_commento) //"コメントが付きました。　　　"
        }else if (avisoObjects[position].content_object?.modelName == "DirectMessage"){
            holder.tvNotificationType.text = MyApplication.appContext.getString(R.string.notification_recyclerview_adapter_begin_transaction)  //"取引を始めてください。　　　"
        }else if (avisoObjects[position].content_object?.modelName == "DirectMessageContent"){
            holder.tvNotificationType.text = MyApplication.appContext.getString(R.string.notification_recyclerview_adapter_recieve_message) //"取引相手から連絡が来ました。"
        }


        //holder.tvModel.text = avisoObjects[position].content_object?.modelName
        //holder.tvObjectId.text = avisoObjects[position].object_id.toString()
        val modelName = avisoObjects[position].content_object?.modelName
        val objectId  = avisoObjects[position].object_id.toString()

        holder.v.setOnClickListener {
            if (modelName == null) return@setOnClickListener
            if (modelName == "Solicitud" ){
                listener.launchNotificationSolicitar(objectId)
                return@setOnClickListener
            }
            if (modelName == "ItemContact"){
                listener.launchNotificationItemContact(objectId)
            }
            if (modelName == "DirectMessageContent"){
                listener.launchNotificationDirectMessageContent(objectId)
            }

        }
    }

    override fun getItemCount(): Int {
        return avisoObjects.size
    }


}