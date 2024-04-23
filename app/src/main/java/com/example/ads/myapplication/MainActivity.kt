package com.example.ads.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.BaseSplashAdsActivity

class MainActivity : BaseSplashAdsActivity() {
    override fun getLayoutView(): View {
        val relativeLayout = RelativeLayout(this)
        val progressBar = ProgressBar(this)
        val layoutParams = RelativeLayout.LayoutParams(100, 100)
        layoutParams.addRule(14)
        layoutParams.addRule(12)
        layoutParams.setMargins(0, 0, 0, 50)
        relativeLayout.addView(progressBar, layoutParams)
        return relativeLayout
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

    override fun onCompleted() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdsApplication.setPremium(false)
    }
}