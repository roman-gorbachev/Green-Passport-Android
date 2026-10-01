package com.smartcity.greenpassport.feature.games.presentation.web.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GameWebView(
    url: String,
    onFinish: (Int) -> Unit,
    onClose: () -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    onFailure: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnFinish by rememberUpdatedState(onFinish)
    val currentOnClose by rememberUpdatedState(onClose)
    val currentOnLoadingChange by rememberUpdatedState(onLoadingChange)
    val currentOnFailure by rememberUpdatedState(onFailure)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                addJavascriptInterface(
                    GameBridge(onFinish = { currentOnFinish(it) }, onClose = { currentOnClose() }),
                    GameBridge.NAME,
                )
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        currentOnLoadingChange(false)
                    }

                    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                        if (request.isForMainFrame) currentOnFailure()
                    }
                }
                currentOnLoadingChange(true)
                loadUrl(url)
            }
        },
        onRelease = { webView ->
            webView.removeJavascriptInterface(GameBridge.NAME)
            webView.destroy()
        },
        modifier = modifier,
    )
}
