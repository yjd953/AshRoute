package io.github.yjd953.ashroute.ui.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import io.github.yjd953.ashroute.R
import io.github.yjd953.ashroute.enums.EConfigType
import io.github.yjd953.ashroute.ui.compose.FormTextField

class ServerSocksActivity : BaseServerActivity() {

    override val serverConfigType: EConfigType = EConfigType.SOCKS

    @Composable
    override fun ScreenContent() {
        val scope = rememberCoroutineScope()
        val uiState = rememberSaveable(saver = ServerUiState.Saver) {
            ServerUiState.from(
                initialConfig = initialConfig
            )
        }.apply {
            configType = serverConfigType
        }

        ServerEditorScaffold(
            title = serverConfigType.toString(),
            onSaveClick = { saveServer(uiState) }
        ) {
            CommonBasicFields(uiState)
            SocksProtocolFields(uiState)

        }
    }

    @Composable
    private fun SocksProtocolFields(state: ServerUiState) {
        FormTextField(
            stringResource(R.string.server_lab_security4),
            state.username,
            { state.username = it }
        )
        FormTextField(
            stringResource(R.string.server_lab_id4),
            state.password,
            { state.password = it }
        )
    }
}
