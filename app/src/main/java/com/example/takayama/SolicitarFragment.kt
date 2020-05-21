package com.example.takayama

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_solicitar.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




class SolicitarFragment : Fragment() {



    private var solicitud_objects: ArrayList<SolicitudSerializerModel>? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            solicitud_objects = it.getSerializable(ARG_PARAM1) as ArrayList<SolicitudSerializerModel>
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        println(solicitud_objects)

        return inflater.inflate(R.layout.fragment_solicitar, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        recyclerViewSolicitarList.layoutManager = LinearLayoutManager(MyApplication.appContext)
        recyclerViewSolicitarList.adapter = MySolicitarRecyclerViewAdapter(solicitud_objects!!, listener!!)


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
        fun onClickView(selectedSolicitud: SolicitudSerializerModel)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(solicitud_objects: ArrayList<SolicitudSerializerModel>?, param2: String?) =
            SolicitarFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, solicitud_objects)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
