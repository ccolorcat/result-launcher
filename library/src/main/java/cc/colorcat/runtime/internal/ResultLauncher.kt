package cc.colorcat.runtime.internal

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
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
    private val provideInput: () -> I,
    private val transform: (O) -> R
) {
    private var launcher: ActivityResultLauncher<I>? = null
    private var continuation: CancellableContinuation<R>? = null
    private var contextProvider: (() -> Context?)? = null

    constructor(
        contract: ActivityResultContract<I, O>,
        input: I,
        transform: (O) -> R
    ) : this(
        contract = contract,
        provideInput = { input },
        transform = transform
    )

    fun register(activity: ComponentActivity) {
        launcher = activity.registerForActivityResult(contract, this::handleOutput)
        contextProvider = { activity }
    }

    fun register(fragment: Fragment) {
        launcher = fragment.registerForActivityResult(contract, this::handleOutput)
        contextProvider = { fragment.context }
    }

    private fun handleOutput(output: O) {
        val continuation = this.continuation
        if (continuation != null && continuation.isActive) {
            continuation.resume(transform(output))
        }
        this.continuation = null
    }

    protected suspend fun performLaunch(input: I, launcher: ActivityResultLauncher<I>): R {
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
        val launcher = launcher
        val provider = contextProvider
        if (launcher == null || provider == null) {
            throw RuntimeException("You must call register first.")
        }
        return launch(provideInput(), launcher, provider)
    }

    protected open suspend fun launch(input: I, launcher: ActivityResultLauncher<I>, provide: () -> Context?): R {
        return performLaunch(input, launcher)
    }
}
