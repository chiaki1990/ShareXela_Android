package ga.sharexela.sharexela

import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Toastの実装
fun makeToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT ).show()
}

//retrofitのserviceを生成
fun setService():ShareXelaService{
    val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(ShareXelaService::class.java)
}

//AuthTokenHeaderの作成
fun getAuthTokenHeader(authToken: String?): String?{
    if (authToken == "" || authToken == null) return null
    return " Token " + authToken
}



//SharedPreferencesインスタンスを取得する。
// またインスタンスは、APIレベルによって暗号化させるかさせないかの性質が変わる。
fun getSharedPreferencesInstance(): SharedPreferences{

    lateinit var sharedPreferences:SharedPreferences;
    val SP_XML = "SESSION_MANAGE"

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
         sharedPreferences = MyApplication.appContext.getSharedPreferences(SP_XML, Context.MODE_PRIVATE)

    } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)


        sharedPreferences = EncryptedSharedPreferences.create(
            MyApplication.appContext.getString(R.string.LOGIN_SHARED_PREFERENCES),
            masterKeyAlias,
            MyApplication.appContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    return sharedPreferences
}


fun getAuthTokenFromSP(sharedPreferences: SharedPreferences): String? {

    val authToken = sharedPreferences.getString(MyApplication.appContext.getString(R.string.SP_KEY_AUTH_TOKEN), null)
    println("端末内のTOKENデータを表示       " +  authToken)
    return authToken
}


fun getLOGIN_STATUS(sharedPreferences: SharedPreferences): Boolean{

    val LOGIN_STATUS = sharedPreferences.getBoolean(MyApplication.appContext.getString(R.string.SP_KEY_LOGIN_STATUS), false)
    return LOGIN_STATUS
}





//MainActivityに画面遷移させる
fun sendMainActivity(context: Context){
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
}

fun sendLogInActivity(context: Context){
    val intent = Intent(context, LogInActivity::class.java)
    context.startActivity(intent)
}

fun sendSignUpActivity(context: Context){
    val intent = Intent(context, SignUpActivity::class.java)
    context.startActivity(intent)

}

fun sendProfileActivity(context: Context){
    val intent = Intent(context, ProfileActivity::class.java)
    intent.putExtra(IntentKey.FragmentTag.name, FragmentTag.PROFILE_LIST.name )
    context.startActivity(intent)
}

fun sendCrearArticuloActivity(context: Context){
    val intent = Intent(context, CrearArticuloActivity::class.java)
    intent.putExtra(IntentKey.FragmentTag.name, FragmentTag.TO_CREAR_ARTICULO.name)
    context.startActivity(intent)
}


fun sendItemContactActivity(context:Context, itemObjId:Int){
    val intent = Intent(context, ItemContactActivity::class.java).apply {
        putExtra("itemObjId", itemObjId)
    }

    context.startActivity(intent)
}


fun sendNotificationActivity(context: Context){
    val intent = Intent(context, NotificationActivity::class.java)
    context.startActivity(intent)
}


fun sendSignOutActivity(context: Context){
    val intent = Intent(context, SignOutActivity::class.java)
    context.startActivity(intent)
}


fun sendFavoriteItemActivity(context: Context){
    val intent = Intent(context, FavoriteItemActivity::class.java)
    context.startActivity(intent)
}



fun hideKeybord(fragment: Fragment){
    //Fragment内でキーボード表示中の状態で元のフラグメントに戻るとonResumeでレイアウトが適切に表示されない事例が発覚した。
    //そのためにこの関数を作成した

    //ソフトキーボードを非表示
    val imm : InputMethodManager = fragment.requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(fragment.view?.windowToken, 0)
}


/*
fun sendDetailActivity(context: Context){
    val intent = Intent(context, DetailActivity::class.java)
    context.startActivity(intent)
}
 */


//使い方： v.layoutParams = ViewGroup.LayoutParams(pixels, ViewGroup.LayoutParams.MATCH_PARENT) //ViewGroup.LayoutParams.WRAP_CONTENT
fun getPixelsMatchlDP(dps:Int):Int{
    val scale: Float = MyApplication.appContext.getResources().getDisplayMetrics().density
    val pixels: Int = (dps * scale + 0.5f).toInt()
    return  pixels
}





fun sendDeviceToken() {

    FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token.toString()

            //deviceTokenをサーバーに送信する
            val service = setService()
            service.patchDeviceTokenDealAPIVeiw(sessionData.authTokenHeader, token)
                .enqueue(object : Callback<ResultModel> {

                    override fun onResponse(call: Call<ResultModel>, response: Response<ResultModel>) {
                        println("onResponseを通る: ")

                        val result = response.body()!!.result
                        if (result == "success") return makeToast(MyApplication.appContext, token)
                        if (result == "fail") return makeToast(MyApplication.appContext, "送信不可")

                    }


                    override fun onFailure(call: Call<ResultModel>, t: Throwable) {
                        println("onFailureを通る：　")
                        println(t)
                        println(t.message)
                    }
                })
        })
}






fun getPathFromUri(context: Context, uri: Uri): String? {
    val isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    Log.e("getPathFromUri", "uri:" + uri.authority!!)
    if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if ("com.android.externalstorage.documents" == uri.authority) {// ExternalStorageProvider
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true))
            {
                return (Environment.getExternalStorageDirectory().path + "/" + split[1])
            } else
            {
                return  "/stroage/" + type + "/" + split[1]
            }
        } else if ("com.android.providers.downloads.documents" == uri.authority) {// DownloadsProvider
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        } else if ("com.android.providers.media.documents" == uri.authority) {// MediaProvider
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var contentUri: Uri? = MediaStore.Files.getContentUri("external")
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {//MediaStore
        return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {// File
        return uri.path
    }
    return null
}


fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val cindex = cursor.getColumnIndexOrThrow(projection[0])
            return cursor.getString(cindex)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}










