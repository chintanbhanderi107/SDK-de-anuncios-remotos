package com.custom.ads.sdk

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class AdsApplication : Application() {
    private val adsPreferencesKey: String = "Ads_Preference"

    override fun onCreate() {
        super.onCreate()
        appOpenManager = AppOpenManager(this)
        adsSharedPreferences = getSharedPreferences(adsPreferencesKey, Context.MODE_PRIVATE)
        adsEditor = adsSharedPreferences?.edit()
    }

    companion object {
        var appOpenAdId: String = ""
        var bannerAdId: String = ""
        var nativeAdId: String = ""
        var interstitialAdId: String = ""
        var adsClickCounter: Int = 0
        var adsSharedPreferences: SharedPreferences? = null
        var adsEditor: SharedPreferences.Editor? = null
        var appOpenManager: AppOpenManager? = null

        fun setPublisherId(publisherId: Int) {
            adsEditor?.putInt("publisher_id", publisherId)?.apply()
        }

        fun getPublisherId(): Int {
            return adsSharedPreferences?.getInt("publisher_id", 0) ?: 0
        }

        fun setUpdatedStatus(updatedStatus: Boolean) {
            adsEditor?.putBoolean("updated_status", updatedStatus)?.apply()
        }

        fun getUpdatedStatus(): Boolean {
            return adsSharedPreferences?.getBoolean("updated_status", false) ?: false
        }

        fun setAppLiveStatus(appLiveStatus: Boolean) {
            adsEditor?.putBoolean("app_live_status", appLiveStatus)?.apply()
        }

        fun getAppLiveStatus(): Boolean {
            return adsSharedPreferences?.getBoolean("app_live_status", false) ?: false
        }

        fun setPublisherName(publisherName: String) {
            adsEditor?.putString("publisher_name", publisherName)?.apply()
        }

        fun getPublisherName(): String {
            return adsSharedPreferences?.getString("publisher_name", "no") ?: "no"
        }

        fun setNewPackage(newPackage: String) {
            adsEditor?.putString("new_package", newPackage)?.apply()
        }

        fun getNewPackage(): String {
            return adsSharedPreferences?.getString("new_package", "") ?: ""
        }

        fun setVersionCode(versionCode: Int) {
            adsEditor?.putInt("version_code", versionCode)?.apply()
        }

        fun getVersionCode(): Int {
            return adsSharedPreferences?.getInt("version_code", 0) ?: 0
        }

        fun setShowAds(showAds: Boolean) {
            adsEditor?.putBoolean("show_ads", showAds)?.apply()
        }

        fun getShowAds(): Boolean {
            return adsSharedPreferences?.getBoolean("show_ads", false) ?: false
        }

        fun setHomeScreenAds(homeScreenAds: Boolean) {
            adsEditor?.putBoolean("home_screen_ads", homeScreenAds)?.apply()
        }

        fun getHomeScreenAds(): Boolean {
            return adsSharedPreferences?.getBoolean("home_screen_ads", false) ?: false
        }

        fun setUpdatedType(updatedType: Int) {
            adsEditor?.putInt("updated_type", updatedType)?.apply()
        }

        fun getUpdatedType(): Int {
            return adsSharedPreferences?.getInt("updated_type", 0) ?: 0
        }

        fun setUserClickCounter(userClickCounter: Int) {
            adsEditor?.putInt("user_click_counter", userClickCounter)?.apply()
        }

        fun getUserClickCounter(): Int {
            return adsSharedPreferences?.getInt("user_click_counter", 0) ?: 0
        }

        fun setAppId(appId: String) {
            adsEditor?.putString("app_id", appId)?.apply()
        }

        fun getAppId(): String {
            return adsSharedPreferences?.getString("app_id", "") ?: ""
        }

        fun setInterstitialAdId(appId: String) {
            adsEditor?.putString("interstitial_ad_id", appId)?.apply()
        }

        fun getInterstitialAdId(): String {
            return adsSharedPreferences?.getString("interstitial_ad_id", "") ?: ""
        }

        fun setCrossNativeAds(native: String) {
            adsEditor?.putString("native", native)?.apply()
        }

        fun getCrossNativeAds(): String {
            return adsSharedPreferences?.getString("native", "") ?: ""
        }

        fun setCrossBannerAds(banner: String) {
            adsEditor?.putString("banner", banner)?.apply()
        }

        fun getCrossBannerAds(): String {
            return adsSharedPreferences?.getString("banner", "") ?: ""
        }

        fun setCrossInterstitialAds(interstitial: String) {
            adsEditor?.putString("interstitial", interstitial)?.apply()
        }

        fun getCrossInterstitialAds(): String {
            return adsSharedPreferences?.getString("interstitial", "") ?: ""
        }

        fun setCrossOpenAppAds(openapp: String) {
            adsEditor?.putString("openapp", openapp)?.apply()
        }

        fun getCrossOpenAppAds(): String {
            return adsSharedPreferences?.getString("openapp", "") ?: ""
        }

        fun setAdsUnits(adUnits: String) {
            adsEditor?.putString("ad_units", adUnits)?.apply()
        }

        fun getAdsUnits(): String {
            return adsSharedPreferences?.getString("ad_units", "") ?: ""
        }

        fun setPremium(isPremium: Boolean) {
            adsEditor?.putBoolean("is_premium", isPremium)?.apply()
        }

        fun isPremium(): Boolean {
            return adsSharedPreferences?.getBoolean("is_premium", false) ?: false
        }
    }
}