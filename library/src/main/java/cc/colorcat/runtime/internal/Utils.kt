package cc.colorcat.runtime.internal

import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

/**
 * Author: ccolorcat
 * Date: 2023-12-19
 * GitHub: https://github.com/ccolorcat
 */
internal fun createPickMediaLauncher(
    maxItems: Int,
    mediaType: ActivityResultContracts.PickVisualMedia.VisualMediaType
): ResultLauncher<PickVisualMediaRequest, List<Uri>, List<Uri>> {
    val contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems)
    val request = PickVisualMediaRequest.Builder()
        .setMediaType(mediaType)
        .build()
    return ResultLauncher(contract, request) { it }
}