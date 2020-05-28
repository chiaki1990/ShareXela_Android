package ga.sharexela.sharexela

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat





fun isAllPermissionsGranted(REQUIRED_PERMISSIONS: Array<String>) = REQUIRED_PERMISSIONS.all {
    ContextCompat.checkSelfPermission(
        MyApplication.appContext, it) == PackageManager.PERMISSION_GRANTED
}