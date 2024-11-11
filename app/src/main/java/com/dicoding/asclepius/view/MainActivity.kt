package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.viewmodel.MainViewModel
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var results: String? = null

    private val viewModel: MainViewModel by viewModels()

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            showImage()
        } else {
            showToast("Gagal mengambil gambar")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showImage()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        viewModel.currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        showToast(error)
                    }
                }

                override fun onResults(result: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        result?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val confidence = it[0].categories[0].score * 100
                                val confidencePercentage = String.format("%.0f%%", confidence)
                                val label = it[0].categories[0].label
                                results = "$label $confidencePercentage"
                                moveToResult()
                            }
                        }
                    }
                }
            }
        )

        imageClassifierHelper.classifyStaticImage(viewModel.currentImageUri?: Uri.EMPTY)
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_RESULT, results)
        intent.putExtra(ResultActivity.EXTRA_IMAGE, viewModel.currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}