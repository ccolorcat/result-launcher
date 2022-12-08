package cc.colorcat.runtime

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment

/**
 * Author: ccolorcat
 * Date: 2022-12-08
 * GitHub: https://github.com/ccolorcat
 */
abstract class ContextResultLauncher<I, O, R>(
    contract: ActivityResultContract<I, O>,
    input: I,
    transform: (O) -> R
) : ResultLauncher<I, O, R>(contract, input, transform) {
    private var contextProvider: () -> Context? = { null }

    final override fun register(activity: ComponentActivity) {
        super.register(activity)
        contextProvider = { activity }
    }

    final override fun register(fragment: Fragment) {
        super.register(fragment)
        contextProvider = { fragment.context }
    }

    final override suspend fun launch(launcher: ActivityResultLauncher<I>, input: I): R {
        val context = contextProvider.invoke() ?: throw RuntimeException("context is null.")
        return launch(context, launcher, input)
    }

    protected abstract suspend fun launch(context: Context, launcher: ActivityResultLauncher<I>, input: I): R
}
