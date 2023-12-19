package cc.colorcat.runtime.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Author: ccolorcat
 * Date: 2023-03-09
 * GitHub: https://github.com/ccolorcat
 */
class ResultHelperActivity : ComponentActivity() {
    private val requestCode: Int by lazy { intent.getIntExtra(REQUEST_CODE, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isValidRequest()) {
            finish()
            return
        }

        lifecycleScope.launch {
            if (ResultHelper.performAction(this@ResultHelperActivity, requestCode)) {
                finish()
            }
        }
    }

    private fun isValidRequest(): Boolean {
        return requestCode != -1 && ResultHelper.containsRequestCode(requestCode)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestCode == requestCode) {
            val result = ActivityResult(resultCode, data)
            ResultHelper.deliver(requestCode, result)
            finish()
        }
    }

    internal companion object {
        private const val REQUEST_CODE = "request_code"

        fun launch(context: Context, requestCode: Int) {
            val intent = Intent(context, ResultHelperActivity::class.java)
                .putExtra(REQUEST_CODE, requestCode)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
