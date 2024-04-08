package cc.colorcat.runtime

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import cc.colorcat.runtime.internal.ForPermissions
import cc.colorcat.runtime.internal.ForResult
import cc.colorcat.runtime.internal.ResultHelper
import cc.colorcat.runtime.internal.ResultLauncher
import cc.colorcat.runtime.internal.createPickMediaLauncher
import cc.colorcat.runtime.internal.createPickMultipleMediaLauncher
import cc.colorcat.runtime.internal.createTakePictureLauncher

/**
 * Author: ccolorcat
 * Date: 2023-12-19
 * GitHub: https://github.com/ccolorcat
 */
const val CODE_COMPONENT_ABSENT = -1000

fun ComponentActivity.forPermissions(permissions: Array<String>): ResultLauncher<*, *, Array<String>> {
    return ForPermissions(permissions).also {
        it.register(this)
    }
}

fun Fragment.forPermissions(permissions: Array<String>): ResultLauncher<*, *, Array<String>> {
    return ForPermissions(permissions).also {
        it.register(this)
    }
}


fun ComponentActivity.forResult(intent: Intent): ResultLauncher<*, *, ActivityResult> {
    return ForResult(intent).also {
        it.register(this)
    }
}

fun Fragment.forResult(intent: Intent): ResultLauncher<Intent, ActivityResult, ActivityResult> {
    return ForResult(intent).also {
        it.register(this)
    }
}


fun ComponentActivity.forMedia(
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<*, *, Uri?> {
    return createPickMediaLauncher(mediaType).also {
        it.register(this)
    }
}

fun Fragment.forMedia(
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<*, *, Uri?> {
    return createPickMediaLauncher(mediaType).also {
        it.register(this)
    }
}


fun ComponentActivity.forMultipleMedia(
    maxItems: Int,
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<*, *, List<Uri>> {
    return createPickMultipleMediaLauncher(maxItems, mediaType).also {
        it.register(this)
    }
}

fun Fragment.forMultipleMedia(
    maxItems: Int,
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<*, *, List<Uri>> {
    return createPickMultipleMediaLauncher(maxItems, mediaType).also {
        it.register(this)
    }
}


fun ComponentActivity.takePicture(provideUri: () -> Uri): ResultLauncher<*, *, Uri?> {
    return createTakePictureLauncher(provideUri).also {
        it.register(this)
    }
}

fun Fragment.takePicture(provideUri: () -> Uri): ResultLauncher<*, *, Uri?> {
    return createTakePictureLauncher(provideUri).also {
        it.register(this)
    }
}


suspend fun Context.launchForResult(intent: Intent): ActivityResult {
    return ResultHelper.requestForResult(this, intent)
}

suspend fun Context.launchForResult(action: suspend (activity: ComponentActivity, requestCode: Int) -> ActivityResult?): ActivityResult {
    return ResultHelper.requestForResult(this, action)
}

suspend fun Context.launchForPermissions(permissions: Array<String>): Array<String> {
    return ResultHelper.requestForPermissions(this, permissions)
}
