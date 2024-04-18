package com.custom.ads.sdk.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RemoteAds {
    @SerializedName("publisher_id")
    @Expose
    var publisherId: Int? = null

    @SerializedName("updated_status")
    @Expose
    var updatedStatus: Boolean? = null

    @SerializedName("app_live_status")
    @Expose
    var appLiveStatus: Boolean? = null

    @SerializedName("total_ad_show")
    @Expose
    var totalAdShow: Any? = null

    @SerializedName("publisher_name")
    @Expose
    var publisherName: String? = null

    @SerializedName("new_package")
    @Expose
    var newPackage: String? = null

    @SerializedName("version_code")
    @Expose
    var versionCode: Int? = null

    @SerializedName("show_ads")
    @Expose
    var showAds: Boolean? = null

    @SerializedName("home_screen_ads")
    @Expose
    var homeScreenAds: Boolean? = null

    @SerializedName("updated_type")
    @Expose
    var updatedType: Int? = null

    @SerializedName("back_press_ads")
    @Expose
    var backPressAds: Boolean? = null

    @SerializedName("user_click_counter")
    @Expose
    var userClickCounter: Int? = null

    @SerializedName("app_id")
    @Expose
    var appId: String? = null

    @SerializedName("interstitial_ad_id")
    @Expose
    var interstitialAdId: String? = null

    @SerializedName("native")
    @Expose
    var nativeData: Native? = null

    @SerializedName("banner")
    @Expose
    var banner: Banner? = null

    @SerializedName("interstitial")
    @Expose
    var interstitial: Interstitial? = null

    @SerializedName("openapp")
    @Expose
    var openapp: Openapp? = null

    @SerializedName("ad_units")
    @Expose
    var adUnits: List<AdUnit>? = null

    class AdUnit(
        @SerializedName("adsName")
        @Expose
        var adsName: String? = null,

        @SerializedName("idAds")
        @Expose
        var idAds: String? = null,

        @SerializedName("adsType")
        @Expose
        var adsType: String? = null,

        @SerializedName("enableAds")
        @Expose
        var enableAds: Boolean? = null,

        @SerializedName("frequency")
        @Expose
        var frequency: Int? = null,

        @SerializedName("adFailed")
        @Expose
        var adFailed: String? = null,

        @SerializedName("publishers")
        @Expose
        var publishers: String? = null,

        @SerializedName("adsLayout")
        @Expose
        var adsLayout: String? = null
    )

    class Banner(
        @SerializedName("ad_call_to_action_url")
        @Expose
        var adCallToActionUrl: String? = null,
        @SerializedName("ad_media")
        @Expose
        var adMedia: String? = null
    )

    class Interstitial(
        @SerializedName("ad_media")
        @Expose
        var adMedia: String? = null,

        @SerializedName("ad_call_to_action_url")
        @Expose
        var adCallToActionUrl: String? = null,

        @SerializedName("ad_headline")
        @Expose
        var adHeadline: String? = null,

        @SerializedName("info_url")
        @Expose
        var infoUrl: String? = null
    )

    class Native(
        @SerializedName("ad_app_icon")
        @Expose
        var adAppIcon: String? = null,

        @SerializedName("ad_call_to_action_url")
        @Expose
        var adCallToActionUrl: String? = null,

        @SerializedName("ad_call_to_action_text")
        @Expose
        var adCallToActionText: String? = null,

        @SerializedName("ad_headline")
        @Expose
        var adHeadline: String? = null,

        @SerializedName("ad_body")
        @Expose
        var adBody: String? = null,

        @SerializedName("ad_media")
        @Expose
        var adMedia: String? = null,

        @SerializedName("info_url")
        @Expose
        var infoUrl: String? = null
    )

    class Openapp(
        @SerializedName("ad_media")
        @Expose
        var adMedia: String? = null,

        @SerializedName("ad_app_icon")
        @Expose
        var adAppIcon: String? = null,

        @SerializedName("ad_call_to_action_url")
        @Expose
        var adCallToActionUrl: String? = null,

        @SerializedName("ad_headline")
        @Expose
        var adHeadline: String? = null,

        @SerializedName("info_url")
        @Expose
        var infoUrl: String? = null
    )
}
