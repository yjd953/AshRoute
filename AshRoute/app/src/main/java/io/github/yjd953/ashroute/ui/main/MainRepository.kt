package io.github.yjd953.ashroute.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import io.github.yjd953.ashroute.AshRouteApplication
import io.github.yjd953.ashroute.AppConfig
import io.github.yjd953.ashroute.R
import io.github.yjd953.ashroute.dto.ConnectionTestResult
import io.github.yjd953.ashroute.dto.RealPingResult
import io.github.yjd953.ashroute.dto.SubscriptionUpdateResult
import io.github.yjd953.ashroute.dto.TestServiceMessage
import io.github.yjd953.ashroute.dto.entities.ProfileItem
import io.github.yjd953.ashroute.dto.entities.ServerAffiliationInfo
import io.github.yjd953.ashroute.dto.entities.SubscriptionCache
import io.github.yjd953.ashroute.dto.entities.SubscriptionItem
import io.github.yjd953.ashroute.extension.serializable
import io.github.yjd953.ashroute.handler.ProfileConfigManager
import io.github.yjd953.ashroute.handler.AppLocaleManager
import io.github.yjd953.ashroute.handler.MmkvManager
import io.github.yjd953.ashroute.handler.SettingsManager
import io.github.yjd953.ashroute.handler.SubscriptionUpdater
import io.github.yjd953.ashroute.helper.MessageHelper
import io.github.yjd953.ashroute.util.LogUtil
import io.github.yjd953.ashroute.util.Utils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.atomic.AtomicBoolean

class MainRepository(
    private val app: AshRouteApplication
) : MainDataSource {

    private val localizedContext: Context
        get() = AppLocaleManager.localizedContext(app)

    private val closed = AtomicBoolean(false)

    // Probe results are finite and must remain lossless until the ViewModel coalesces them.
    private val mainServiceEventChannel = Channel<MainServiceEvent>(Channel.UNLIMITED)

    override val mainServiceEvent: Flow<MainServiceEvent> = mainServiceEventChannel.receiveAsFlow()

    private val serviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val safeIntent = intent ?: return
            val requestId = safeIntent.getStringExtra(MessageHelper.EXTRA_REQUEST_ID).orEmpty()
            val event = when (safeIntent.getIntExtra("key", 0)) {
                AppConfig.MSG_STATE_RUNNING -> MainServiceEvent.StateRunning
                AppConfig.MSG_STATE_NOT_RUNNING -> MainServiceEvent.StateNotRunning
                AppConfig.MSG_STATE_START_SUCCESS -> MainServiceEvent.StateStartSuccess
                AppConfig.MSG_STATE_START_FAILURE -> MainServiceEvent.StateStartFailure(
                    safeIntent.getStringExtra("content")
                )

                AppConfig.MSG_STATE_STOP_SUCCESS -> MainServiceEvent.StateStopSuccess
                AppConfig.MSG_MEASURE_DELAY_RESULT -> safeIntent
                    .serializable<ConnectionTestResult>("content")
                    ?.let { MainServiceEvent.MeasureDelayResult(it, requestId) }
                AppConfig.MSG_MEASURE_DELAY_CANCEL -> MainServiceEvent.MeasureDelayCancelled(requestId)

                AppConfig.MSG_MEASURE_CONFIG_SUCCESS -> safeIntent
                    .serializable<RealPingResult>("content")
                    ?.let { MainServiceEvent.MeasureConfigSuccess(it, requestId) }
                AppConfig.MSG_MEASURE_CONFIG_NOTIFY -> MainServiceEvent.MeasureConfigNotify(
                    safeIntent.getStringExtra("content").orEmpty(), requestId
                )

                AppConfig.MSG_MEASURE_CONFIG_FINISH -> MainServiceEvent.MeasureConfigFinish(
                    requestId
                )
                AppConfig.MSG_MEASURE_CONFIG_CANCEL -> MainServiceEvent.MeasureConfigCancelled(requestId)

                else -> null
            }
            event?.let { mainServiceEventChannel.trySend(it) }
        }
    }

    init {
        ContextCompat.registerReceiver(
            app,
            serviceReceiver,
            IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY),
            Utils.receiverFlags()
        )
        MessageHelper.sendMsg2Service(app, AppConfig.MSG_REGISTER_CLIENT, "")
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        runCatching {
            MessageHelper.sendMsg2Service(app, AppConfig.MSG_UNREGISTER_CLIENT, "")
        }.onFailure {
            LogUtil.e(AppConfig.TAG, "Failed to unregister service client", it)
        }
        runCatching {
            app.unregisterReceiver(serviceReceiver)
        }.onFailure {
            LogUtil.e(AppConfig.TAG, "Failed to unregister main service receiver", it)
        }
        mainServiceEventChannel.close()
    }

    override fun getSelectedSubscriptionId(): String =
        MmkvManager.decodeSettingsString(AppConfig.CACHE_SUBSCRIPTION_ID, "").orEmpty()

    override fun setSelectedSubscriptionId(id: String) {
        MmkvManager.encodeSettings(AppConfig.CACHE_SUBSCRIPTION_ID, id)
    }

    override fun getSelectServer(): String? = MmkvManager.getSelectServer()

    override fun setSelectServer(guid: String) = MmkvManager.setSelectServer(guid)

    override fun getConfirmRemove(): Boolean =
        MmkvManager.decodeSettingsBool(AppConfig.PREF_CONFIRM_REMOVE, false)

    override fun getDoubleColumnDisplay(): Boolean =
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DOUBLE_COLUMN_DISPLAY, false)

    override fun isGroupAllDisplayEnabled(): Boolean =
        MmkvManager.decodeSettingsBool(AppConfig.PREF_GROUP_ALL_DISPLAY)

    override fun getString(resId: Int): String = localizedContext.getString(resId)

    override fun getString(resId: Int, vararg formatArgs: Any): String =
        localizedContext.getString(resId, *formatArgs)

    override fun getSubscriptions(): List<SubscriptionCache> {
        val result = mutableListOf<SubscriptionCache>()
        if (isGroupAllDisplayEnabled()) {
            result += SubscriptionCache(
                guid = "",
                subscription = SubscriptionItem().apply {
                    remarks = localizedContext.getString(R.string.filter_config_all)
                }
            )
        }
        result += MmkvManager.decodeSubscriptions()
        return result
    }

    override fun getSubscriptionItem(id: String): SubscriptionItem? =
        MmkvManager.decodeSubscription(id)

    override fun getServerGuidList(groupId: String): List<String> =
        if (groupId.isEmpty()) {
            MmkvManager.decodeAllServerList()
        } else {
            MmkvManager.decodeServerList(groupId)
        }

    override fun decodeServerConfig(guid: String): ProfileItem? =
        MmkvManager.decodeServerConfig(guid)

    override fun decodeAffiliationInfo(guid: String): ServerAffiliationInfo? =
        MmkvManager.decodeServerAffiliationInfo(guid)

    override fun encodeServerList(guids: List<String>, groupId: String) =
        MmkvManager.encodeServerList(ArrayList(guids), groupId)

    override fun removeServer(guid: String) = MmkvManager.removeServer(guid)

    override fun removeAllServer(): Int = MmkvManager.removeAllServer()

    override fun removeInvalidServerByGuid(guid: String): Int =
        MmkvManager.removeInvalidServer(guid)

    override fun removeInvalidServersInGroup(groupId: String): Int =
        if (groupId.isEmpty()) {
            MmkvManager.removeInvalidServer("")
        } else {
            getServerGuidList(groupId).sumOf(::removeInvalidServerByGuid)
        }

    override fun clearAllTestDelayResults(guids: List<String>) =
        MmkvManager.clearAllTestDelayResults(guids)

    override fun sortByTestResultsForSub(subId: String) {
        ProfileConfigManager.sortByTestResultsForSub(subId)
    }

    override fun getSubsList(): List<String> = MmkvManager.decodeSubsList()

    override suspend fun importBatchConfig(
        server: String?,
        subscriptionId: String,
        updateUI: Boolean
    ): Pair<Int, Int> = ProfileConfigManager.importBatchConfig(server, subscriptionId, updateUI)

    override fun updateConfigViaSubAll(): SubscriptionUpdateResult =
        ProfileConfigManager.updateConfigViaSubAll()

    override fun updateConfigViaSub(subscriptionCache: SubscriptionCache): SubscriptionUpdateResult =
        ProfileConfigManager.updateConfigViaSub(subscriptionCache)

    override fun shareNonCustomConfigsToClipboard(guids: List<String>): Int =
        ProfileConfigManager.shareNonCustomConfigsToClipboard(app, guids)

    override fun share2QRCode(guid: String): android.graphics.Bitmap? =
        ProfileConfigManager.share2QRCode(guid)

    override fun share2Clipboard(guid: String): Boolean =
        ProfileConfigManager.share2Clipboard(app, guid) == 0

    override fun sendMsg2Service(msgId: Int, content: String) =
        MessageHelper.sendMsg2Service(app, msgId, content)

    override fun sendMsg2TestService(msg: TestServiceMessage, requestId: String?) =
        MessageHelper.sendMsg2TestService(app, msg, requestId)

    override fun cancelAllPing() {
        sendMsg2TestService(
            TestServiceMessage(key = AppConfig.MSG_MEASURE_CONFIG_CANCEL)
        )
    }

    override fun testCurrentServerRealPing(requestId: String) {
        MessageHelper.sendMsg2ServiceForResult(app, AppConfig.MSG_MEASURE_DELAY, requestId) { handled ->
            if (!handled) mainServiceEventChannel.trySend(MainServiceEvent.MeasureDelayCancelled(requestId))
        }
    }

    override fun syncSubscriptions() {
        SubscriptionUpdater.sync(app)
    }

    override fun initAssets() {
        SettingsManager.initAssets(app, app.assets)
    }
}
