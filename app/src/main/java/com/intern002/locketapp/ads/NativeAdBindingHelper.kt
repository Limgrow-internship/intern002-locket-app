package com.intern002.locketapp.ads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.intern002.locketapp.R

object NativeAdBindingHelper {

    fun bindNativeAdFull(
        adContainer: ViewGroup,
        nativeAd: NativeAd,
        onBindCompleted: ((NativeAdView) -> Unit)? = null
    ) {
        val adView = LayoutInflater.from(adContainer.context)
            .inflate(R.layout.layout_native_ad_full, adContainer, false) as NativeAdView

        adView.mediaView = adView.findViewById(R.id.ad_media)

        val bottomFrame = adView.findViewById<FrameLayout>(R.id.nativeAdView)

        adView.headlineView = bottomFrame?.findViewById(R.id.ad_headline)
        adView.bodyView = bottomFrame?.findViewById(R.id.ad_body)
        adView.callToActionView = bottomFrame?.findViewById(R.id.ad_call_to_action)
        adView.iconView = bottomFrame?.findViewById(R.id.ad_icon)

        (adView.headlineView as? TextView)?.text = nativeAd.headline
        (adView.bodyView as? TextView)?.text = nativeAd.body
        (adView.callToActionView as? TextView)?.text = nativeAd.callToAction // CTA là TextView

        nativeAd.mediaContent?.let { adView.mediaView?.setMediaContent(it) }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.GONE
        } else {
            (adView.iconView as? ImageView)?.setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)

        adContainer.removeAllViews()
        adContainer.addView(adView)

        onBindCompleted?.invoke(adView)
    }

    fun bindNativeAdInline(adView: NativeAdView, nativeAd: NativeAd) {
        adView.setNativeAd(nativeAd)

        val mediaView = adView.findViewById<com.google.android.gms.ads.nativead.MediaView>(R.id.ad_media)
        mediaView?.visibility = View.GONE

        val closeButton = adView.findViewById<ImageView>(R.id.ad_close)
        closeButton?.visibility = View.GONE


        // Icon
        val iconView = adView.findViewById<ImageView>(R.id.ad_icon)
        if (nativeAd.icon != null) {
            iconView?.setImageDrawable(nativeAd.icon?.drawable)
            iconView?.visibility = View.VISIBLE
            adView.iconView = iconView
        } else {
            iconView?.visibility = View.GONE
        }

        // Headline
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        if (nativeAd.headline != null) {
            headlineView.text = nativeAd.headline
            adView.headlineView = headlineView
        }

        // Body
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        if (nativeAd.body != null) {
            bodyView.text = nativeAd.body
            adView.bodyView = bodyView
        }

        val ctaView = adView.findViewById<TextView>(R.id.ad_call_to_action)
        if (nativeAd.callToAction != null) {
            ctaView.text = nativeAd.callToAction
            adView.callToActionView = ctaView
            ctaView.visibility = View.VISIBLE


            val ctaLp = ctaView.layoutParams as RelativeLayout.LayoutParams

            ctaLp.addRule(RelativeLayout.BELOW, R.id.ad_attribution)

            ctaLp.topMargin = 8
            ctaView.layoutParams = ctaLp

        } else {
            ctaView.visibility = View.INVISIBLE
        }

        adView.findViewById<ImageView>(R.id.ad_label_icon)?.visibility = View.VISIBLE
    }
}