package com.example.takayama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MySolicitarRecyclerViewAdapter(val dataArrayList: ArrayList<SolicitudSerializerModel>, val listener: SolicitarFragment.OnFragmentInteractionListener): RecyclerView.Adapter<MySolicitarRecyclerViewAdapter.MyViewHolder>(){



    class MyViewHolder(val view: View):RecyclerView.ViewHolder(view){
        val userName:TextView
        val message:TextView

        init {
            userName = view.findViewById(R.id.tvSolicitarUserNameList)
            message = view.findViewById(R.id.tvSolicitarMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySolicitarRecyclerViewAdapter.MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_recyclerview_solicitar_list_card, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MySolicitarRecyclerViewAdapter.MyViewHolder, position: Int) {

        holder.userName.text = dataArrayList[position].applicant!!.user!!.username
        holder.message.text = dataArrayList[position].message

        //println(dataArrayList[position].applicant.username)
        //println(dataArrayList[position].message)

        holder.view.setOnClickListener {
            //詳細のSolicitudデータを表示するためにコールバックする。
            listener.onClickView(dataArrayList[position])
        }

    }

    override fun getItemCount(): Int {
        return dataArrayList.size
    }


}