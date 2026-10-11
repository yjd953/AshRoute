package io.github.yjd953.ashroute.enums

enum class BrowserDialerMode(val value: String) {
    OKHTTP("OkHttp"),
    WEBVIEW("WebView");

    companion object {
        fun from(value: String?): BrowserDialerMode? = entries.find { it.value == value }
    }
}
