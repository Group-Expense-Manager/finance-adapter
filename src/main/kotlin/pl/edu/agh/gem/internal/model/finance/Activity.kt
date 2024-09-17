package pl.edu.agh.gem.internal.model.finance

import java.math.BigDecimal
import java.time.Instant

data class Activity(
    val activityId: String,
    val type: ActivityType,
    val creatorId: String,
    val title: String,
    val value: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val date: Instant,
)
