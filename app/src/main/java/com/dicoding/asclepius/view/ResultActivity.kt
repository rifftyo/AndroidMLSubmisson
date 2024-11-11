package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_IMAGE = "extra_image"
    }

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.resultText.text = intent.getStringExtra(EXTRA_RESULT)
        binding.resultImage.setImageURI(intent.getStringExtra(EXTRA_IMAGE)?.toUri())
    }


}