package io.github.yjd953.ashroute.fmt

import io.github.yjd953.ashroute.AppConfig
import io.github.yjd953.ashroute.dto.V2rayNShareItem
import io.github.yjd953.ashroute.dto.entities.ProfileItem
import io.github.yjd953.ashroute.extension.isNotNullEmpty
import io.github.yjd953.ashroute.util.JsonUtil
import io.github.yjd953.ashroute.util.LogUtil
import io.github.yjd953.ashroute.util.Utils

object V2rayNFmt : FmtBase() {
    fun parse(list: List<String>, subId: String): List<ProfileItem> {
        val idMap = mutableMapOf<String, V2rayNShareItem>()
        list.mapNotNull(::parseShareItem).forEach { item ->
            idMap.putIfAbsent(item.IndexId.orEmpty(), item)
        }

        return idMap.values.map { item ->
            item.toProfileItem().apply {
                val proto = item.ProtoExtraObj
                policyGroupSubscriptionId = if (proto?.SubChildItems == "self") {
                    subId
                } else {
                    null
                }
                proto?.ChildItems?.takeIf { it.isNotNullEmpty() }?.let { ids ->
                    val remarks = ids.split(",")
                        .mapNotNull { idMap[it]?.Remarks }
                        .filter { it.isNotNullEmpty() }

                    if (remarks.isNotEmpty()) {
                        when (item.ConfigType) {
                            101 -> policyGroupFilter = remarks.joinToString("|", "^(", ")$") { Regex.escape(it) }
                            102 -> proxyChainProfiles = remarks.joinToString(",")
                        }
                    }
                }
            }
        }
    }

    private fun parseShareItem(str: String): V2rayNShareItem? = try {
        JsonUtil.fromJson(Utils.decode(str.substringAfterLast('/')), V2rayNShareItem::class.java)
    } catch (e: Exception) {
        LogUtil.e(AppConfig.TAG, "Failed to parse V2rayN share item", e)
        null
    }
}