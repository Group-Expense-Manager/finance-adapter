package pl.edu.agh.gem.internal.model.finance.filter

import pl.edu.agh.gem.internal.model.finance.ActivityStatus

data class ClientFilterOptions(
    val title: String?,
    val status: ActivityStatus?,
    val creatorId: String?,
    val sortedBy: SortedBy,
    val sortOrder: SortOrder,
)
