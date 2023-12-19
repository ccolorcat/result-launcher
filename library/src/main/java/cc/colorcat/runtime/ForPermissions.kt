package cc.colorcat.runtime

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * After calling the [ResultLauncher.launch] method, the missing permissions will be requested,
 * and the denied permissions will be returned in an array.
 * If the returned array is empty, it means that all permissions have been granted.
 *
 * Author: ccolorcat
 * Date: 2022-12-08
 * GitHub: https://github.com/ccolorcat
 */
class ForPermissions(
    permissions: Array<String>
) : ResultLauncher<Array<String>, Map<String, Boolean>, Array<String>>(
    ActivityResultContracts.RequestMultiplePermissions(),
    permissions,
    { result -> result.filter { !it.value }.keys.toTypedArray() },
) {
    override suspend fun launch(
        input: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>,
        provide: () -> Context?
    ): Array<String> {
        val context = provide() ?: throw RuntimeException("context is null.")
        val missingPermissions = input.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        return if (missingPermissions.isEmpty()) {
            emptyArray()
        } else {
            performLaunch(missingPermissions.toTypedArray(), launcher)
        }
    }
}
