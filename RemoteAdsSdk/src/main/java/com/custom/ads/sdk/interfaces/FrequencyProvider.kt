package com.custom.ads.sdk.interfaces

interface FrequencyProvider {
    fun getFrequency(): Int

    fun incrementFrequency()
}