package ga.sharexela.sharexela

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment

import kotlinx.android.synthetic.main.activity_crear_articulo.*
import kotlinx.android.synthetic.main.fragment_crear_articulo.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// このアクティビティはCrearActivityからCrearArticuloFragmentoまたは
// EditarAriticuloFragmentoを起動させる。まずEditarArticuloFragmentのIntentKeyが
// 存在するか調べ、あればEditarArticuloFragmentを起動させる


//このアクティビティの説明
// CrearArticuloFragmentとEditarArticuloFragmentが存在する
//これを起動する役割をもつ


var uri1:Uri? = null
var uri2:Uri? = null
var uri3:Uri? = null
var imageView1FilePath: String? = null
var imageView2FilePath: String? = null
var imageView3FilePath: String? = null

//val REQUEST_TAKE_PHOTO = 1


class CrearArticuloActivity : AppCompatActivity(),
    CrearArticuloFragment.OnFragmentInteractionListener,
    EditarArticuloFragment.OnFragmentInteractionListener,
    GetCoordinatesFragment.OnFragmentInteractionListener{



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.menuGoHome).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = false
            findItem(R.id.menuSync).isVisible = false
        }
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_articulo)
        setSupportActionBar(toolbar)

        uri1 = null
        uri2 = null
        uri3 = null
        imageView1FilePath = null
        imageView2FilePath = null
        imageView3FilePath = null



        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }


        if (intent.extras?.getString(IntentKey.FragmentTag.name) == FragmentTag.EDITAR_ARTICULO.name){
            println("EditarArticuloFragentを起動する@CrearArticuloActicity#OnCreate")

            toolbar.title = getString(R.string.editar_articulo_title)

            val itemObj = intent.extras!!.getSerializable(IntentKey.ItemObj.name) as ItemSerializerModel
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutCrearArticulo, EditarArticuloFragment.newInstance(itemObj, ""))
                .commit()
            return
        }

        if (intent.extras?.getString(IntentKey.FragmentTag.name) == FragmentTag.TO_CREAR_ARTICULO.name){
            println("CrearArticuloFragentを起動する@CrearArticuloActicity#OnCreate")

            toolbar.title = getString(R.string.toolbar_crearArticulo_title)

            //フラグメントの起動
            supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutCrearArticulo, CrearArticuloFragment.newInstance(null, "param2"))
                .commit()
        }
    }


    //CrearArticuloFragment.OnFragmentInteractionListener#successCrearArticulo
    override fun successCrearArticulo() {
        makeToast(this@CrearArticuloActivity, getString(R.string.success_crear_articlo_message))
        finish()
    }


    //CrearArticuloFragment.OnFragmentInteractionListener#onLaunchImagesActivity
    override fun onLaunchImagesActivity() {

        val intent = Intent(this, ImagesActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_IMAGES_ACTIVITY)
    }


    //CrearArticuloFragment.OnFragmentInteractionListener#launchGetCoordinatesFragment
    override fun launchGetCoordinatesFragment(itemObj: ItemSerializerModel, launchFrom: String){

        if (launchFrom == FragmentTag.FROM_CREAR_ARTICULO_FRAGMENT.name) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutCrearArticulo, GetCoordinatesFragment.newInstance(itemObj, ""), launchFrom)
                .commit()
            return
        } else if (launchFrom == FragmentTag.FROM_EDITAR_ARTICULO_FRAGMENT.name){
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutCrearArticulo, GetCoordinatesFragment.newInstance(itemObj, ""), launchFrom)
                .commit()
            return
        }
    }


    //GetCoordinatesFragment.OnFragmentInteractionListener#updateItemObj
    override fun sendCrearArticuloFragmentAgain(itemObj: ItemSerializerModel?) {
        //GoogleMapから得たデータ(point, radius)も含めたitemObjをCrearArticuloFragmentに反映する
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutCrearArticulo, CrearArticuloFragment.newInstance(itemObj, ""))
            .commit()

    }

    override fun sendEditarArticuloFragmentAgain(itemObj: ItemSerializerModel?) {
        //GoogleMapから得たデータ(point, radius)も含めたitemObjをEditarArticuloFragmentに反映する

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutCrearArticulo, EditarArticuloFragment.newInstance(itemObj!!, ""))
            .commit()
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

                imageView1FilePath = data!!.getStringExtra("imageView1FilePath")
                imageView2FilePath = data.getStringExtra("imageView2FilePath")
                imageView3FilePath = data.getStringExtra("imageView3FilePath")


                //uri1 = Uri.parse(imageView1FilePath)
                //uri2 = Uri.parse(imageView2FilePath)
                //uri3 = Uri.parse(imageView3FilePath)

                //makeToast(this, imageView1FilePath+imageView2FilePath+imageView3FilePath)

                Glide.with(this).load(imageView1FilePath).into(ivArticuloImage1)
                Glide.with(this).load(imageView2FilePath).into(ivArticuloImage2)
                Glide.with(this).load(imageView3FilePath).into(ivArticuloImage3)
            }
        }
    }

}
