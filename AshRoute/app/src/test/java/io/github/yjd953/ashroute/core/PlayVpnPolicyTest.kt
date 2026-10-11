package io.github.yjd953.ashroute.core

import android.content.Context
import io.github.yjd953.ashroute.AppConfig
import io.github.yjd953.ashroute.dto.CoreConfigContext
import io.github.yjd953.ashroute.dto.entities.ProfileItem
import io.github.yjd953.ashroute.enums.CoreResolvedType
import io.github.yjd953.ashroute.enums.EConfigType
import org.mockito.kotlin.mock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlayVpnPolicyTest {
    @Test
    fun disclosureIsRequiredOnlyForUnacceptedVpnMode() {
        assertTrue(PlayVpnPolicy.requiresDisclosure(isVpnMode = true, accepted = false))
        assertFalse(PlayVpnPolicy.requiresDisclosure(isVpnMode = true, accepted = true))
        assertFalse(PlayVpnPolicy.requiresDisclosure(isVpnMode = false, accepted = false))
    }

    @Test
    fun encryptedProtocolsArePermitted() {
        assertTrue(profile(EConfigType.VMESS, method = "auto").isEncrypted())
        assertTrue(profile(EConfigType.SHADOWSOCKS, method = "aes-256-gcm").isEncrypted())
        assertTrue(profile(EConfigType.VLESS, security = AppConfig.TLS).isEncrypted())
        assertTrue(profile(EConfigType.TROJAN, security = AppConfig.REALITY).isEncrypted())
        assertTrue(profile(EConfigType.HYSTERIA2, security = AppConfig.TLS).isEncrypted())
        assertTrue(profile(EConfigType.WIREGUARD).isEncrypted())
    }

    @Test
    fun potentiallyUnencryptedProtocolsAreRejected() {
        assertFalse(profile(EConfigType.VMESS, method = "none").isEncrypted())
        assertFalse(profile(EConfigType.SHADOWSOCKS, method = "plain").isEncrypted())
        assertFalse(profile(EConfigType.VLESS).isEncrypted())
        assertFalse(profile(EConfigType.SOCKS).isEncrypted())
        assertFalse(profile(EConfigType.HTTP).isEncrypted())
        assertFalse(profile(EConfigType.CUSTOM).isEncrypted())
    }

    @Test
    fun resolvedPlanRequiresEveryEndpointToBeEncrypted() {
        val encrypted = profile(EConfigType.VLESS, security = AppConfig.TLS)
        val plain = profile(EConfigType.HTTP)

        assertTrue(PlayVpnPolicy.permits(contextWith(encrypted)))
        assertFalse(PlayVpnPolicy.permits(contextWith(encrypted, plain)))
        assertFalse(PlayVpnPolicy.permits(contextWith()))
    }

    @Test
    fun customConfigurationCannotBeVerified() {
        assertFalse(
            PlayVpnPolicy.permits(
                CoreConfigContext(
                    context = mock(),
                    guid = "custom",
                    isCustom = true,
                )
            )
        )
    }

    private fun contextWith(vararg profiles: ProfileItem): CoreConfigContext =
        CoreConfigContext(
            context = mock<Context>(),
            guid = "test",
            resolvedOutbounds = if (profiles.isEmpty()) {
                emptyList()
            } else {
                listOf(
                    CoreConfigContext.ResolvedOutbound(
                        tag = AppConfig.TAG_PROXY,
                        profile = profiles.first(),
                        resolvedProfiles = profiles.toList(),
                        resolvedType = CoreResolvedType.NORMAL,
                    )
                )
            },
        )

    private fun profile(
        type: EConfigType,
        method: String? = null,
        security: String? = null,
    ) = ProfileItem.create(type).apply {
        this.method = method
        this.security = security
    }

    private fun ProfileItem.isEncrypted(): Boolean =
        PlayVpnPolicy.isEncrypted(this)
}
