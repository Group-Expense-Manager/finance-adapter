package pl.edu.agh.gem.internal.sort

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.DESCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.TITLE

fun List<Activity>.sort(filterOptions: FilterOptions): List<Activity> {
    return when (filterOptions.sortedBy) {
        TITLE -> {
            when (filterOptions.sortOrder) {
                ASCENDING -> this.sortedBy { it.title.lowercase() }
                DESCENDING -> this.sortedByDescending { it.title.lowercase() }
            }
        }
        DATE -> {
            when (filterOptions.sortOrder) {
                ASCENDING -> this.sortedBy { it.date }
                DESCENDING -> this.sortedByDescending { it.date }
            }
        }
    }
}
