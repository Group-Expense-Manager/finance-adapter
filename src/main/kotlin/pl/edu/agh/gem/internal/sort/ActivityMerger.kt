package pl.edu.agh.gem.internal.sort

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.FilterOptions
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.DESCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.TITLE

class ActivityMerger(
    private val filterOptions: FilterOptions,
) {

    private val activityComparator = Comparator<Activity> { a, b ->
        val result = when (filterOptions.sortedBy) {
            TITLE -> a.title.lowercase().compareTo(b.title.lowercase())
            DATE -> a.date.compareTo(b.date)
        }

        when (filterOptions.sortOrder) {
            ASCENDING -> result
            DESCENDING -> -result
        }
    }

    fun merge(activitiesA: List<Activity>, activitiesB: List<Activity>): List<Activity> {
        val result = mutableListOf<Activity>()
        var index1 = 0
        var index2 = 0

        while (index1 < activitiesA.size && index2 < activitiesB.size) {
            if (activityComparator.compare(activitiesA[index1], activitiesB[index2]) <= 0) {
                result.add(activitiesA[index1])
                index1++
            } else {
                result.add(activitiesB[index2])
                index2++
            }
        }

        while (index1 < activitiesA.size) {
            result.add(activitiesA[index1])
            index1++
        }
        while (index2 < activitiesB.size) {
            result.add(activitiesB[index2])
            index2++
        }

        return result
    }
}
