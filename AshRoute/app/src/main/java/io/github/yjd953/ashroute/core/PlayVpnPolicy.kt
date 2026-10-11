package io.github.yjd953.ashroute.core

import io.github.yjd953.ashroute.AppConfig
import io.github.yjd953.ashroute.dto.CoreConfigContext
import io.github.yjd953.ashroute.dto.entities.ProfileItem
import io.github.yjd953.ashroute.enums.EConfigType

/**
 * Enforces Google Play's requirement that VpnService traffic is encrypted to
 * the configured tunnel endpoint.
 */
object PlayVpnPolicy {
    fun requiresDisclosure(isVpnMode: Boolean, accepted: Boolean): Boolean =
        isVpnMode && !accepted

    fun permits(context: CoreConfigContext): Boolean {
        if (context.isCustom) return false

        val profiles = context.resolvedOutbounds.flatMap { it.resolvedProfiles }
        return profiles.isNotEmpty() && profiles.all(::isEncrypted)
    }

    internal fun isEncrypted(profile: ProfileItem): Boolean {
        val transportSecurity = profile.security.orEmpty().lowercase()
        val method = profile.method.orEmpty().lowercase()

        return when (profile.configType) {
            EConfigType.VMESS -> method !in UNENCRYPTED_METHODS
            EConfigType.SHADOWSOCKS -> method !in UNENCRYPTED_METHODS
            EConfigType.VLESS,
            EConfigType.TROJAN,
            EConfigType.HYSTERIA,
            EConfigType.HYSTERIA2 -> {
                transportSecurity == AppConfig.TLS ||
                        transportSecurity == AppConfig.REALITY
            }

            EConfigType.WIREGUARD -> true
            EConfigType.SOCKS,
            EConfigType.HTTP,
            EConfigType.CUSTOM,
            EConfigType.POLICYGROUP,
            EConfigType.PROXYCHAIN -> false
        }
    }

    private val UNENCRYPTED_METHODS = setOf("", "none", "plain", "zero")
}
