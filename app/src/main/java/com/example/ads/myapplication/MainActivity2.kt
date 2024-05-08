package com.example.ads.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.custom.ads.sdk.AdsApplication
import com.custom.ads.sdk.adsUtils.AdsUtils
import com.custom.ads.sdk.interfaces.InterstitialAdShowedListener

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnShowAd).setOnClickListener {
            AdsApplication.adsClickCounter++
            AdsUtils.showInterstitialAd(
                this,
                "demo_inter",
                object : InterstitialAdShowedListener {
                    override fun onCompleted() {
                        startActivity(Intent(this@MainActivity2, MainActivity3::class.java))
                    }
                }
            )
        }
    }
}