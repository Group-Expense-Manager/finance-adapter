package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import java.math.BigDecimal
import java.time.Instant

data class ActivitiesResponse(
    val groupId: String,
    val activities: List<ActivityDTO>,
)

data class ActivityDTO(
    val activityId: String,
    val type: ActivityType,
    val creatorId: String,
    val title: String,
    val sum: BigDecimal,
    val baseCurrency: String,
    val targetCurrency: String?,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val activityDate: Instant,
)

fun Activity.toDTO(): ActivityDTO {
    return ActivityDTO(
        activityId = activityId,
        type = type,
        creatorId = creatorId,
        title = title,
        sum = sum,
        baseCurrency = baseCurrency,
        targetCurrency = targetCurrency,
        status = status,
        participantIds = participantIds,
        activityDate = activityDate,
    )
}

fun List<Activity>.toActivitiesResponse(groupId: String): ActivitiesResponse {
    return ActivitiesResponse(
        groupId = groupId,
        activities = this.map { it.toDTO() },
    )
}
