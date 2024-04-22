package com.custom.ads.sdk

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(private val adsApplication: AdsApplication) : LifecycleObserver,
    Application.ActivityLifecycleCallbacks {
    var appOpenAd: AppOpenAd? = null
    var isAdShow = false
    private var currentActivity: Activity? = null

    init {
        adsApplication.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (currentActivity !is BaseSplashAdsActivity) {
            if (!isAdShow && !AdsApplication.isPremium()) {
                showAdIfAvailable()
            }
        }
    }

    fun fetchAd(adsId: String, adFailed: String) {
        try {
            if (isAdAvailable) {
                return
            }
            val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(appOpenAd: AppOpenAd) {
                        super.onAdLoaded(appOpenAd)
                        this@AppOpenManager.appOpenAd = appOpenAd
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        if (adFailed == com.custom.ads.sdk.utils.Utils.CROSS_PROMOTION) {
                            showCrossAppOpenAds()
                        } else {
                            appOpenAd = null
                        }
                    }
                }
            if (!AdsApplication.isPremium()) {
                val request = adRequest
                AppOpenAd.load(
                    adsApplication,
                    adsId,
                    request,
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    loadCallback
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()
    private val isAdAvailable: Boolean
        get() = appOpenAd != null

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    private fun showAdIfAvailable() {
        if (!AdsApplication.isPremium()) {
            if (AdsApplication.getShowAds()) {
                for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                    if (BaseSplashAdsActivity.adsUnit[i].adsName == "on_resume_open_app") {
                        if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                            if (BaseSplashAdsActivity.adsUnit[i].publishers == com.custom.ads.sdk.utils.Utils.AD_UNIT) {
                                val adUnitId =
                                    if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds else AdsApplication.defaultAppOpenAdId
                                if (!isShowingAd && isAdAvailable) {
                                    val fullScreenContentCallback: FullScreenContentCallback =
                                        object : FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                try {
                                                    appOpenAd = null
                                                    isShowingAd = false
                                                    fetchAd(
                                                        adUnitId!!,
                                                        BaseSplashAdsActivity.adsUnit[i].adFailed!!
                                                    )
                                                } catch (exception: Exception) {
                                                    exception.printStackTrace()
                                                }
                                            }

                                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                                appOpenAd = null
                                                isShowingAd = false
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                isShowingAd = true
                                            }
                                        }
                                    appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
                                    appOpenAd?.show(currentActivity!!)
                                } else {
                                    fetchAd(adUnitId!!, BaseSplashAdsActivity.adsUnit[i].adFailed!!)
                                }
                            } else if (BaseSplashAdsActivity.adsUnit[i].publishers == com.custom.ads.sdk.utils.Utils.CROSS_PROMOTION) {
                                showCrossAppOpenAds()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCrossAppOpenAds() {
        val fullScreenDialog =
            Dialog(currentActivity!!, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        fullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fullScreenDialog.setContentView(R.layout.cross_promotion_app_open_ad_layout)
        fullScreenDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgAdIcon = fullScreenDialog.findViewById<ImageView>(R.id.imgAdIcon)
        val textContinueToApp = fullScreenDialog.findViewById<TextView>(R.id.textContinueToApp)
        val textAdName = fullScreenDialog.findViewById<TextView>(R.id.textAdName)
        val imgAdClose = fullScreenDialog.findViewById<ImageView>(R.id.imgAdClose)
        val adTopLayout: RelativeLayout = fullScreenDialog.findViewById(R.id.adTopLayout)
        val adBottomLayout: RelativeLayout = fullScreenDialog.findViewById(R.id.adBottomLayout)
        val imgAdMedia = fullScreenDialog.findViewById<ImageView>(R.id.imgAdMedia)
        Glide.with(currentActivity!!).load(BaseSplashAdsActivity.crossOpenAppAds?.adAppIcon).into(imgAdIcon)
        Glide.with(currentActivity!!).load(BaseSplashAdsActivity.crossOpenAppAds?.adMedia).into(imgAdMedia)
        textAdName.text = BaseSplashAdsActivity.crossOpenAppAds?.adHeadline
        textContinueToApp.setOnClickListener {
            fullScreenDialog.dismiss()
            AdsApplication.appOpenManager?.isAdShow = false
        }
        imgAdClose.setOnClickListener {
            fullScreenDialog.dismiss()
            AdsApplication.appOpenManager?.isAdShow = false
        }
        adTopLayout.setOnClickListener {
            fullScreenDialog.dismiss()
            AdsApplication.appOpenManager?.isAdShow = false
        }
        adBottomLayout.setOnClickListener {
            currentActivity!!.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse(BaseSplashAdsActivity.crossOpenAppAds?.adCallToActionUrl)
                )
            )
        }
        fullScreenDialog.setOnDismissListener {
            it.dismiss()
            AdsApplication.appOpenManager?.isAdShow = false
        }
        if (!currentActivity!!.isFinishing && !fullScreenDialog.isShowing) {
            fullScreenDialog.show()
            AdsApplication.appOpenManager?.isAdShow = true
        }
    }

    companion object {
        private var isShowingAd = false
    }
}