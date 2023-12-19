package cc.colorcat.runtime.sample

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cc.colorcat.runtime.forPermissions
import cc.colorcat.runtime.forResult
import cc.colorcat.runtime.launchForPermissions
import cc.colorcat.runtime.launchForResult
import cc.colorcat.runtime.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

const val TAG = "ResultLauncher"

class MainActivity : AppCompatActivity() {
    private val permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
    )

    private val permissionsLauncher = forPermissions(permissions)

    private val resultLauncher = forResult(pickImageIntent)

    private val pickImageIntent: Intent
        get() = Intent(Intent.ACTION_PICK).apply { type = "image/*" }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupPickImage()
        setupRequestPermissions()
        setupRequestScreenCapture()
    }

    private fun setupPickImage() {
        binding.pickImage.setOnClickListener {
            lifecycleScope.launch {
                // The first method is recommended to be used inside Activity or Fragment.
                val result = resultLauncher.launch()

                // The second method is recommended if you can only get the Context.
//                val result = application.launchForResult(pickImageIntent)

                // The third method is recommended if you have more complex requirements.
//                val result = launchForResult { activity, requestCode ->
//                    activity.startActivityForResult(pickImageIntent, requestCode)
//                    null
//                }

                setPickImageResult(result)
            }
        }
    }

    private fun setPickImageResult(result: ActivityResult) {
        binding.message.text = if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                binding.image.setImageURI(it)
            }
            "ok"
        } else {
            result.toString()
        }
    }

    private fun setupRequestPermissions() {
        binding.requestPermissions.setOnClickListener {
            lifecycleScope.launch {
                // The first method is recommended to be used inside Activity or Fragment.
                val deniedPermissions = permissionsLauncher.launch()

                // The second method is recommended if you can only get the Context.
//                val deniedPermissions = launchForPermissions(permissions)
                setRequestPermissionsResult(deniedPermissions)
            }
        }
    }

    private fun setupRequestScreenCapture() {
        binding.requestScreenCapture.setOnClickListener {
            lifecycleScope.launch {
                val result = launchForResult { activity, requestCode -> requestScreenCapture(activity, requestCode) }
                Log.d(TAG, "request screen capture result: $result")
                binding.message.text = result.toString()
            }
        }
    }


    private fun setRequestPermissionsResult(deniedPermissions: Array<String>) {
        binding.message.text = if (deniedPermissions.isEmpty()) {
            "All permissions have been granted."
        } else {
            "${deniedPermissions.contentToString()} have been denied."
        }
    }

    companion object {
        const val RESULT_GET_MEDIA_PROJECT_MANAGER_FAILED = -100
        const val RESULT_START_SCREEN_CAPTURE_INTENT_FAILED = -101
        private fun requestScreenCapture(activity: ComponentActivity, requestCode: Int): ActivityResult? {
            val manager = activity.getSystemService(MediaProjectionManager::class.java)
            return if (manager == null) {
                ActivityResult(RESULT_GET_MEDIA_PROJECT_MANAGER_FAILED, null)
            } else if (!activity.startActivityIfNeeded(manager.createScreenCaptureIntent(), requestCode)) {
                ActivityResult(RESULT_START_SCREEN_CAPTURE_INTENT_FAILED, null)
            } else {
                null
            }
        }
    }
}