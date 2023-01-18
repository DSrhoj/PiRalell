package com.nativeit.piralell

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nativeit.piralell.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvEmoji.text = "🐣"

        binding.btnCalculate.setOnClickListener{
            processCalculation(
                7,
                10_000_0,
                binding.tvResult
            )
            binding.tvResult.setTextColor(resources.getColor(R.color.black))
            binding.btnCalculate.isEnabled = false
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}