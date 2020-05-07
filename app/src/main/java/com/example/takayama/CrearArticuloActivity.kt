package com.example.takayama

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_crear_articulo.*
import kotlinx.android.synthetic.main.fragment_crear_articulo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var uri1:Uri? = null
var uri2:Uri? = null
var uri3:Uri? = null
var imageView1FilePath: String? = null
var imageView2FilePath: String? = null
var imageView3FilePath: String? = null

val REQUEST_TAKE_PHOTO = 1


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




    override fun onLaunchImagesActivity() {

        //後にuriを引っ張るためにstartActivityForResultに改修する
        //val intent = Intent(this, ImagesActivity::class.java)
        //startActivity(intent)

        val intent = Intent(this, ImagesActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_IMAGES_ACTIVITY)
    }


    var currentPhotoPath: String = ""

    fun createImageFile():File{
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return makeToast(this, resultCode.toString())

        when (requestCode){
            REQUEST_CODE_ARTICULO_IMAGE1 -> {
                val uri = data!!.data
                ivArticuloImage1.setImageURI(uri)
                uri1 = uri
            }
            REQUEST_CODE_ARTICULO_IMAGE2 -> {
                val uri = data!!.data
                ivArticuloImage2.setImageURI(uri)
                uri2 = uri
            }
            REQUEST_CODE_ARTICULO_IMAGE3 -> {
                val uri = data!!.data
                ivArticuloImage3.setImageURI(uri)
                uri3 = uri
            }

            REQUEST_CODE_IMAGES_ACTIVITY -> {

                makeToast(this, "わはっはははははｈ")
                imageView1FilePath = data!!.getStringExtra("imageView1FilePath")
                imageView2FilePath = data!!.getStringExtra("imageView2FilePath")
                imageView3FilePath = data!!.getStringExtra("imageView3FilePath")


                //uri1 = Uri.parse(imageView1FilePath)
                //uri2 = Uri.parse(imageView2FilePath)
                //uri3 = Uri.parse(imageView3FilePath)

                makeToast(this, imageView1FilePath+imageView2FilePath+imageView3FilePath)

                Glide.with(this).load(imageView1FilePath).into(ivArticuloImage1)
                Glide.with(this).load(imageView2FilePath).into(ivArticuloImage2)
                Glide.with(this).load(imageView3FilePath).into(ivArticuloImage3)
            }
        }


    }


}
