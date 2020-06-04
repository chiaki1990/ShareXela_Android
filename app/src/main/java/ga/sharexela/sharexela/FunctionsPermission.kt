package ga.sharexela.sharexela

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsClient.getPackageName
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment


fun isAllPermissionsGranted(REQUIRED_PERMISSIONS: Array<String>) = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
        MyApplication.appContext, it) == PackageManager.PERMISSION_GRANTED
}



// https://blog.usejournal.com/method-to-detect-if-user-has-selected-dont-ask-again-while-requesting-for-permission-921b95ded536
class PermissionUtils{

    fun neverAskAgainSelected(permission: String, fragment: Fragment):Boolean{
        val prevShouldShowStatus: Boolean = getRatinaleDisplayStatus(permission)
        val currShouldShowStatus: Boolean = ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), permission)
        return prevShouldShowStatus != currShouldShowStatus;
    }

    fun setShouldShowStatus(permission: String?) {
        val genPrefs: SharedPreferences = MyApplication.appContext.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        val editor = genPrefs.edit()
        editor.putBoolean(permission, true)
        editor.apply()
    }

    fun getRatinaleDisplayStatus( permission: String?): Boolean {
        val genPrefs = MyApplication.appContext.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE)
        return genPrefs.getBoolean(permission, false)
    }

}

fun displayNeverAskAgainDialog(fragment: Fragment) {
    val builder = AlertDialog.Builder(fragment.requireActivity())
    builder.setMessage(
        """
            Please permit the permission through Settings screen.
            
            Select Permisos -> Habilitar el permiso
            """.trimIndent()
    )
    builder.setCancelable(false)
    builder.setPositiveButton(
        "Permiso manual"
    ) { dialog, which ->
        dialog.dismiss()
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", MyApplication.appContext.packageName, null)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        MyApplication.appContext.startActivity(intent)
    }
    builder.setNegativeButton("Cancelar", null)
    builder.show()
}

