package pl.edu.agh.gem.external.dto.finance

import pl.edu.agh.gem.internal.model.finance.Activities
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import java.math.BigDecimal
import java.time.Instant

data class InternalActivitiesResponse(
    val groupId: String,
    val groupActivities: List<InternalActivityGroupDTO>,
)

data class InternalActivityGroupDTO(
    val currency: String,
    val activities: List<InternalActivityDTO>,
)

fun Activities.toInternalActivityGroupDTO() = InternalActivityGroupDTO(
    currency = currency,
    activities = activities.map { it.toInternalDTO() },
)

data class InternalActivityDTO(
    val id: String,
    val type: ActivityType,
    val creatorId: String,
    val title: String,
    val value: BigDecimal,
    val status: ActivityStatus,
    val participantIds: List<String>,
    val date: Instant,
)

fun Activity.toInternalDTO(): InternalActivityDTO {
    return InternalActivityDTO(
        id = activityId,
        type = type,
        creatorId = creatorId,
        title = title,
        value = value,
        status = status,
        participantIds = participantIds,
        date = date,
    )
}

fun List<Activities>.toInternalActivitiesResponse(groupId: String): InternalActivitiesResponse {
    return InternalActivitiesResponse(
        groupId = groupId,
        groupActivities = this.map { it.toInternalActivityGroupDTO() },
    )
}
