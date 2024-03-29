package cc.colorcat.runtime.internal

import android.content.ActivityNotFoundException
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import cc.colorcat.runtime.CODE_COMPONENT_ABSENT

/**
 * Author: ccolorcat
 * Date: 2023-04-16
 * GitHub: https://github.com/ccolorcat
 */
internal typealias Action<T> = suspend (activity: ComponentActivity, requestCode: Int) -> T?
internal typealias Receiver<T> = (result: T) -> Unit

internal class ActionAndReceiver<T>(private val action: Action<T>, private val receiver: Receiver<T>) {

    /**
     * If the result is received by "receiver", it means the execution is successful,
     * in which case it returns true, otherwise it returns false.
     */
    suspend fun performAction(activity: ComponentActivity, requestCode: Int): Boolean {
        return try {
            val result = action.invoke(activity, requestCode)
            if (result != null) {
                receiver.invoke(result)
                true
            } else {
                false
            }
        } catch (e: ActivityNotFoundException) {
            try {
                deliver(ActivityResult(CODE_COMPONENT_ABSENT, null))
                true
            } catch (e: ClassCastException) {
                false
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deliver(result: ActivityResult) {
        result as T
        receiver.invoke(result)
    }
}
