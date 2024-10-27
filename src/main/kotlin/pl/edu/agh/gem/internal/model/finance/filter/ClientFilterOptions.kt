package pl.edu.agh.gem.internal.model.finance.filter

import pl.edu.agh.gem.internal.model.finance.ActivityStatus

data class ClientFilterOptions(
    val title: String? = null,
    val status: ActivityStatus? = null,
    val creatorId: String? = null,
    val sortedBy: SortedBy = SortedBy.DATE,
    val sortOrder: SortOrder = SortOrder.ASCENDING,
)
