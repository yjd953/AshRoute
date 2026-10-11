package io.github.yjd953.ashroute

import android.app.Application
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.Configuration
import androidx.work.WorkManager
import io.github.yjd953.ashroute.AppConfig.APP_PACKAGE
import io.github.yjd953.ashroute.handler.AppLocaleManager
import io.github.yjd953.ashroute.handler.MmkvManager
import io.github.yjd953.ashroute.handler.SettingsManager
import io.github.yjd953.ashroute.ui.compose.ThemeManager

class AshRouteApplication : Application() {
    companion object {
        lateinit var application: AshRouteApplication
    }

    /**
     * Attaches the base context to the application.
     * @param base The base context.
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let(ContextCompat::getContextForLanguage))
        application = this
    }

    private val workManagerConfiguration: Configuration = Configuration.Builder()
        .setDefaultProcessName("${APP_PACKAGE}:bg")
        .build()

    /**
     * Initializes the application.
     */
    override fun onCreate() {
        super.onCreate()

        MmkvManager.initialize(this)

        AppLocaleManager.initialize(this)

        // Initialize WorkManager with the custom configuration
        WorkManager.initialize(this, workManagerConfiguration)

        // Ensure critical preference defaults are present in MMKV early
        SettingsManager.initApp(this)

        // Initialize theme state from MMKV
        ThemeManager.refresh()
    }
}
