package com.custom.ads.sdk

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.custom.ads.sdk.adsUtils.AdsUtils
import com.custom.ads.sdk.interfaces.DataLoadCompleteListener
import com.custom.ads.sdk.model.RemoteAds
import com.custom.ads.sdk.utils.Utils
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

abstract class BaseAdsActivity : AppCompatActivity() {
    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 0
    }

    abstract fun getLayoutResourceId(): Int
    abstract fun onCompleted()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        initData(object : DataLoadCompleteListener {
            override fun onDataLoaded() {
                adsUnit = Gson().fromJson(
                    AdsApplication.getAdsUnits(),
                    object : TypeToken<List<RemoteAds.AdUnit>>() {}.type
                )

                crossNativeAds = Gson().fromJson(
                    AdsApplication.getCrossNativeAds(), RemoteAds.Native::class.java
                )
                crossBannerAds = Gson().fromJson(
                    AdsApplication.getCrossBannerAds(), RemoteAds.Banner::class.java
                )
                crossInterstitialAds = Gson().fromJson(
                    AdsApplication.getCrossInterstitialAds(),
                    RemoteAds.Interstitial::class.java
                )
                crossOpenAppAds = Gson().fromJson(
                    AdsApplication.getCrossOpenAppAds(), RemoteAds.Openapp::class.java
                )

                AdsUtils.loadInterstitialAd(this@BaseAdsActivity)

                onCompleted()
            }

            override fun onDataLoadFailed() {
                onCompleted()
            }
        })
    }

    private fun initData(listener: DataLoadCompleteListener) {
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val jsonObject = JSONObject(remoteConfig.getString(Utils.firebaseKey))

                AdsApplication.setPublisherId(jsonObject.getInt("publisher_id"))
                AdsApplication.setUpdatedStatus(jsonObject.getBoolean("updated_status"))
                AdsApplication.setAppLiveStatus(jsonObject.getBoolean("app_live_status"))
                AdsApplication.setPublisherName(jsonObject.getString("publisher_name"))
                AdsApplication.setNewPackage(jsonObject.getString("new_package"))
                AdsApplication.setVersionCode(jsonObject.getInt("version_code"))
                AdsApplication.setShowAds(jsonObject.getBoolean("show_ads"))
                AdsApplication.setHomeScreenAds(jsonObject.getBoolean("home_screen_ads"))
                AdsApplication.setUpdatedType(jsonObject.getInt("updated_type"))
                AdsApplication.setUserClickCounter(jsonObject.getInt("user_click_counter"))
                AdsApplication.setAppId(jsonObject.getString("app_id"))
                AdsApplication.setInterstitialAdId(jsonObject.getString("interstitial_ad_id"))

                val nativeJsonObject = jsonObject.getJSONObject("native")
                val bannerJsonObject = jsonObject.getJSONObject("banner")
                val interstitialJsonObject = jsonObject.getJSONObject("interstitial")
                val openAppJsonObject = jsonObject.getJSONObject("openapp")

                val crossNative = RemoteAds.Native(
                    nativeJsonObject.getString("ad_app_icon"),
                    nativeJsonObject.getString("ad_call_to_action_url"),
                    nativeJsonObject.getString("ad_call_to_action_text"),
                    nativeJsonObject.getString("ad_headline"),
                    nativeJsonObject.getString("ad_body"),
                    nativeJsonObject.getString("ad_media"),
                    nativeJsonObject.getString("info_url")
                )

                AdsApplication.setCrossNativeAds(Gson().toJson(crossNative))

                val crossBanner = RemoteAds.Banner(
                    bannerJsonObject.getString("ad_call_to_action_url"),
                    bannerJsonObject.getString("ad_media")
                )

                AdsApplication.setCrossBannerAds(Gson().toJson(crossBanner))

                val crossInterstitial = RemoteAds.Interstitial(
                    interstitialJsonObject.getString("ad_media"),
                    interstitialJsonObject.getString("ad_call_to_action_url"),
                    interstitialJsonObject.getString("ad_headline"),
                    interstitialJsonObject.getString("info_url")
                )

                AdsApplication.setCrossInterstitialAds(Gson().toJson(crossInterstitial))

                val crossOpenApp = RemoteAds.Openapp(
                    openAppJsonObject.getString("ad_media"),
                    openAppJsonObject.getString("ad_app_icon"),
                    openAppJsonObject.getString("ad_call_to_action_url"),
                    openAppJsonObject.getString("ad_headline"),
                    openAppJsonObject.getString("info_url")
                )

                AdsApplication.setCrossOpenAppAds(Gson().toJson(crossOpenApp))

                val adsUnits = jsonObject.getJSONArray("ad_units")

                val adsUnitList = mutableListOf<RemoteAds.AdUnit>()
                for (i in 0 until adsUnits.length()) {
                    val adsUnitJsonObject = adsUnits.getJSONObject(i)
                    adsUnitList.add(
                        RemoteAds.AdUnit(
                            adsUnitJsonObject.getString("adsName"),
                            adsUnitJsonObject.getString("idAds"),
                            adsUnitJsonObject.getString("adsType"),
                            adsUnitJsonObject.getBoolean("enableAds"),
                            adsUnitJsonObject.getInt("frequency"),
                            adsUnitJsonObject.getString("adFailed"),
                            adsUnitJsonObject.getString("publishers"),
                            adsUnitJsonObject.getString("adsLayout")
                        )
                    )
                }

                AdsApplication.setAdsUnits(Gson().toJson(adsUnitList))

                listener.onDataLoaded()
            } else {
                Log.e("TAG", "fetchDataUnSuccessful")
            }
        }.addOnFailureListener {
            Log.e("TAG", "fetchDataFailure: ${it.message}")
        }

        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                if (configUpdate.updatedKeys.contains(Utils.firebaseKey)) {
                    remoteConfig.activate().addOnCompleteListener {
                        if (it.isSuccessful) {
                            val jsonObject = JSONObject(remoteConfig.getString(Utils.firebaseKey))

                            AdsApplication.setPublisherId(jsonObject.getInt("publisher_id"))
                            AdsApplication.setUpdatedStatus(jsonObject.getBoolean("updated_status"))
                            AdsApplication.setAppLiveStatus(jsonObject.getBoolean("app_live_status"))
                            AdsApplication.setPublisherName(jsonObject.getString("publisher_name"))
                            AdsApplication.setNewPackage(jsonObject.getString("new_package"))
                            AdsApplication.setVersionCode(jsonObject.getInt("version_code"))
                            AdsApplication.setShowAds(jsonObject.getBoolean("show_ads"))
                            AdsApplication.setHomeScreenAds(jsonObject.getBoolean("home_screen_ads"))
                            AdsApplication.setUpdatedType(jsonObject.getInt("updated_type"))
                            AdsApplication.setUserClickCounter(jsonObject.getInt("user_click_counter"))
                            AdsApplication.setAppId(jsonObject.getString("app_id"))
                            AdsApplication.setInterstitialAdId(jsonObject.getString("interstitial_ad_id"))

                            val nativeJsonObject = jsonObject.getJSONObject("native")
                            val bannerJsonObject = jsonObject.getJSONObject("banner")
                            val interstitialJsonObject = jsonObject.getJSONObject("interstitial")
                            val openAppJsonObject = jsonObject.getJSONObject("openapp")

                            val crossNative = RemoteAds.Native(
                                nativeJsonObject.getString("ad_app_icon"),
                                nativeJsonObject.getString("ad_call_to_action_url"),
                                nativeJsonObject.getString("ad_call_to_action_text"),
                                nativeJsonObject.getString("ad_headline"),
                                nativeJsonObject.getString("ad_body"),
                                nativeJsonObject.getString("ad_media"),
                                nativeJsonObject.getString("info_url")
                            )

                            AdsApplication.setCrossNativeAds(Gson().toJson(crossNative))

                            val crossBanner = RemoteAds.Banner(
                                bannerJsonObject.getString("ad_call_to_action_url"),
                                bannerJsonObject.getString("ad_media")
                            )

                            AdsApplication.setCrossBannerAds(Gson().toJson(crossBanner))

                            val crossInterstitial = RemoteAds.Interstitial(
                                interstitialJsonObject.getString("ad_media"),
                                interstitialJsonObject.getString("ad_call_to_action_url"),
                                interstitialJsonObject.getString("ad_headline"),
                                interstitialJsonObject.getString("info_url")
                            )

                            AdsApplication.setCrossInterstitialAds(Gson().toJson(crossInterstitial))

                            val crossOpenApp = RemoteAds.Openapp(
                                openAppJsonObject.getString("ad_media"),
                                openAppJsonObject.getString("ad_app_icon"),
                                openAppJsonObject.getString("ad_call_to_action_url"),
                                openAppJsonObject.getString("ad_headline"),
                                openAppJsonObject.getString("info_url")
                            )

                            AdsApplication.setCrossOpenAppAds(Gson().toJson(crossOpenApp))

                            val adsUnits = jsonObject.getJSONArray("ad_units")

                            val adsUnitList = mutableListOf<RemoteAds.AdUnit>()
                            for (i in 0 until adsUnits.length()) {
                                val adsUnitJsonObject = adsUnits.getJSONObject(i)
                                adsUnitList.add(
                                    RemoteAds.AdUnit(
                                        adsUnitJsonObject.getString("adsName"),
                                        adsUnitJsonObject.getString("idAds"),
                                        adsUnitJsonObject.getString("adsType"),
                                        adsUnitJsonObject.getBoolean("enableAds"),
                                        adsUnitJsonObject.getInt("frequency"),
                                        adsUnitJsonObject.getString("adFailed"),
                                        adsUnitJsonObject.getString("publishers"),
                                        adsUnitJsonObject.getString("adsLayout")
                                    )
                                )
                            }

                            AdsApplication.setAdsUnits(Gson().toJson(adsUnitList))
                        }
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.e("TAG", "onError: ${error.message}")
            }
        })
    }

    companion object {
        var adsUnit = mutableListOf<RemoteAds.AdUnit>()

        var crossNativeAds: RemoteAds.Native? = null
        var crossBannerAds: RemoteAds.Banner? = null
        var crossInterstitialAds: RemoteAds.Interstitial? = null
        var crossOpenAppAds: RemoteAds.Openapp? = null
    }
}