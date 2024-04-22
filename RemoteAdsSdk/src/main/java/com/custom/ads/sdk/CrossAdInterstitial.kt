package com.custom.ads.sdk

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.custom.ads.sdk.databinding.CrossPromotionInterstitialAdBinding
import com.custom.ads.sdk.interfaces.CrossInterstitialAdShowedListener


class CrossAdInterstitial : DialogFragment() {
    private lateinit var binding: CrossPromotionInterstitialAdBinding
    private var crossInterstitialAdShowedListener: CrossInterstitialAdShowedListener? = null

    fun setCrossPromotionListener(crossInterstitialAdShowedListener: CrossInterstitialAdShowedListener) {
        this.crossInterstitialAdShowedListener = crossInterstitialAdShowedListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.Theme_Black_NoTitleBar)

        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CrossPromotionInterstitialAdBinding.inflate(inflater, null, false)

        AdsApplication.appOpenManager?.isAdShow = true

        binding.imgClose.setOnClickListener {
            dismiss()
            AdsApplication.adsClickCounter = 0
            AdsApplication.appOpenManager?.isAdShow = false
            crossInterstitialAdShowedListener?.onCompleted()
        }

        Glide.with(requireActivity()).load(BaseSplashAdsActivity.crossInterstitialAds?.adMedia)
            .into(binding.imgAdContent)

        binding.imgAdContent.startAnimation(
            AnimationUtils.loadAnimation(
                requireActivity(), com.custom.ads.sdk.R.anim.slide_up
            )
        )

        binding.adContainer.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(BaseSplashAdsActivity.crossInterstitialAds?.adCallToActionUrl)
                )
            )
        }

        return binding.root
    }
}