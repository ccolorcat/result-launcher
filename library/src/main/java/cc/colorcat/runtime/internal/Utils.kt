package cc.colorcat.runtime.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper

/**
 * Author: ccolorcat
 * Date: 2023-12-19
 * GitHub: https://github.com/ccolorcat
 */
internal fun createPickMultipleMediaLauncher(
    maxItems: Int,
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<PickVisualMediaRequest, List<Uri>, List<Uri>> {
    val contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    val request = PickVisualMediaRequest.Builder()
        .setMediaType(mediaType)
        .build()
    return ResultLauncher(contract, request) { it }
}

internal fun createPickMediaLauncher(
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<PickVisualMediaRequest, Uri?, Uri?> {
    val contract = ActivityResultContracts.PickVisualMedia()
    val request = PickVisualMediaRequest.Builder()
        .setMediaType(mediaType)
        .build()
    return ResultLauncher(contract, request) { it }
}


internal fun createTakePictureLauncher(provideUri: () -> Uri): ResultLauncher<Uri, Uri?, Uri?> {
    val contract = object : ActivityResultContract<Uri, Uri?>() {
        private var output: Uri? = null

        @CallSuper
        override fun createIntent(context: Context, input: Uri): Intent {
            output = input
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, input)
        }

        override fun getSynchronousResult(context: Context, input: Uri): SynchronousResult<Uri?>? = null

        @Suppress("AutoBoxing")
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) output else null
        }
    }
    return ResultLauncher(contract = contract, provideInput = provideUri) { it }
}


internal fun createGetContentLauncher(mimeType: String): ResultLauncher<String, Uri?, Uri?> {
    val contract = ActivityResultContracts.GetContent()
    return ResultLauncher(contract, mimeType) { it }
}
