package ga.sharexela.sharexela

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_solicitar.*


//dataをフラグメントに引き渡す要素として
// 申請者一覧を表すsolicitud_objects
// どのアイテムについて申請オブジェクトを生成するかのキーとなるitemObj がある。


class SolicitarActivity : AppCompatActivity(),
    SolicitarFragment.OnFragmentInteractionListener,
    SolicitarDecideFragment.OnFragmentInteractionListener,
    SolicitarMessageMakingFragment.OnFragmentInteractionListener {

    lateinit var itemObj: ItemSerializerModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar)
        setSupportActionBar(toolbar)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener {
                finish()
            }
        }



        // データの受取
        val bundle = intent.extras!!
        val launchFragmentTag: String = bundle.getString("launchFragmentTag")!!

        if (launchFragmentTag == "notification"){
            //通知から飛んできたものを扱う
            val solicitudObj = bundle.getSerializable("solicitudObj") as SolicitudSerializerModel
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutSolicitar, SolicitarDecideFragment.newInstance(solicitudObj,""))
                .commit()
        }

        else if (launchFragmentTag == getString(R.string.fragment_tag_choose_solicitud)){
            //取引相手を選ぶ画面を開くことを前提にする
            val solicitud_objects = bundle.getSerializable("solicitud_objects") as ArrayList<SolicitudSerializerModel>
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutSolicitar, SolicitarFragment.newInstance(solicitud_objects,""))
                .commit()
            return
        }


        else if (launchFragmentTag == "SolicitarMessageMakingFragment"){
            val itemObj = bundle.getSerializable("itemObj") as ItemSerializerModel
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutSolicitar, SolicitarMessageMakingFragment.newInstance(itemObj, ""))
                .commit()
            return
        }

        
    }

    override fun onClickView(selectedSolicitud: SolicitudSerializerModel) {
        //solicitarデータを渡す

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutSolicitar, SolicitarDecideFragment.newInstance(selectedSolicitud,""))
            .commit()
    }


    override fun launchDirectMessageActivity(itemObj: ItemSerializerModel) {
        //DirectMassageActivityを起動する
        val intent = Intent(this@SolicitarActivity, DirectMessageActivity::class.java)
        intent.putExtra("itemObj",itemObj)
        startActivity(intent)
        finish()

    }

    override fun launchSolicitarFragment(solicitudObjects: ArrayList<SolicitudSerializerModel>) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutSolicitar, SolicitarFragment.newInstance(solicitudObjects,""))
            .commit()
    }


    override fun finishSolicitarActivity() {
        finish()
    }


}
