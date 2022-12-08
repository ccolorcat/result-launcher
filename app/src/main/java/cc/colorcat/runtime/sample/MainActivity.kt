package cc.colorcat.runtime.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cc.colorcat.runtime.forPermissions
import cc.colorcat.runtime.forResult
import cc.colorcat.runtime.sample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val permissionsLauncher = forPermissions(
        arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )
    )

    private val resultLauncher = forResult(
        Intent(Intent.ACTION_PICK).apply { type = "image/*" }
    )

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupPickImage()
        setupRequestPermissions()
    }

    private fun setupPickImage() {
        binding.pickImage.setOnClickListener {
            lifecycleScope.launch {
                val result = resultLauncher.launch()
                binding.message.text = if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let {
                        binding.image.setImageURI(it)
                    }
                    "ok"
                } else {
                    result.toString()
                }
            }
        }
    }

    private fun setupRequestPermissions() {
        binding.requestPermissions.setOnClickListener {
            lifecycleScope.launch {
                val deniedPermissions = permissionsLauncher.launch()
                binding.message.text = if (deniedPermissions.isEmpty()) {
                    "All permissions have been granted."
                } else {
                    "${deniedPermissions.contentToString()} have been denied."
                }
            }
        }
    }
}