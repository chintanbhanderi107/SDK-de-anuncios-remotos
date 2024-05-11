package com.example.ads.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.BaseSplashAdsActivity
import com.custom.ads.sdk.adsUtils.AdsUtils
import com.custom.ads.sdk.interfaces.AppOpenAdShowedListener
import com.custom.ads.sdk.utils.ConfigType

class MainActivity : BaseSplashAdsActivity() {

    override fun onCompleteSucceed() {
        Handler(Looper.getMainLooper()).postDelayed({
            AdsUtils.loadAndShowAppOpenAd(
                this,
                "main_splash_app_open",
                object : AppOpenAdShowedListener {
                    override fun onCompleted() {
                        startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                        finish()
                    }
                })
        }, 1500)
    }

    override fun onCompleteFailed() {

    }

    override fun getConfigAdsKey(): String {
        return getString(R.string.firebase_key)
    }

    override fun getConfigType(): ConfigType {
        return ConfigType.REMOTE_CONFIG
    }

    override fun getDefaultAppOpenAdId(): String {
        return getString(R.string.app_open_ads)
    }

    override fun getDefaultBannerAdId(): String {
        return getString(R.string.banner_ads)
    }

    override fun getDefaultNativeAdId(): String {
        return getString(R.string.native_ads)
    }

    override fun getDefaultInterstitialAdId(): String {
        return getString(R.string.inter_ads)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdsApplication.setPremium(false)
    }
}