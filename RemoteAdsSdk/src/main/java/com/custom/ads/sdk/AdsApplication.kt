package com.custom.ads.sdk

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.google.android.gms.ads.MobileAds


open class AdsApplication : Application() {
    private val adsPreferencesKey: String = "Ads_Preference"

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)

        appOpenManager = AppOpenManager(this)
        adsSharedPreferences = getSharedPreferences(adsPreferencesKey, Context.MODE_PRIVATE)
        adsEditor = adsSharedPreferences?.edit()
    }

    companion object {
        var defaultAppOpenAdId: String = ""
        var defaultBannerAdId: String = ""
        var defaultNativeAdId: String = ""
        var defaultInterstitialAdId: String = ""

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

        fun setPremiumFirstTimeAds(premiumFirstTimeAds: Boolean) {
            adsEditor?.putBoolean("premium_first_time_ads", premiumFirstTimeAds)?.apply()
        }

        fun getPremiumFirstTimeAds(): Boolean {
            return adsSharedPreferences?.getBoolean("premium_first_time_ads", false) ?: false
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

        fun setWeeklyPremiumKey(weeklyPremiumKey: String) {
            adsEditor?.putString("weekly_premium_key", weeklyPremiumKey)?.apply()
        }

        fun getWeeklyPremiumKey(): String {
            return adsSharedPreferences?.getString("weekly_premium_key", "weekly_key")
                ?: "weekly_key"
        }

        fun setMonthlyPremiumKey(monthlyPremiumKey: String) {
            adsEditor?.putString("monthly_premium_key", monthlyPremiumKey)?.apply()
        }

        fun getMonthlyPremiumKey(): String {
            return adsSharedPreferences?.getString("monthly_premium_key", "monthly_key")
                ?: "monthly_key"
        }

        fun setYearlyPremiumKey(yearlyPremiumKey: String) {
            adsEditor?.putString("yearly_premium_key", yearlyPremiumKey)?.apply()
        }

        fun getYearlyPremiumKey(): String {
            return adsSharedPreferences?.getString("yearly_premium_key", "yearly_key")
                ?: "yearly_key"
        }

        fun setWeeklyPrice(weeklyPrice: String) {
            adsEditor?.putString("weekly_price", weeklyPrice)?.apply()
        }

        fun getWeeklyPrice(): String {
            return adsSharedPreferences?.getString("weekly_price", "199") ?: "199"
        }

        fun setMonthlyPrice(monthlyPrice: String) {
            adsEditor?.putString("monthly_price", monthlyPrice)?.apply()
        }

        fun getMonthlyPrice(): String {
            return adsSharedPreferences?.getString("monthly_price", "199") ?: "199"
        }

        fun setYearlyPrice(yearlyPrice: String) {
            adsEditor?.putString("yearly_price", yearlyPrice)?.apply()
        }

        fun getYearlyPrice(): String {
            return adsSharedPreferences?.getString("yearly_price", "1449") ?: "1449"
        }

        fun setTimingAd(timingAd: Boolean) {
            adsEditor?.putBoolean("timing_ad", timingAd)?.apply()
        }

        fun getTimingAd(): Boolean {
            return adsSharedPreferences?.getBoolean("timing_ad", false) ?: false
        }

        fun setShowTime(showTime: Long) {
            adsEditor?.putLong("show_time", showTime)?.apply()
        }

        fun getShowTime(): Long {
            return adsSharedPreferences?.getLong("show_time", 0L) ?: 0L
        }

        fun setLastAdShowedTime(lastAdShowedTime: Long) {
            adsEditor?.putLong("ad_showed_time", lastAdShowedTime)?.apply()
        }

        fun getLastAdShowedTime(): Long {
            return adsSharedPreferences?.getLong("ad_showed_time", 0L) ?: 0L
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected && (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE || activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI)
        }
    }
}