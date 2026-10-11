package io.github.yjd953.ashroute.dto

import java.io.Serializable

/** One persisted delay-test result delivered from the task process to the UI. */
data class RealPingResult(
    val guid: String,
    val delayMillis: Long,
) : Serializable
