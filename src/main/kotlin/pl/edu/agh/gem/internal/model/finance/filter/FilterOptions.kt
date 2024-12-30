package pl.edu.agh.gem.internal.model.finance.filter

import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.DESCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE

data class FilterOptions(
    val title: String?,
    val status: ActivityStatus?,
    val creatorId: String?,
    val type: ActivityType?,
    val currency: String?,
    val sortedBy: SortedBy,
    val sortOrder: SortOrder,
) {
    fun toClientFilterOptions() = ClientFilterOptions(
        title = title,
        status = status,
        creatorId = creatorId,
        currency = currency,
        sortedBy = sortedBy,
        sortOrder = sortOrder,
    )

    companion object {
        fun create(
            userId: String,
            title: String?,
            status: ActivityStatus?,
            isCreator: Boolean?,
            type: ActivityType?,
            currency: String?,
            sortedBy: SortedBy?,
            sortOrder: SortOrder?,
        ) = FilterOptions(
            title = title,
            status = status,
            creatorId = if (isCreator == true) userId else null,
            type = type,
            currency = currency,
            sortedBy = sortedBy ?: DATE,
            sortOrder = sortOrder ?: DESCENDING,
        )
    }
}
