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
        var indexA = 0
        var indexB = 0

        while (indexA < activitiesA.size && indexB < activitiesB.size) {
            if (activityComparator.compare(activitiesA[indexA], activitiesB[indexB]) <= 0) {
                result.add(activitiesA[indexA])
                indexA++
            } else {
                result.add(activitiesB[indexB])
                indexB++
            }
        }

        result.addAll(activitiesA.subList(indexA, activitiesA.size))
        result.addAll(activitiesB.subList(indexB, activitiesB.size))

        return result
    }
}
