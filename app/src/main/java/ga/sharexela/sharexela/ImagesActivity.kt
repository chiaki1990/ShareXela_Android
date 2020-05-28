package ga.sharexela.sharexela

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_images.*
import kotlinx.android.synthetic.main.content_images.*
import java.io.File
import java.util.concurrent.Executors






class ImagesActivity : AppCompatActivity() {


    private  val REQUEST_CODE_PERMISSIONS = 10
    private  val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
    private  val READ_REQUEST_CODE1: Int = 42
    private  val READ_REQUEST_CODE2: Int = 44
    private  val READ_REQUEST_CODE3: Int = 46


    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    var ivImages1Set: Boolean = false
    var ivImages2Set: Boolean = false
    var ivImages3Set: Boolean = false

    var emptyImageView: String? = ""
    var emptyView     : String? = ""

    /* imageView1 はCrearArticuloActivityで使っているので後々ネーミングを修正するかも*/
    var imageView1FilePath: String = ""
    var imageView2FilePath: String = ""
    var imageView3FilePath: String = ""





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        menu.apply {
            findItem(R.id.menuSearch).isVisible = false
            findItem(R.id.action_settings).isVisible = false
            findItem(R.id.menuDone).isVisible = true
            findItem(R.id.menuGoHome).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuDone -> {
                makeToast(this, "URIをCREARACTIVITYに戻してやる。")
                val intent = Intent()
                intent.putExtra("imageView1FilePath", imageView1FilePath)
                intent.putExtra("imageView2FilePath", imageView2FilePath)
                intent.putExtra("imageView3FilePath", imageView3FilePath)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return true
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        setSupportActionBar(toolbar)

        //toolbarの完了ボタンを設定する
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener { finish() }



        ivImages1.setOnClickListener {
            //ギャラリーを引っ張る
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, READ_REQUEST_CODE1)
        }

        ivImages1.setOnLongClickListener{
            //イメージを消去する
            ivImages1.setImageResource(R.drawable.ic_image_black_24dp)
            ivImages1Set = false
            true
        }


        ivImages2.setOnClickListener {
            //ギャラリーを引っ張る
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, READ_REQUEST_CODE2)
        }

        ivImages2.setOnLongClickListener {
            //イメージを消去する
            ivImages2.setImageResource(R.drawable.ic_image_black_24dp)
            ivImages2Set = false
            true
        }

        ivImages3.setOnClickListener {
            //ギャラリーを引っ張る
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, READ_REQUEST_CODE3)
        }

        ivImages3.setOnLongClickListener {
            //イメージを消去する
            ivImages3.setImageResource(R.drawable.ic_image_black_24dp)
            ivImages3Set = false
            true
        }

        emptyImageView = searchEmptyImageView()
        setViewFinder(emptyImageView!!)
    }

    fun searchEmptyImageView():String?{

        //checkImageView
        val images = arrayListOf<Int>(R.id.ivImages1, R.id.ivImages2, R.id.ivImages3)
        for (iv in images){
            when (iv){
                R.id.ivImages1 -> if (ivImages1Set == false){
                    emptyImageView = "imageView1"
                    return emptyImageView
                }
                R.id.ivImages2 -> if (ivImages2Set == false){
                    emptyImageView = "imageView2"
                    return emptyImageView
                }
                R.id.ivImages3 -> if (ivImages3Set == false){
                    emptyImageView = "imageView3"
                    return emptyImageView
                }
                else -> return null
            }
        }
        return null
    }


    fun setViewFinder(emptyImageView: String){
        viewFinder = findViewById(R.id.view_finder)

        if (allPermissionsGranted()) {
            viewFinder.post { startCamera(emptyImageView) }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }


    private fun startCamera(emptyImageView:String) {
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(480, 640))
        }.build()
        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                //setTargetAspectRatio(AspectRatio.values())
                //setTargetAspectRatioCustom(Rational(1920, 1080))
                setTargetResolution(Size(480, 640))
                //setTargetResolution(Size(1920, 1080))
            }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)

        findViewById<ImageButton>(R.id.capture_button).setOnClickListener {

            val fileDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TAKAYAMA")
            fileDir.mkdirs()

            val file = File(fileDir, "${System.currentTimeMillis()}.jpg")
            imageCapture.takePicture(file, executor, object :ImageCapture.OnImageSavedListener{


                override fun onError(imageCaptureError: ImageCapture.ImageCaptureError, message: String, exc: Throwable?) {

                    val msg = "Photo capture failed: $message"
                    Log.e("CameraXApp", msg, exc)
                    viewFinder.post {
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        CameraX.unbind(preview, imageCapture)
                    }
                }

                override fun onImageSaved(file: File) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    Log.d("CameraXApp", msg)
                    viewFinder.post {
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

                        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                            val f = File(file.absolutePath)
                            mediaScanIntent.data = Uri.fromFile(f)
                            sendBroadcast(mediaScanIntent)
                            CameraX.unbind(preview, imageCapture)
                        }
                        //撮った写真を描画
                        when (emptyImageView){
                            "imageView1" -> {
                                Glide.with(this@ImagesActivity).load(file.absolutePath).into(ivImages1)
                                //imageView.setImageURI(Uri.parse(file.absolutePath))
                                imageView1FilePath = file.absolutePath
                                ivImages1Set = true
                                //searchEmptyImageView()
                                emptyView = searchEmptyImageView()
                                if (emptyView == null) return@post
                                setViewFinder(emptyView!!)
                            }

                            "imageView2" -> {
                                Glide.with(this@ImagesActivity).load(file.absolutePath).into(ivImages2)
                                //imageView2.setImageURI(Uri.parse(file.absolutePath))
                                imageView2FilePath = file.absolutePath
                                ivImages2Set = true
                                //searchEmptyImageView()
                                emptyView = searchEmptyImageView()
                                if (emptyView == null) return@post
                                setViewFinder(emptyView!!)
                            }

                            "imageView3" -> {
                                Glide.with(this@ImagesActivity).load(file.absolutePath).into(ivImages3)
                                //imageView3.setImageURI(Uri.parse(file.absolutePath))
                                imageView3FilePath = file.absolutePath
                                ivImages3Set = true
                                //searchEmptyImageView()
                                emptyView = searchEmptyImageView()
                                if (emptyView == null) return@post
                                setViewFinder(emptyView!!)
                            }
                        }

                    }

                }
            })
        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }


    private fun updateTransform() {

        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        //matrix.postRotate(0.toFloat(), centerX, centerY)
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)

    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera(emptyImageView!!) }
            } else {
                makeToast(this, "Permissions not granted by the user.")
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
        データをImageViewに表示する
        SAFで開いた画像をMediaStore形式のuriに変換する
        カメラバインディングを切る
        空のImageViewを探してカメラバインディングを行う
        */


        if (requestCode == READ_REQUEST_CODE1 && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
                ivImages1.setImageURI(uri)
                imageView1FilePath = getPathFromUri(MyApplication.appContext, uri)!!
            }
            ivImages1Set = true
            CameraX.unbindAll()
            emptyView = searchEmptyImageView()
            if (emptyView == null) return
            setViewFinder(emptyView!!)
            return
        }
        if (requestCode == READ_REQUEST_CODE2 && resultCode == Activity.RESULT_OK){
            data?.data?.also { uri ->
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
                ivImages2.setImageURI(uri)
                imageView2FilePath = getPathFromUri(MyApplication.appContext, uri)!!
            }
            ivImages2Set = true
            CameraX.unbindAll()
            emptyView = searchEmptyImageView()
            if (emptyView == null) return
            setViewFinder(emptyView!!)
            return
        }
        if (requestCode == READ_REQUEST_CODE3 && resultCode == Activity.RESULT_OK){
            data?.data?.also { uri ->
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show()
                ivImages3.setImageURI(uri)
                imageView3FilePath = getPathFromUri(MyApplication.appContext, uri)!!
            }
            ivImages3Set = true
            CameraX.unbindAll()
            emptyView = searchEmptyImageView()
            if (emptyView == null) return
            setViewFinder(emptyView!!)
            return
        }
    }
}
