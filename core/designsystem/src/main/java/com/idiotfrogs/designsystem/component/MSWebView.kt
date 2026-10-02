package com.idiotfrogs.designsystem.component

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MSWebView(
    url: String,
    goToBack: () -> Unit,
) {
    val context = LocalContext.current
    val webView = remember { WebView(context) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            webView.apply {
                webViewClient = object : WebViewClient() {}
                webChromeClient = object : WebChromeClient() {}
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = { webView.loadUrl(url) },
        onRelease = { webView.destroy() }
    )

    BackHandler {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            goToBack.invoke()
        }
    }
}