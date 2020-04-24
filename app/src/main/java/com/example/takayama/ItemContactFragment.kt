package com.example.takayama

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlinx.android.synthetic.main.fragment_direct_message.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class ItemContactFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var itemContactObjects: ItemContactListAPIViewModel? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemContactObjects = it.getSerializable(ARG_PARAM1) as ItemContactListAPIViewModel
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // original return inflater.inflate(R.layout.fragment_item_contact, container, false)
        return inflater.inflate(R.layout.fragment_direct_message, container, false)
    }





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //画面を描画する
        val layoutManager = LinearLayoutManager(MyApplication.appContext)
        recyclerViewDirectMessage.layoutManager = layoutManager.apply {
            stackFromEnd = true
        }
        recyclerViewDirectMessage.adapter = MyItemContactRecyclerViewAdapter(itemContactObjects!!.ITEM_CONTACT_OBJECTS)

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




    interface OnFragmentInteractionListener {

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(itemContactObjects: Serializable, param2: String) =
            ItemContactFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, itemContactObjects)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
