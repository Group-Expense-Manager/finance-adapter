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
    val value: BigDecimal,
    val currency: String,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val date: Instant,
)

fun Activity.toDTO(): ActivityDTO {
    return ActivityDTO(
        activityId = activityId,
        type = type,
        creatorId = creatorId,
        title = title,
        value = value,
        currency = currency,
        status = status,
        participantIds = participantIds,
        date = date,
    )
}

fun List<Activity>.toActivitiesResponse(groupId: String): ActivitiesResponse {
    return ActivitiesResponse(
        groupId = groupId,
        activities = this.map { it.toDTO() },
    )
}
