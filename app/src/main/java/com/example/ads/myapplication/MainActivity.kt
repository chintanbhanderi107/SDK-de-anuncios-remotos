package com.example.ads.myapplication

import android.os.Bundle
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.BaseAdsActivity

class MainActivity : BaseAdsActivity() {
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onCompleted() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdsApplication.setPremium(false)
    }
}