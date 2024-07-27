package pl.edu.agh.gem.internal.model.finance.filter

import pl.edu.agh.gem.internal.model.expense.filter.ExpenseFilterOptions
import pl.edu.agh.gem.internal.model.finance.ActivityStatus
import pl.edu.agh.gem.internal.model.finance.ActivityType
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE

data class FilterOptions(
    val title: String?,
    val status: ActivityStatus?,
    val creatorId: String?,
    val type: ActivityType?,
    val sortedBy: SortedBy,
    val sortOrder: SortOrder,
) {
    fun toExpenseFilterOptions() = ExpenseFilterOptions(
        title = title,
        status = status,
        creatorId = creatorId,
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
            sortedBy: SortedBy?,
            sortOrder: SortOrder?,
        ) = FilterOptions(
            title = title,
            status = status,
            creatorId = if (isCreator == true) userId else null,
            type = type,
            sortedBy = sortedBy ?: DATE,
            sortOrder = sortOrder ?: ASCENDING,
        )
    }
}
