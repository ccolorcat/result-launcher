package cc.colorcat.runtime

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import cc.colorcat.runtime.ForResult.Companion.CODE_COMPONENT_ABSENT

/**
 * An [ActivityResult] will be returned after calling the [ResultLauncher.launch] method.
 * If no [android.app.Activity] can handle the [Intent], the ActivityResult's [ActivityResult.getResultCode]
 * will be [CODE_COMPONENT_ABSENT] and the [ActivityResult.getData] will be null.
 *
 * Author: ccolorcat
 * Date: 2022-12-08
 * GitHub: https://github.com/ccolorcat
 */
class ForResult(
    input: Intent
) : ResultLauncher<Intent, ActivityResult, ActivityResult>(
    ActivityResultContracts.StartActivityForResult(),
    input,
    { o -> o },
) {
    override fun transformException(throwable: Throwable): ActivityResult? {
        return if (throwable is ActivityNotFoundException) {
            ActivityResult(CODE_COMPONENT_ABSENT, null)
        } else {
            super.transformException(throwable)
        }
    }

    companion object {
        const val CODE_COMPONENT_ABSENT = -1000
    }
}
