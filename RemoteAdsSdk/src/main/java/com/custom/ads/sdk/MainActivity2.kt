package com.custom.ads.sdk

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.custom.ads.sdk.adsUtils.AdsUtils
import com.custom.ads.sdk.databinding.ActivityMain2Binding
import com.custom.ads.sdk.interfaces.FrequencyProvider
import com.custom.ads.sdk.interfaces.InterstitialAdShowedListener
import com.custom.ads.sdk.viewbinding.viewBinding

class MainActivity2 : AppCompatActivity() {
    private val binding by viewBinding(ActivityMain2Binding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnClick.setOnClickListener {
            AdsApplication.adsClickCounter++
            AdsUtils.showInterstitialAd(
                this@MainActivity2,
                "main_interstitial",
                object : InterstitialAdShowedListener {
                    override fun onCompleted() {
                        startActivity(Intent(this@MainActivity2, MainActivity3::class.java))
                    }
                })
        }

        AdsUtils.loadAndShowNative(this,
            binding.layoutNativeAd.nativeLay,
            binding.layoutNativeAd.shimmerLayout,
            "main_native",
            object : FrequencyProvider {
                override fun incrementFrequency() {
                    AdsApplication.setMainScreenFrequency(AdsApplication.getMainScreenFrequency() + 1)
                }

                override fun getFrequency(): Int {
                    return AdsApplication.getMainScreenFrequency()
                }
            })

        AdsUtils.loadAndShowBanner(this,
            binding.layoutBannerAd.bannerLay,
            binding.layoutBannerAd.shimmerLayout,
            binding.layoutBannerAd.crossBannerLay,
            "main_banner",
            object : FrequencyProvider {
                override fun getFrequency(): Int {
                    return AdsApplication.getMainScreenBannerFrequency()
                }

                override fun incrementFrequency() {
                    AdsApplication.setMainScreenBannerFrequency(AdsApplication.getMainScreenBannerFrequency() + 1)
                }
            })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AdsApplication.adsClickCounter++
                AdsUtils.showInterstitialAd(
                    this@MainActivity2,
                    "main_interstitial_back",
                    object : InterstitialAdShowedListener {
                        override fun onCompleted() {
                            finish()
                        }
                    })
            }
        })
    }
}