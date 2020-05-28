package ga.sharexela.sharexela

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_notification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"





class NotificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val service = setService()
        service.getAvisosAllListAPIView(sessionData.authTokenHeader).enqueue(object : Callback<AvisosAllListAPIViewModel>{

            override fun onResponse(call: Call<AvisosAllListAPIViewModel>, response: Response<AvisosAllListAPIViewModel>) {
                println("onResponseを通る")
                //println(response.body())

                //ここにavisoの一覧を表示する = RecyclerViewで表現する
                val avisoObjects: ArrayList<AvisoSerializerModel> = response.body()!!.AVISO_OBJECTS


                val layoutManager = LinearLayoutManager(MyApplication.appContext)
                recyclerViewNotification.layoutManager = layoutManager
                recyclerViewNotification.adapter = MyNotificationRecyclerViewAdapter(avisoObjects,listener!!)

            }

            override fun onFailure(call: Call<AvisosAllListAPIViewModel>, t: Throwable) {
                println("onFailureを通る")
                println(t)
                println(t.message)
            }
        })
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

        fun launchNotificationSolicitar(objectId:String)

        fun launchNotificationItemContact(itemContactobjId:String)

        fun launchNotificationDirectMessageContent(directMessageonrent:String)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
