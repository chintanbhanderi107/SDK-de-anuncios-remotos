package com.custom.ads.sdk.adsUtils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.Pair
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.BaseSplashAdsActivity
import com.custom.ads.sdk.CrossAdInterstitial
import com.custom.ads.sdk.R
import com.custom.ads.sdk.interfaces.AppOpenAdShowedListener
import com.custom.ads.sdk.interfaces.CrossInterstitialAdShowedListener
import com.custom.ads.sdk.interfaces.FrequencyProvider
import com.custom.ads.sdk.interfaces.InterstitialAdShowedListener
import com.custom.ads.sdk.utils.Utils
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import java.util.Calendar

class AdsUtils {

    companion object {
        private var nativeAd: NativeAd? = null
        private var interstitialAd: InterstitialAd? = null
        private var appOpenAd: AppOpenAd? = null

        fun loadAndShowBanner(
            activity: Activity,
            frameLayout: FrameLayout,
            shimmerLayout: ShimmerFrameLayout,
            crossBannerLayout: ImageView,
            screenName: String,
            frequencyProvider: FrequencyProvider
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmer()
                        for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                            if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                    if (BaseSplashAdsActivity.adsUnit[i].frequency!! > frequencyProvider.getFrequency()) {
                                        if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                            val adView = AdView(activity)
                                            adView.adUnitId =
                                                if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds!! else AdsApplication.defaultBannerAdId
                                            adView.setAdSize(getAdSize(activity, frameLayout))
                                            adView.loadAd(AdRequest.Builder().build())
                                            frameLayout.removeAllViews()
                                            frameLayout.addView(adView)

                                            adView.adListener = object : AdListener() {
                                                override fun onAdLoaded() {
                                                    super.onAdLoaded()
                                                    Log.e("TAG", "onBannerAdLoaded: ")
                                                    shimmerLayout.stopShimmer()
                                                    crossBannerLayout.visibility = View.GONE
                                                    shimmerLayout.visibility = View.GONE
                                                    frameLayout.visibility = View.VISIBLE
                                                    frequencyProvider.incrementFrequency()
                                                }

                                                override fun onAdFailedToLoad(p0: LoadAdError) {
                                                    super.onAdFailedToLoad(p0)
                                                    Log.e(
                                                        "TAG",
                                                        "onBannerAdFailedToLoad: ${p0.message}"
                                                    )
                                                    if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                        loadAndShowCrossPromotionBanner(
                                                            activity,
                                                            frameLayout,
                                                            shimmerLayout,
                                                            crossBannerLayout,
                                                            frequencyProvider
                                                        )
                                                    } else {
                                                        shimmerLayout.stopShimmer()
                                                        crossBannerLayout.visibility = View.GONE
                                                        shimmerLayout.visibility = View.GONE
                                                        frameLayout.visibility = View.GONE
                                                    }
                                                }
                                            }
                                        } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                            loadAndShowCrossPromotionBanner(
                                                activity,
                                                frameLayout,
                                                shimmerLayout,
                                                crossBannerLayout,
                                                frequencyProvider
                                            )
                                        }
                                    } else {
                                        shimmerLayout.stopShimmer()
                                        crossBannerLayout.visibility = View.GONE
                                        shimmerLayout.visibility = View.GONE
                                        frameLayout.visibility = View.GONE
                                    }
                                } else {
                                    shimmerLayout.stopShimmer()
                                    crossBannerLayout.visibility = View.GONE
                                    shimmerLayout.visibility = View.GONE
                                    frameLayout.visibility = View.GONE
                                }
                            } else {
                                shimmerLayout.stopShimmer()
                                crossBannerLayout.visibility = View.GONE
                                shimmerLayout.visibility = View.GONE
                                frameLayout.visibility = View.GONE
                            }
                        }
                    } else {
                        shimmerLayout.stopShimmer()
                        crossBannerLayout.visibility = View.GONE
                        shimmerLayout.visibility = View.GONE
                        frameLayout.visibility = View.GONE
                    }
                } else {
                    shimmerLayout.stopShimmer()
                    crossBannerLayout.visibility = View.GONE
                    shimmerLayout.visibility = View.GONE
                    frameLayout.visibility = View.GONE
                }
            } else {
                shimmerLayout.stopShimmer()
                crossBannerLayout.visibility = View.GONE
                shimmerLayout.visibility = View.GONE
                frameLayout.visibility = View.GONE
            }
        }

        private fun loadAndShowCrossPromotionBanner(
            activity: Activity,
            frameLayout: FrameLayout,
            shimmerLayout: ShimmerFrameLayout,
            crossBannerLayout: ImageView,
            frequencyProvider: FrequencyProvider
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                crossBannerLayout.visibility = View.VISIBLE
                Glide.with(activity).load(BaseSplashAdsActivity.crossBannerAds?.adMedia)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            shimmerLayout.stopShimmer()
                            crossBannerLayout.visibility = View.GONE
                            shimmerLayout.visibility = View.GONE
                            frameLayout.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            shimmerLayout.stopShimmer()
                            crossBannerLayout.visibility = View.VISIBLE
                            shimmerLayout.visibility = View.GONE
                            frameLayout.visibility = View.GONE
                            frequencyProvider.incrementFrequency()
                            return false
                        }
                    }).into(crossBannerLayout)
            }, 500)
        }

        private fun getAdSize(activity: Activity, frameLayout: FrameLayout): AdSize {
            val display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = frameLayout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }

        @SuppressLint("InflateParams")
        fun loadAndShowNative(
            activity: Activity,
            frameLayout: FrameLayout,
            shimmerLayout: ShimmerFrameLayout,
            screenName: String,
            frequencyProvider: FrequencyProvider
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        val displaySize = getDisplaySize(activity)

                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmer()
                        for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                            if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                    if (BaseSplashAdsActivity.adsUnit[i].frequency!! > frequencyProvider.getFrequency()) {
                                        if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                            val builder = AdLoader.Builder(
                                                activity,
                                                if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds!! else AdsApplication.defaultNativeAdId
                                            )
                                            builder.forNativeAd {
                                                if (activity.isDestroyed) {
                                                    it.destroy()
                                                }
                                                this@Companion.nativeAd = it

                                                if (displaySize.first >= 720 && displaySize.second >= 1344) {
                                                    when (BaseSplashAdsActivity.adsUnit[i].adsLayout) {
                                                        "four" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.small_native_ad_button_right_4,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "four"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "three" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.small_native_ad_bottom_button_3,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "three"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "two" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.big_native_ad_right_button_2,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "two"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "one" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.big_native_ad_1, null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "one"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }
                                                    }
                                                } else {
                                                    val adView = activity.layoutInflater.inflate(
                                                        R.layout.small_native_ad_button_right_4,
                                                        null
                                                    )
                                                    adView.findViewById<View>(R.id.adChoice).visibility =
                                                        View.GONE
                                                    adNativeView(
                                                        it, adView as NativeAdView, "four"
                                                    )
                                                    frameLayout.removeAllViews()
                                                    frameLayout.addView(adView)
                                                }
                                            }

                                            val adLoader =
                                                builder.withAdListener(object : AdListener() {
                                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                                        super.onAdFailedToLoad(loadAdError)
                                                        Log.e(
                                                            "TAG",
                                                            "onNativeAdFailedToLoad: ${loadAdError.message}"
                                                        )
                                                        if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                            loadAndShowCrossPromotionNative(
                                                                activity,
                                                                displaySize.first,
                                                                displaySize.second,
                                                                shimmerLayout,
                                                                frameLayout,
                                                                BaseSplashAdsActivity.adsUnit[i].adsLayout!!,
                                                                frequencyProvider
                                                            )
                                                        } else {
                                                            shimmerLayout.stopShimmer()
                                                            shimmerLayout.visibility = View.GONE
                                                            frameLayout.visibility = View.GONE
                                                        }
                                                    }

                                                    override fun onAdLoaded() {
                                                        super.onAdLoaded()
                                                        Log.e("TAG", "onNativeAdLoaded: ")
                                                        shimmerLayout.stopShimmer()
                                                        shimmerLayout.visibility = View.GONE
                                                        frameLayout.visibility = View.VISIBLE
                                                        frequencyProvider.incrementFrequency()
                                                    }
                                                }).build()
                                            adLoader.loadAd(AdRequest.Builder().build())
                                        } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                            loadAndShowCrossPromotionNative(
                                                activity,
                                                displaySize.first,
                                                displaySize.second,
                                                shimmerLayout,
                                                frameLayout,
                                                BaseSplashAdsActivity.adsUnit[i].adsLayout!!,
                                                frequencyProvider
                                            )
                                        }
                                    } else {
                                        shimmerLayout.stopShimmer()
                                        shimmerLayout.visibility = View.GONE
                                        frameLayout.visibility = View.GONE
                                    }
                                } else {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    frameLayout.visibility = View.GONE
                                }
                            } else {
                                shimmerLayout.stopShimmer()
                                shimmerLayout.visibility = View.GONE
                                frameLayout.visibility = View.GONE
                            }
                        }
                    } else {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        frameLayout.visibility = View.GONE
                    }
                } else {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    frameLayout.visibility = View.GONE
                }
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                frameLayout.visibility = View.GONE
            }
        }

        @SuppressLint("InflateParams")
        private fun loadAndShowCrossPromotionNative(
            activity: Activity,
            widthPixels: Int,
            heightPixels: Int,
            shimmerLayout: ShimmerFrameLayout,
            flNative: FrameLayout,
            adSize: String,
            frequencyProvider: FrequencyProvider
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                flNative.visibility = View.VISIBLE
                if (widthPixels >= 720 && heightPixels >= 1344) {
                    when (adSize) {
                        "four" -> {
                            val adView = activity.layoutInflater.inflate(
                                R.layout.small_native_ad_button_right_4, null
                            )
                            adView.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                            (adView.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView.findViewById<View>(R.id.adCallToAction)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView)
                        }

                        "three" -> {
                            @SuppressLint("InflateParams") val adView2 =
                                activity.layoutInflater.inflate(
                                    R.layout.small_native_ad_bottom_button_3, null
                                )
                            adView2.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView2.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                            (adView2.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView2.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView2.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView2.findViewById<View>(R.id.adCallToAction)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView2.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView2)
                        }

                        "two" -> {
                            @SuppressLint("InflateParams") val adView3 =
                                activity.layoutInflater.inflate(
                                    R.layout.big_native_ad_right_button_2, null
                                )
                            adView3.findViewById<View>(R.id.ad_media_cross).visibility =
                                View.VISIBLE
                            adView3.findViewById<View>(R.id.ad_media).visibility = View.GONE
                            adView3.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adMedia)
                                .into(adView3.findViewById<View>(R.id.ad_media_cross) as AppCompatImageView)
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView3.findViewById<View>(R.id.ad_app_icon) as AppCompatImageView)
                            (adView3.findViewById<View>(R.id.ad_headline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView3.findViewById<View>(R.id.ad_body) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView3.findViewById<View>(R.id.ad_call_to_action) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView3.findViewById<View>(R.id.ad_call_to_action)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView3.findViewById<View>(R.id.ad_media_cross)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView3.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView3)
                        }

                        "one" -> {
                            @SuppressLint("InflateParams") val adView4 =
                                activity.layoutInflater.inflate(R.layout.big_native_ad_1, null)
                            adView4.findViewById<View>(R.id.ad_media_cross).visibility =
                                View.VISIBLE
                            adView4.findViewById<View>(R.id.ad_media).visibility = View.GONE
                            adView4.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adMedia)
                                .into(adView4.findViewById<View>(R.id.ad_media_cross) as AppCompatImageView)
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView4.findViewById<View>(R.id.ad_app_icon) as AppCompatImageView)
                            (adView4.findViewById<View>(R.id.ad_headline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView4.findViewById<View>(R.id.ad_body) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView4.findViewById<View>(R.id.ad_call_to_action) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView4.findViewById<View>(R.id.ad_call_to_action)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView4.findViewById<View>(R.id.ad_media_cross)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView4.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView4)
                        }
                    }
                } else {
                    @SuppressLint("InflateParams") val adView = activity.layoutInflater.inflate(
                        R.layout.small_native_ad_button_right_4, null
                    )
                    adView.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                    Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                        .into(adView.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                    (adView.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                        BaseSplashAdsActivity.crossNativeAds?.adHeadline
                    (adView.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                        BaseSplashAdsActivity.crossNativeAds?.adBody
                    (adView.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                        BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                    adView.findViewById<View>(R.id.adCallToAction).setOnClickListener { v: View? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                        activity.startActivity(intent)
                    }
                    adView.findViewById<View>(R.id.adChoice).setOnClickListener { v: View? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                        activity.startActivity(intent)
                    }
                    frequencyProvider.incrementFrequency()
                    flNative.removeAllViews()
                    flNative.addView(adView)
                }
            }, 500)
        }

        @SuppressLint("InflateParams")
        fun loadAndShowExitNative(
            activity: Activity,
            frameLayout: FrameLayout,
            shimmerLayout: ShimmerFrameLayout,
            imageContainer: ImageView,
            screenName: String,
            frequencyProvider: FrequencyProvider
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        val displaySize = getDisplaySize(activity)

                        shimmerLayout.visibility = View.VISIBLE
                        shimmerLayout.startShimmer()
                        for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                            if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                    if (BaseSplashAdsActivity.adsUnit[i].frequency!! > frequencyProvider.getFrequency()) {
                                        if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                            val builder = AdLoader.Builder(
                                                activity,
                                                if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds!! else AdsApplication.defaultNativeAdId
                                            )
                                            builder.forNativeAd {
                                                if (activity.isDestroyed) {
                                                    it.destroy()
                                                }
                                                this@Companion.nativeAd = it

                                                if (displaySize.first >= 720 && displaySize.second >= 1344) {
                                                    when (BaseSplashAdsActivity.adsUnit[i].adsLayout) {
                                                        "four" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.small_native_ad_button_right_4,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "four"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "three" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.small_native_ad_bottom_button_3,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "three"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "two" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.big_native_ad_right_button_2,
                                                                    null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "two"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }

                                                        "one" -> {
                                                            val adView =
                                                                activity.layoutInflater.inflate(
                                                                    R.layout.big_native_ad_1, null
                                                                )
                                                            adView.findViewById<View>(R.id.adChoice).visibility =
                                                                View.GONE
                                                            adNativeView(
                                                                it, adView as NativeAdView, "one"
                                                            )
                                                            frameLayout.removeAllViews()
                                                            frameLayout.addView(adView)
                                                        }
                                                    }
                                                } else {
                                                    val adView = activity.layoutInflater.inflate(
                                                        R.layout.small_native_ad_button_right_4,
                                                        null
                                                    )
                                                    adView.findViewById<View>(R.id.adChoice).visibility =
                                                        View.GONE
                                                    adNativeView(
                                                        it, adView as NativeAdView, "four"
                                                    )
                                                    frameLayout.removeAllViews()
                                                    frameLayout.addView(adView)
                                                }
                                            }

                                            val adLoader =
                                                builder.withAdListener(object : AdListener() {
                                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                                        super.onAdFailedToLoad(loadAdError)
                                                        Log.e(
                                                            "TAG",
                                                            "onNativeAdFailedToLoad: ${loadAdError.message}"
                                                        )
                                                        if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                            loadAndShowCrossPromotionExitNative(
                                                                activity,
                                                                displaySize.first,
                                                                displaySize.second,
                                                                shimmerLayout,
                                                                frameLayout,
                                                                imageContainer,
                                                                BaseSplashAdsActivity.adsUnit[i].adsLayout!!,
                                                                frequencyProvider
                                                            )
                                                        } else {
                                                            shimmerLayout.stopShimmer()
                                                            shimmerLayout.visibility = View.GONE
                                                            frameLayout.visibility = View.GONE
                                                            imageContainer.visibility = View.VISIBLE
                                                        }
                                                    }

                                                    override fun onAdLoaded() {
                                                        super.onAdLoaded()
                                                        Log.e("TAG", "onNativeAdLoaded: ")
                                                        shimmerLayout.stopShimmer()
                                                        shimmerLayout.visibility = View.GONE
                                                        frameLayout.visibility = View.VISIBLE
                                                        imageContainer.visibility = View.GONE
                                                        frequencyProvider.incrementFrequency()
                                                    }
                                                }).build()
                                            adLoader.loadAd(AdRequest.Builder().build())
                                        } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                            loadAndShowCrossPromotionExitNative(
                                                activity,
                                                displaySize.first,
                                                displaySize.second,
                                                shimmerLayout,
                                                frameLayout,
                                                imageContainer,
                                                BaseSplashAdsActivity.adsUnit[i].adsLayout!!,
                                                frequencyProvider
                                            )
                                        }
                                    } else {
                                        shimmerLayout.stopShimmer()
                                        shimmerLayout.visibility = View.GONE
                                        frameLayout.visibility = View.GONE
                                        imageContainer.visibility = View.VISIBLE
                                    }
                                } else {
                                    shimmerLayout.stopShimmer()
                                    shimmerLayout.visibility = View.GONE
                                    frameLayout.visibility = View.GONE
                                    imageContainer.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        frameLayout.visibility = View.GONE
                        imageContainer.visibility = View.VISIBLE
                    }
                } else {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.visibility = View.GONE
                    frameLayout.visibility = View.GONE
                    imageContainer.visibility = View.VISIBLE
                }
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                frameLayout.visibility = View.GONE
                imageContainer.visibility = View.VISIBLE
            }
        }

        @SuppressLint("InflateParams")
        private fun loadAndShowCrossPromotionExitNative(
            activity: Activity,
            widthPixels: Int,
            heightPixels: Int,
            shimmerLayout: ShimmerFrameLayout,
            flNative: FrameLayout,
            imageContainer: ImageView,
            adSize: String,
            frequencyProvider: FrequencyProvider
        ) {
            Handler(Looper.getMainLooper()).postDelayed({
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                flNative.visibility = View.VISIBLE
                imageContainer.visibility = View.GONE
                if (widthPixels >= 720 && heightPixels >= 1344) {
                    when (adSize) {
                        "four" -> {
                            val adView = activity.layoutInflater.inflate(
                                R.layout.small_native_ad_button_right_4, null
                            )
                            adView.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                            (adView.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView.findViewById<View>(R.id.adCallToAction)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView)
                        }

                        "three" -> {
                            @SuppressLint("InflateParams") val adView2 =
                                activity.layoutInflater.inflate(
                                    R.layout.small_native_ad_bottom_button_3, null
                                )
                            adView2.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView2.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                            (adView2.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView2.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView2.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView2.findViewById<View>(R.id.adCallToAction)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView2.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView2)
                        }

                        "two" -> {
                            @SuppressLint("InflateParams") val adView3 =
                                activity.layoutInflater.inflate(
                                    R.layout.big_native_ad_right_button_2, null
                                )
                            adView3.findViewById<View>(R.id.ad_media_cross).visibility =
                                View.VISIBLE
                            adView3.findViewById<View>(R.id.ad_media).visibility = View.GONE
                            adView3.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adMedia)
                                .into(adView3.findViewById<View>(R.id.ad_media_cross) as AppCompatImageView)
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView3.findViewById<View>(R.id.ad_app_icon) as AppCompatImageView)
                            (adView3.findViewById<View>(R.id.ad_headline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView3.findViewById<View>(R.id.ad_body) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView3.findViewById<View>(R.id.ad_call_to_action) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView3.findViewById<View>(R.id.ad_call_to_action)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView3.findViewById<View>(R.id.ad_media_cross)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView3.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView3)
                        }

                        "one" -> {
                            @SuppressLint("InflateParams") val adView4 =
                                activity.layoutInflater.inflate(R.layout.big_native_ad_1, null)
                            adView4.findViewById<View>(R.id.ad_media_cross).visibility =
                                View.VISIBLE
                            adView4.findViewById<View>(R.id.ad_media).visibility = View.GONE
                            adView4.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                            Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adMedia)
                                .into(adView4.findViewById<View>(R.id.ad_media_cross) as AppCompatImageView)
                            Glide.with(activity)
                                .load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                                .into(adView4.findViewById<View>(R.id.ad_app_icon) as AppCompatImageView)
                            (adView4.findViewById<View>(R.id.ad_headline) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adHeadline
                            (adView4.findViewById<View>(R.id.ad_body) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adBody
                            (adView4.findViewById<View>(R.id.ad_call_to_action) as AppCompatTextView).text =
                                BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                            adView4.findViewById<View>(R.id.ad_call_to_action)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView4.findViewById<View>(R.id.ad_media_cross)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                                    activity.startActivity(intent)
                                }
                            adView4.findViewById<View>(R.id.adChoice)
                                .setOnClickListener { v: View? ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                                    activity.startActivity(intent)
                                }
                            frequencyProvider.incrementFrequency()
                            flNative.removeAllViews()
                            flNative.addView(adView4)
                        }
                    }
                } else {
                    @SuppressLint("InflateParams") val adView = activity.layoutInflater.inflate(
                        R.layout.small_native_ad_button_right_4, null
                    )
                    adView.findViewById<View>(R.id.adChoice).visibility = View.VISIBLE
                    Glide.with(activity).load(BaseSplashAdsActivity.crossNativeAds?.adAppIcon)
                        .into(adView.findViewById<View>(R.id.adAppIcon) as AppCompatImageView)
                    (adView.findViewById<View>(R.id.adHeadline) as AppCompatTextView).text =
                        BaseSplashAdsActivity.crossNativeAds?.adHeadline
                    (adView.findViewById<View>(R.id.adBody) as AppCompatTextView).text =
                        BaseSplashAdsActivity.crossNativeAds?.adBody
                    (adView.findViewById<View>(R.id.adCallToAction) as AppCompatButton).text =
                        BaseSplashAdsActivity.crossNativeAds?.adCallToActionText
                    adView.findViewById<View>(R.id.adCallToAction).setOnClickListener { v: View? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.adCallToActionUrl))
                        activity.startActivity(intent)
                    }
                    adView.findViewById<View>(R.id.adChoice).setOnClickListener { v: View? ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        intent.setData(Uri.parse(BaseSplashAdsActivity.crossNativeAds?.infoUrl))
                        activity.startActivity(intent)
                    }
                    frequencyProvider.incrementFrequency()
                    flNative.removeAllViews()
                    flNative.addView(adView)
                }
            }, 500)
        }

        private fun adNativeView(
            nativeAd: NativeAd, adView: NativeAdView, adSize: String?
        ) {
            when (adSize) {
                "four", "three" -> {
                    adView.headlineView = adView.findViewById(R.id.adHeadline)
                    adView.bodyView = adView.findViewById(R.id.adBody)
                    adView.callToActionView = adView.findViewById(R.id.adCallToAction)
                    adView.iconView = adView.findViewById(R.id.adAppIcon)

                    (adView.headlineView as? AppCompatTextView)?.text = nativeAd.headline
                    (adView.bodyView as? AppCompatTextView)?.text = nativeAd.body
                    if (nativeAd.callToAction == null) {
                        adView.callToActionView?.visibility = View.INVISIBLE
                    } else {
                        adView.callToActionView?.visibility = View.VISIBLE
                        (adView.callToActionView as? AppCompatButton)?.text = nativeAd.callToAction
                    }
                }

                "two", "one" -> {
                    adView.headlineView = adView.findViewById(R.id.ad_headline)
                    adView.mediaView = adView.findViewById(R.id.ad_media)
                    adView.bodyView = adView.findViewById(R.id.ad_body)
                    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
                    adView.iconView = adView.findViewById(R.id.ad_app_icon)

                    (adView.headlineView as? AppCompatTextView)?.text = nativeAd.headline
                    (adView.bodyView as? AppCompatTextView)?.text = nativeAd.body
                    if (nativeAd.callToAction == null) {
                        adView.callToActionView?.visibility = View.INVISIBLE
                    } else {
                        adView.callToActionView?.visibility = View.VISIBLE
                        (adView.callToActionView as? AppCompatTextView)?.text =
                            nativeAd.callToAction
                    }
                }
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as? AppCompatImageView)?.setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView!!.visibility = View.VISIBLE
            }

            adView.setNativeAd(nativeAd)
        }

        private fun getDisplaySize(activity: Activity): Pair<Int, Int> {
            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val widthPixels = displayMetrics.widthPixels
            val heightPixels = displayMetrics.heightPixels
            return Pair(widthPixels, heightPixels)
        }

        fun loadInterstitialAd(activity: Activity) {
            val adsId = AdsApplication.getInterstitialAdId()
                .ifEmpty { AdsApplication.defaultInterstitialAdId }
            InterstitialAd.load(activity,
                adsId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        super.onAdLoaded(interstitialAd)
                        Log.e("TAG", "onInterstitialAdLoaded: ")
                        this@Companion.interstitialAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                        Log.e("TAG", "onInterstitialAdFailedToLoad: ${loadAdError.message}")
                        interstitialAd = null
                    }
                })
        }

        fun showInterstitialAd(
            activity: Activity,
            screenName: String,
            interstitialAdShowedListener: InterstitialAdShowedListener
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        val timeDifference =
                            Calendar.getInstance().timeInMillis - AdsApplication.getLastAdShowedTime()
                        if (AdsApplication.getTimingAd()) {
                            if (AdsApplication.getHomeScreenAds()) {
                                for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                    if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                        if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                            if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                                if (interstitialAd != null) {
                                                    interstitialAd?.show(activity)
                                                    AdsApplication.setLastAdShowedTime(Calendar.getInstance().timeInMillis)
                                                    AdsApplication.appOpenManager?.isAdShow = true
                                                    interstitialAd?.fullScreenContentCallback =
                                                        object : FullScreenContentCallback() {
                                                            override fun onAdDismissedFullScreenContent() {
                                                                super.onAdDismissedFullScreenContent()
                                                                interstitialAd = null
                                                                AdsApplication.adsClickCounter = 0
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                AdsApplication.appOpenManager?.isAdShow =
                                                                    false
                                                                interstitialAdShowedListener.onCompleted()
                                                                loadInterstitialAd(activity)
                                                            }

                                                            override fun onAdFailedToShowFullScreenContent(
                                                                p0: AdError
                                                            ) {
                                                                super.onAdFailedToShowFullScreenContent(
                                                                    p0
                                                                )
                                                                interstitialAd = null
                                                                AdsApplication.adsClickCounter = 0
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                AdsApplication.appOpenManager?.isAdShow =
                                                                    false
                                                                interstitialAdShowedListener.onCompleted()
                                                            }
                                                        }
                                                } else {
                                                    if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                        val appCompatActivity =
                                                            activity as? AppCompatActivity
                                                        appCompatActivity?.supportFragmentManager?.let {
                                                            val crossAdInterstitial =
                                                                CrossAdInterstitial()
                                                            crossAdInterstitial.setCrossPromotionListener(
                                                                object :
                                                                    CrossInterstitialAdShowedListener {
                                                                    override fun onCompleted() {
                                                                        AdsApplication.setHomeScreenAds(
                                                                            false
                                                                        )
                                                                        interstitialAdShowedListener.onCompleted()
                                                                    }
                                                                })
                                                            crossAdInterstitial.show(
                                                                it,
                                                                Utils.CROSS_PROMOTION_INTERSTITIAL
                                                            )
                                                        }
                                                    } else {
                                                        interstitialAdShowedListener.onCompleted()
                                                    }
                                                }
                                            } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                                val appCompatActivity =
                                                    activity as? AppCompatActivity
                                                appCompatActivity?.supportFragmentManager?.let {
                                                    val crossAdInterstitial = CrossAdInterstitial()
                                                    crossAdInterstitial.setCrossPromotionListener(
                                                        object :
                                                            CrossInterstitialAdShowedListener {
                                                            override fun onCompleted() {
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                interstitialAdShowedListener.onCompleted()
                                                            }
                                                        })
                                                    crossAdInterstitial.show(
                                                        it, Utils.CROSS_PROMOTION_INTERSTITIAL
                                                    )
                                                }
                                            }
                                        } else {
                                            interstitialAdShowedListener.onCompleted()
                                        }
                                    } else {
                                        interstitialAdShowedListener.onCompleted()
                                    }
                                }
                            } else {
                                if (timeDifference >= AdsApplication.getShowTime()) {
                                    for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                        if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                            if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                                if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                                    if (interstitialAd != null) {
                                                        interstitialAd?.show(activity)
                                                        AdsApplication.setLastAdShowedTime(Calendar.getInstance().timeInMillis)
                                                        interstitialAd?.fullScreenContentCallback =
                                                            object : FullScreenContentCallback() {
                                                                override fun onAdDismissedFullScreenContent() {
                                                                    super.onAdDismissedFullScreenContent()
                                                                    interstitialAd = null
                                                                    AdsApplication.adsClickCounter =
                                                                        0
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                    interstitialAdShowedListener.onCompleted()
                                                                    loadInterstitialAd(activity)
                                                                }

                                                                override fun onAdFailedToShowFullScreenContent(
                                                                    p0: AdError
                                                                ) {
                                                                    super.onAdFailedToShowFullScreenContent(
                                                                        p0
                                                                    )
                                                                    interstitialAd = null
                                                                    AdsApplication.adsClickCounter =
                                                                        0
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                    interstitialAdShowedListener.onCompleted()
                                                                }
                                                            }
                                                    } else {
                                                        if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                            val appCompatActivity =
                                                                activity as? AppCompatActivity
                                                            appCompatActivity?.supportFragmentManager?.let {
                                                                val crossAdInterstitial =
                                                                    CrossAdInterstitial()
                                                                crossAdInterstitial.setCrossPromotionListener(
                                                                    object :
                                                                        CrossInterstitialAdShowedListener {
                                                                        override fun onCompleted() {
                                                                            interstitialAdShowedListener.onCompleted()
                                                                        }
                                                                    })
                                                                crossAdInterstitial.show(
                                                                    it,
                                                                    Utils.CROSS_PROMOTION_INTERSTITIAL
                                                                )
                                                                AdsApplication.setLastAdShowedTime(
                                                                    Calendar.getInstance().timeInMillis
                                                                )
                                                            }
                                                        } else {
                                                            interstitialAdShowedListener.onCompleted()
                                                        }
                                                    }
                                                } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                                    val appCompatActivity =
                                                        activity as? AppCompatActivity
                                                    appCompatActivity?.supportFragmentManager?.let {
                                                        val crossAdInterstitial =
                                                            CrossAdInterstitial()
                                                        crossAdInterstitial.setCrossPromotionListener(
                                                            object :
                                                                CrossInterstitialAdShowedListener {
                                                                override fun onCompleted() {
                                                                    interstitialAdShowedListener.onCompleted()
                                                                }
                                                            })
                                                        crossAdInterstitial.show(
                                                            it, Utils.CROSS_PROMOTION_INTERSTITIAL
                                                        )
                                                        AdsApplication.setLastAdShowedTime(Calendar.getInstance().timeInMillis)
                                                    }
                                                }
                                            } else {
                                                interstitialAdShowedListener.onCompleted()
                                            }
                                        } else {
                                            interstitialAdShowedListener.onCompleted()
                                        }
                                    }
                                } else {
                                    interstitialAdShowedListener.onCompleted()
                                }
                            }
                        } else {
                            if (AdsApplication.getHomeScreenAds()) {
                                for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                    if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                        if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                            if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                                if (interstitialAd != null) {
                                                    interstitialAd?.show(activity)
                                                    AdsApplication.appOpenManager?.isAdShow = true
                                                    interstitialAd?.fullScreenContentCallback =
                                                        object : FullScreenContentCallback() {
                                                            override fun onAdDismissedFullScreenContent() {
                                                                super.onAdDismissedFullScreenContent()
                                                                interstitialAd = null
                                                                AdsApplication.adsClickCounter = 0
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                AdsApplication.appOpenManager?.isAdShow =
                                                                    false
                                                                interstitialAdShowedListener.onCompleted()
                                                                loadInterstitialAd(activity)
                                                            }

                                                            override fun onAdFailedToShowFullScreenContent(
                                                                p0: AdError
                                                            ) {
                                                                super.onAdFailedToShowFullScreenContent(
                                                                    p0
                                                                )
                                                                interstitialAd = null
                                                                AdsApplication.adsClickCounter = 0
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                AdsApplication.appOpenManager?.isAdShow =
                                                                    false
                                                                interstitialAdShowedListener.onCompleted()
                                                            }
                                                        }
                                                } else {
                                                    if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                        val appCompatActivity =
                                                            activity as? AppCompatActivity
                                                        appCompatActivity?.supportFragmentManager?.let {
                                                            val crossAdInterstitial =
                                                                CrossAdInterstitial()
                                                            crossAdInterstitial.setCrossPromotionListener(
                                                                object :
                                                                    CrossInterstitialAdShowedListener {
                                                                    override fun onCompleted() {
                                                                        AdsApplication.setHomeScreenAds(
                                                                            false
                                                                        )
                                                                        interstitialAdShowedListener.onCompleted()
                                                                    }
                                                                })
                                                            crossAdInterstitial.show(
                                                                it,
                                                                Utils.CROSS_PROMOTION_INTERSTITIAL
                                                            )
                                                        }
                                                    } else {
                                                        interstitialAdShowedListener.onCompleted()
                                                    }
                                                }
                                            } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                                val appCompatActivity =
                                                    activity as? AppCompatActivity
                                                appCompatActivity?.supportFragmentManager?.let {
                                                    val crossAdInterstitial = CrossAdInterstitial()
                                                    crossAdInterstitial.setCrossPromotionListener(
                                                        object :
                                                            CrossInterstitialAdShowedListener {
                                                            override fun onCompleted() {
                                                                AdsApplication.setHomeScreenAds(
                                                                    false
                                                                )
                                                                interstitialAdShowedListener.onCompleted()
                                                            }
                                                        })
                                                    crossAdInterstitial.show(
                                                        it, Utils.CROSS_PROMOTION_INTERSTITIAL
                                                    )
                                                }
                                            }
                                        } else {
                                            interstitialAdShowedListener.onCompleted()
                                        }
                                    } else {
                                        interstitialAdShowedListener.onCompleted()
                                    }
                                }
                            } else {
                                if (AdsApplication.adsClickCounter >= AdsApplication.getUserClickCounter()) {
                                    for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                        if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                            if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                                if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                                    if (interstitialAd != null) {
                                                        interstitialAd?.show(activity)
                                                        interstitialAd?.fullScreenContentCallback =
                                                            object : FullScreenContentCallback() {
                                                                override fun onAdDismissedFullScreenContent() {
                                                                    super.onAdDismissedFullScreenContent()
                                                                    interstitialAd = null
                                                                    AdsApplication.adsClickCounter =
                                                                        0
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                    interstitialAdShowedListener.onCompleted()
                                                                    loadInterstitialAd(activity)
                                                                }

                                                                override fun onAdFailedToShowFullScreenContent(
                                                                    p0: AdError
                                                                ) {
                                                                    super.onAdFailedToShowFullScreenContent(
                                                                        p0
                                                                    )
                                                                    interstitialAd = null
                                                                    AdsApplication.adsClickCounter =
                                                                        0
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                    interstitialAdShowedListener.onCompleted()
                                                                }
                                                            }
                                                    } else {
                                                        if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                            val appCompatActivity =
                                                                activity as? AppCompatActivity
                                                            appCompatActivity?.supportFragmentManager?.let {
                                                                val crossAdInterstitial =
                                                                    CrossAdInterstitial()
                                                                crossAdInterstitial.setCrossPromotionListener(
                                                                    object :
                                                                        CrossInterstitialAdShowedListener {
                                                                        override fun onCompleted() {
                                                                            interstitialAdShowedListener.onCompleted()
                                                                        }
                                                                    })
                                                                crossAdInterstitial.show(
                                                                    it,
                                                                    Utils.CROSS_PROMOTION_INTERSTITIAL
                                                                )
                                                            }
                                                        } else {
                                                            interstitialAdShowedListener.onCompleted()
                                                        }
                                                    }
                                                } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                                    val appCompatActivity =
                                                        activity as? AppCompatActivity
                                                    appCompatActivity?.supportFragmentManager?.let {
                                                        val crossAdInterstitial =
                                                            CrossAdInterstitial()
                                                        crossAdInterstitial.setCrossPromotionListener(
                                                            object :
                                                                CrossInterstitialAdShowedListener {
                                                                override fun onCompleted() {
                                                                    interstitialAdShowedListener.onCompleted()
                                                                }
                                                            })
                                                        crossAdInterstitial.show(
                                                            it, Utils.CROSS_PROMOTION_INTERSTITIAL
                                                        )
                                                    }
                                                }
                                            } else {
                                                interstitialAdShowedListener.onCompleted()
                                            }
                                        } else {
                                            interstitialAdShowedListener.onCompleted()
                                        }
                                    }
                                } else {
                                    interstitialAdShowedListener.onCompleted()
                                }
                            }
                        }
                    } else {
                        interstitialAdShowedListener.onCompleted()
                    }
                } else {
                    interstitialAdShowedListener.onCompleted()
                }
            } else {
                interstitialAdShowedListener.onCompleted()
            }
        }

        fun showPremiumCloseInterstitialAd(
            activity: Activity,
            screenName: String,
            interstitialAdShowedListener: InterstitialAdShowedListener
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        if (AdsApplication.getPremiumFirstTimeAds()) {
                            for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                    if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                        if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                            if (interstitialAd != null) {
                                                interstitialAd?.show(activity)
                                                AdsApplication.appOpenManager?.isAdShow = true
                                                interstitialAd?.fullScreenContentCallback =
                                                    object : FullScreenContentCallback() {
                                                        override fun onAdDismissedFullScreenContent() {
                                                            super.onAdDismissedFullScreenContent()
                                                            interstitialAd = null
                                                            AdsApplication.adsClickCounter = 0
                                                            AdsApplication.setPremiumFirstTimeAds(
                                                                false
                                                            )
                                                            AdsApplication.appOpenManager?.isAdShow =
                                                                false
                                                            interstitialAdShowedListener.onCompleted()
                                                            loadInterstitialAd(activity)
                                                        }

                                                        override fun onAdFailedToShowFullScreenContent(
                                                            p0: AdError
                                                        ) {
                                                            super.onAdFailedToShowFullScreenContent(
                                                                p0
                                                            )
                                                            interstitialAd = null
                                                            AdsApplication.adsClickCounter = 0
                                                            AdsApplication.setPremiumFirstTimeAds(
                                                                false
                                                            )
                                                            AdsApplication.appOpenManager?.isAdShow =
                                                                false
                                                            interstitialAdShowedListener.onCompleted()
                                                        }
                                                    }
                                            } else {
                                                if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                    val appCompatActivity =
                                                        activity as? AppCompatActivity
                                                    appCompatActivity?.supportFragmentManager?.let {
                                                        val crossAdInterstitial =
                                                            CrossAdInterstitial()
                                                        crossAdInterstitial.setCrossPromotionListener(
                                                            object :
                                                                CrossInterstitialAdShowedListener {
                                                                override fun onCompleted() {
                                                                    AdsApplication.setPremiumFirstTimeAds(
                                                                        false
                                                                    )
                                                                    interstitialAdShowedListener.onCompleted()
                                                                }
                                                            })
                                                        crossAdInterstitial.show(
                                                            it,
                                                            Utils.CROSS_PROMOTION_INTERSTITIAL
                                                        )
                                                    }
                                                } else {
                                                    interstitialAdShowedListener.onCompleted()
                                                }
                                            }
                                        } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                            val appCompatActivity =
                                                activity as? AppCompatActivity
                                            appCompatActivity?.supportFragmentManager?.let {
                                                val crossAdInterstitial = CrossAdInterstitial()
                                                crossAdInterstitial.setCrossPromotionListener(
                                                    object :
                                                        CrossInterstitialAdShowedListener {
                                                        override fun onCompleted() {
                                                            AdsApplication.setPremiumFirstTimeAds(
                                                                false
                                                            )
                                                            interstitialAdShowedListener.onCompleted()
                                                        }
                                                    })
                                                crossAdInterstitial.show(
                                                    it, Utils.CROSS_PROMOTION_INTERSTITIAL
                                                )
                                            }
                                        }
                                    } else {
                                        interstitialAdShowedListener.onCompleted()
                                    }
                                } else {
                                    interstitialAdShowedListener.onCompleted()
                                }
                            }
                        } else {
                            interstitialAdShowedListener.onCompleted()
                        }
                    } else {
                        interstitialAdShowedListener.onCompleted()
                    }
                } else {
                    interstitialAdShowedListener.onCompleted()
                }
            } else {
                interstitialAdShowedListener.onCompleted()
            }
        }

        fun loadAndShowAppOpenAd(
            activity: Activity, screenName: String, appOpenAdShowedListener: AppOpenAdShowedListener
        ) {
            if (AdsApplication.isNetworkAvailable(activity)) {
                if (!AdsApplication.isPremium()) {
                    if (AdsApplication.getShowAds()) {
                        val timeDifference =
                            Calendar.getInstance().timeInMillis - AdsApplication.getLastAdShowedTime()
                        if (AdsApplication.getTimingAd()) {
                            if (timeDifference >= AdsApplication.getShowTime()) {
                                for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                    if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                        if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                            if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                                val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
                                                    object : AppOpenAd.AppOpenAdLoadCallback() {
                                                        override fun onAdLoaded(ad: AppOpenAd) {
                                                            Log.e("TAG", "onAppOpenAdLoaded: ")
                                                            appOpenAd = ad
                                                            appOpenAd?.show(activity)
                                                            AdsApplication.setLastAdShowedTime(
                                                                Calendar.getInstance().timeInMillis
                                                            )
                                                            AdsApplication.appOpenManager?.isAdShow =
                                                                true
                                                            ad.fullScreenContentCallback =
                                                                object :
                                                                    FullScreenContentCallback() {
                                                                    override fun onAdDismissedFullScreenContent() {
                                                                        appOpenAd = null
                                                                        appOpenAdShowedListener.onCompleted()
                                                                        AdsApplication.appOpenManager?.isAdShow =
                                                                            false
                                                                    }

                                                                    override fun onAdFailedToShowFullScreenContent(
                                                                        adError: AdError
                                                                    ) {
                                                                        super.onAdFailedToShowFullScreenContent(
                                                                            adError
                                                                        )
                                                                        appOpenAd = null
                                                                        appOpenAdShowedListener.onCompleted()
                                                                        AdsApplication.appOpenManager?.isAdShow =
                                                                            false
                                                                    }
                                                                }
                                                        }

                                                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                                            Log.e(
                                                                "TAG",
                                                                "onAppOpenAdFailedToLoad: ${loadAdError.message}"
                                                            )
                                                            if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                                showCrossAppOpenAds(
                                                                    activity,
                                                                    appOpenAdShowedListener
                                                                )
                                                            } else {
                                                                appOpenAd = null
                                                                appOpenAdShowedListener.onCompleted()
                                                                AdsApplication.appOpenManager?.isAdShow =
                                                                    false
                                                            }
                                                        }
                                                    }
                                                AppOpenAd.load(
                                                    activity,
                                                    if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds!! else AdsApplication.defaultNativeAdId,
                                                    AdRequest.Builder().build(),
                                                    loadCallback
                                                )
                                            } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                                showCrossAppOpenAds(
                                                    activity, appOpenAdShowedListener
                                                )
                                            }
                                        } else {
                                            appOpenAdShowedListener.onCompleted()
                                        }
                                    } else {
                                        appOpenAdShowedListener.onCompleted()
                                    }
                                }
                            } else {
                                appOpenAdShowedListener.onCompleted()
                            }
                        } else {
                            for (i in 0 until BaseSplashAdsActivity.adsUnit.size) {
                                if (BaseSplashAdsActivity.adsUnit[i].adsName == screenName) {
                                    if (BaseSplashAdsActivity.adsUnit[i].enableAds!!) {
                                        if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.AD_UNIT) {
                                            val loadCallback: AppOpenAd.AppOpenAdLoadCallback =
                                                object : AppOpenAd.AppOpenAdLoadCallback() {
                                                    override fun onAdLoaded(ad: AppOpenAd) {
                                                        Log.e("TAG", "onAppOpenAdLoaded: ")
                                                        appOpenAd = ad
                                                        appOpenAd?.show(activity)
                                                        AdsApplication.appOpenManager?.isAdShow =
                                                            true
                                                        ad.fullScreenContentCallback =
                                                            object : FullScreenContentCallback() {
                                                                override fun onAdDismissedFullScreenContent() {
                                                                    appOpenAd = null
                                                                    appOpenAdShowedListener.onCompleted()
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                }

                                                                override fun onAdFailedToShowFullScreenContent(
                                                                    adError: AdError
                                                                ) {
                                                                    super.onAdFailedToShowFullScreenContent(
                                                                        adError
                                                                    )
                                                                    appOpenAd = null
                                                                    appOpenAdShowedListener.onCompleted()
                                                                    AdsApplication.appOpenManager?.isAdShow =
                                                                        false
                                                                }
                                                            }
                                                    }

                                                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                                        Log.e(
                                                            "TAG",
                                                            "onAppOpenAdFailedToLoad: ${loadAdError.message}"
                                                        )
                                                        if (BaseSplashAdsActivity.adsUnit[i].adFailed == Utils.CROSS_PROMOTION) {
                                                            showCrossAppOpenAds(
                                                                activity, appOpenAdShowedListener
                                                            )
                                                        } else {
                                                            appOpenAd = null
                                                            appOpenAdShowedListener.onCompleted()
                                                            AdsApplication.appOpenManager?.isAdShow =
                                                                false
                                                        }
                                                    }
                                                }
                                            AppOpenAd.load(
                                                activity,
                                                if (BaseSplashAdsActivity.adsUnit[i].idAds != null) BaseSplashAdsActivity.adsUnit[i].idAds!! else AdsApplication.defaultNativeAdId,
                                                AdRequest.Builder().build(),
                                                loadCallback
                                            )
                                        } else if (BaseSplashAdsActivity.adsUnit[i].publishers == Utils.CROSS_PROMOTION) {
                                            showCrossAppOpenAds(
                                                activity, appOpenAdShowedListener
                                            )
                                        }
                                    } else {
                                        appOpenAdShowedListener.onCompleted()
                                    }
                                } else {
                                    appOpenAdShowedListener.onCompleted()
                                }
                            }
                        }
                    } else {
                        appOpenAdShowedListener.onCompleted()
                    }
                } else {
                    appOpenAdShowedListener.onCompleted()
                }
            } else {
                appOpenAdShowedListener.onCompleted()
            }
        }

        private fun showCrossAppOpenAds(
            activity: Activity, appOpenAdShowedListener: AppOpenAdShowedListener
        ) {
            val fullScreenDialog =
                Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            fullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            fullScreenDialog.setContentView(R.layout.cross_promotion_app_open_ad_layout)
            fullScreenDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val imgAdIcon = fullScreenDialog.findViewById<ImageView>(R.id.imgAdIcon)
            val textContinueToApp = fullScreenDialog.findViewById<TextView>(R.id.textContinueToApp)
            val textAdName = fullScreenDialog.findViewById<TextView>(R.id.textAdName)
            val imgAdClose = fullScreenDialog.findViewById<ImageView>(R.id.imgAdClose)
            val adTopLayout = fullScreenDialog.findViewById<RelativeLayout>(R.id.adTopLayout)
            val adBottomLayout = fullScreenDialog.findViewById<RelativeLayout>(R.id.adBottomLayout)
            val imgAdMedia = fullScreenDialog.findViewById<ImageView>(R.id.imgAdMedia)
            Glide.with(activity).load(BaseSplashAdsActivity.crossOpenAppAds?.adAppIcon)
                .into(imgAdIcon)
            Glide.with(activity).load(BaseSplashAdsActivity.crossOpenAppAds?.adMedia)
                .into(imgAdMedia)
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
                appOpenAdShowedListener.onCompleted()
                AdsApplication.appOpenManager?.isAdShow = false
            }
            adBottomLayout.setOnClickListener {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            BaseSplashAdsActivity.crossOpenAppAds?.adCallToActionUrl
                        )
                    )
                )
            }
            fullScreenDialog.setOnDismissListener {
                AdsApplication.appOpenManager?.isAdShow = false
                it.dismiss()
                appOpenAdShowedListener.onCompleted()
            }
            if (!activity.isFinishing && !fullScreenDialog.isShowing) {
                fullScreenDialog.show()
                AdsApplication.setLastAdShowedTime(Calendar.getInstance().timeInMillis)
                AdsApplication.appOpenManager?.isAdShow = true
            }
        }

        fun destroyNativeAd() {
            nativeAd?.destroy()
        }

        fun sendFirebaseEvent(context: Context, eventName: String, paramEventName: String) {
            FirebaseAnalytics.getInstance(context).logEvent(eventName) {
                param("tracking_id", paramEventName)
            }
        }
    }
}