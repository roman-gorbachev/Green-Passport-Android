package com.smartcity.greenpassport.feature.games.presentation.web.ui

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class GameBridge(
    private val onFinish: (Int) -> Unit,
    private val onClose: () -> Unit,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun finish(score: Double) {
        mainHandler.post { onFinish(score.toInt()) }
    }

    @JavascriptInterface
    fun close() {
        mainHandler.post { onClose() }
    }

    companion object {
        const val NAME = "GreenPassportAndroid"
    }
}
