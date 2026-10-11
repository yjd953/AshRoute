package io.github.yjd953.ashroute.dto

data class OutboundTrafficStat(
    val tag: String,
    val direction: String,
    val value: Long,
)