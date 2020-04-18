package com.example.takayama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_crear_articulo.*
import kotlinx.android.synthetic.main.fragment_crear_articulo.*

var uri1:Uri? = null
var uri2:Uri? = null
var uri3:Uri? = null

class CrearArticuloActivity : AppCompatActivity(),
    CrearArticuloFragment.OnFragmentInteractionListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_articulo)
        setSupportActionBar(toolbar)

        uri1 = null
        uri2 = null
        uri3 = null



        toolbar.title = getString(R.string.toolbar_crearArticulo_title)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }


        //カメラを起動する流れを忘れてしまった。onCreateでまずカメラを起動して、onActivityResultで受け取る感じ？
        // だとしたら最初に記事作成フラグメントを起動するのは間違いか。。。

        //フラグメントの起動
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayoutCrearArticulo, CrearArticuloFragment.newInstance("param1", "param2"))
            .commit()
    }


    //CrearArticuloFragment.OnFragmentInteractionListener#successCrearArticulo
    override fun successCrearArticulo() {
        makeToast(this@CrearArticuloActivity, getString(R.string.success_crear_articlo_message))
        finish()
    }

    //CrearArticuloFragment.OnFragmentInteractionListener#onClickImageView
    override fun onClickImageView(imageViewId: Int) {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra("imageViewId", imageViewId)
        }
        when (imageViewId){
            R.id.ivArticuloImage1 -> startActivityForResult(intent, REQUEST_CODE_ARTICULO_IMAGE1)
            R.id.ivArticuloImage2 -> startActivityForResult(intent, REQUEST_CODE_ARTICULO_IMAGE2)
            R.id.ivArticuloImage3 -> startActivityForResult(intent, REQUEST_CODE_ARTICULO_IMAGE3)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val uri = data!!.data

        //if (requestCode != REQUEST_CODE_ARTICULO_IMAGE1 && requestCode != REQUEST_CODE_ARTICULO_IMAGE2 && requestCode != REQUEST_CODE_ARTICULO_IMAGE3) return

        when (requestCode){
            REQUEST_CODE_ARTICULO_IMAGE1 -> {
                ivArticuloImage1.setImageURI(uri)
                uri1 = uri
            }
            REQUEST_CODE_ARTICULO_IMAGE2 -> {
                ivArticuloImage2.setImageURI(uri)
                uri2 = uri
            }
            REQUEST_CODE_ARTICULO_IMAGE3 -> {
                ivArticuloImage3.setImageURI(uri)
                uri3 = uri
            }
        }


    }


}
