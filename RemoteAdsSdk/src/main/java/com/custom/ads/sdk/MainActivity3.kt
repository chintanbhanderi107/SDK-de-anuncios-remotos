package com.custom.ads.sdk

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.custom.ads.sdk.adsUtils.AdsUtils
import com.custom.ads.sdk.databinding.ActivityMain3Binding
import com.custom.ads.sdk.interfaces.FrequencyProvider
import com.custom.ads.sdk.viewbinding.viewBinding

class MainActivity3 : AppCompatActivity() {
    private val binding by viewBinding(ActivityMain3Binding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        AdsUtils.loadAndShowNative(this,
            binding.layoutNativeAd.nativeLay,
            binding.layoutNativeAd.shimmerLayout,
            "main_native_3",
            object : FrequencyProvider {
                override fun incrementFrequency() {
                    AdsApplication.setMainScreen3Frequency(AdsApplication.getMainScreen3Frequency() + 1)
                }

                override fun getFrequency(): Int {
                    return AdsApplication.getMainScreen3Frequency()
                }
            })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@MainActivity3, MainActivity2::class.java))
    }
}