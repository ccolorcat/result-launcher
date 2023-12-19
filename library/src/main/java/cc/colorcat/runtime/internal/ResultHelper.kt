package cc.colorcat.runtime.internal

import android.content.Context
import android.content.Intent
import android.util.SparseArray
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.core.util.containsKey
import androidx.core.util.size
import androidx.lifecycle.Lifecycle
import cc.colorcat.runtime.forPermissions
import cc.colorcat.runtime.forResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Author: ccolorcat
 * Date: 2023-04-16
 * GitHub: https://github.com/ccolorcat
 */
internal object ResultHelper {
    private val actionAndReceivers = SparseArray<ActionAndReceiver<*>>()

    suspend fun requestForResult(context: Context, action: Action<ActivityResult>): ActivityResult {
        return suspendCancellableCoroutine { cc ->
            request(context, action) {
                if (cc.isActive) {
                    cc.resume(it)
                }
            }
        }
    }

    suspend fun requestForResult(context: Context, intent: Intent): ActivityResult {
        return if (context is ComponentActivity && context.canRegisterForResult) {
            context.forResult(intent).launch()
        } else {
            suspendCancellableCoroutine { cc ->
                request(context, { act, _ -> act.forResult(intent).launch() }) {
                    if (cc.isActive) {
                        cc.resume(it)
                    }
                }
            }
        }
    }

    suspend fun requestForPermissions(context: Context, permissions: Array<String>): Array<String> {
        return if (context is ComponentActivity && context.canRegisterForResult) {
            context.forPermissions(permissions).launch()
        } else {
            suspendCancellableCoroutine { cc ->
                request(context, { act, _ -> act.forPermissions(permissions).launch() }) {
                    if (cc.isActive) {
                        cc.resume(it)
                    }
                }
            }
        }
    }

    private fun <T> request(context: Context, action: Action<T>, receiver: Receiver<T>) {
        val requestCode = actionAndReceivers.size
        actionAndReceivers[requestCode] = ActionAndReceiver(action, receiver)
        ResultHelperActivity.launch(context, requestCode)
    }


    internal fun containsRequestCode(requestCode: Int): Boolean = actionAndReceivers.containsKey(requestCode)

    /**
     * Returns true if the action was executed successfully, false otherwise.
     */
    internal suspend fun performAction(activity: ComponentActivity, requestCode: Int): Boolean {
        return actionAndReceivers[requestCode]!!.performAction(activity, requestCode).also { successful ->
            if (successful) {
                actionAndReceivers.remove(requestCode)
            }
        }
    }

    internal fun deliver(requestCode: Int, result: ActivityResult) {
        val actionAndResult = actionAndReceivers[requestCode] ?: return
        actionAndReceivers.remove(requestCode)
        actionAndResult.deliver(result)
    }


    private val ComponentActivity.canRegisterForResult: Boolean
        get() {
            val state = lifecycle.currentState
            return state < Lifecycle.State.STARTED && state != Lifecycle.State.DESTROYED
        }
}
