package com.example.takayama

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_solicitar.*

class SolicitarActivity : AppCompatActivity(),
    SolicitarFragment.OnFragmentInteractionListener,
    SolicitarDecideFragment.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitar)
        setSupportActionBar(toolbar)

        val bundle = intent.extras!!
        val solicitud_objects = bundle.getSerializable("solicitud_objects") as ArrayList<SolicitudSerializerModel>

        //取引相手を選ぶ画面を開くことを前提にする
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutSolicitar, SolicitarFragment.newInstance(solicitud_objects,""))
            .commit()
        
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


}
