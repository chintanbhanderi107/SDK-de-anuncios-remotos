package com.example.ads.myapplication

import android.os.Bundle
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.BaseSplashAdsActivity

class MainActivity : BaseSplashAdsActivity() {
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun getAppOpenAdId(): String {
        return getString(R.string.app_open_ads)
    }

    override fun getBannerAdId(): String {
        return getString(R.string.banner_ads)
    }

    override fun getNativeAdId(): String {
        return getString(R.string.native_ads)
    }

    override fun getInterstitialAdId(): String {
        return getString(R.string.inter_ads)
    }

    override fun onCompleted() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdsApplication.setPremium(false)
    }
}