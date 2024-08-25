package pl.edu.agh.gem.internal.model.payment

import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy

data class PaymentFilterOptions(
    val title: String?,
    val status: ActivityStatus?,
    val creatorId: String?,
    val sortedBy: SortedBy,
    val sortOrder: SortOrder,
)
