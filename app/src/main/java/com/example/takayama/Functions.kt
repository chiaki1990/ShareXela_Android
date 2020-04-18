package com.example.takayama

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
    context.startActivity(intent)
}

fun sendCrearArticuloActivity(context: Context){
    val intent = Intent(context, CrearArticuloActivity::class.java)
    context.startActivity(intent)
}

/*
fun sendDetailActivity(context: Context){
    val intent = Intent(context, DetailActivity::class.java)
    context.startActivity(intent)
}
 */



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
