package cc.colorcat.runtime

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Author: ccolorcat
 * Date: 2022-12-08
 * GitHub: https://github.com/ccolorcat
 */
open class ResultLauncher<I, O, R>(
    private val contract: ActivityResultContract<I, O>,
    private val input: I,
    private val transform: (O) -> R
) {
    private var resultLauncher: ActivityResultLauncher<I>? = null
    private var continuation: CancellableContinuation<R>? = null

    @CallSuper
    open fun register(activity: ComponentActivity) {
        resultLauncher = activity.registerForActivityResult(contract, this::handleOutput)
    }

    @CallSuper
    open fun register(fragment: Fragment) {
        resultLauncher = fragment.registerForActivityResult(contract, this::handleOutput)
    }

    private fun handleOutput(output: O) {
        val continuation = this.continuation
        if (continuation != null && continuation.isActive) {
            continuation.resume(transform(output))
        }
        this.continuation = null
    }

    protected suspend fun realLaunch(launcher: ActivityResultLauncher<I>, input: I): R {
        return suspendCancellableCoroutine {
            continuation = it
            try {
                launcher.launch(input)
            } catch (throwable: Throwable) {
                if (it.isActive) {
                    val result = transformException(throwable)
                    if (result != null) {
                        it.resume(result)
                    } else {
                        it.resumeWithException(throwable)
                    }
                }
                continuation = null
            }
        }
    }

    protected open fun transformException(throwable: Throwable): R? {
        return null
    }

    fun cancel() {
        continuation?.cancel()
        continuation = null
    }

    suspend fun launch(): R {
        val launcher = resultLauncher ?: throw RuntimeException("You must call register first.")
        return launch(launcher, input)
    }

    protected open suspend fun launch(launcher: ActivityResultLauncher<I>, input: I): R {
        return realLaunch(launcher, input)
    }
}


fun ComponentActivity.forPermissions(permissions: Array<String>): ForPermissions {
    return ForPermissions(permissions).also {
        it.register(this)
    }
}

fun Fragment.forPermissions(permissions: Array<String>): ForPermissions {
    return ForPermissions(permissions).also {
        it.register(this)
    }
}

fun ComponentActivity.forResult(intent: Intent): ForResult {
    return ForResult(intent).also {
        it.register(this)
    }
}

fun Fragment.forResult(intent: Intent): ForResult {
    return ForResult(intent).also {
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
