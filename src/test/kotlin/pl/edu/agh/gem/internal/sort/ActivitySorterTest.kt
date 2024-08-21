package pl.edu.agh.gem.internal.sort

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.DESCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.TITLE
import pl.edu.agh.gem.util.DummyData.EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_EXPENSE_ID
import pl.edu.agh.gem.util.DummyData.OTHER_PAYMENT_ID
import pl.edu.agh.gem.util.DummyData.PAYMENT_ID
import pl.edu.agh.gem.util.createActivity
import pl.edu.agh.gem.util.createFilterOptions
import java.time.Instant

class ActivitySorterTest : ShouldSpec({

    context("sort correctly") {
        withData(
            Pair(
                createFilterOptions(sortedBy = DATE, sortOrder = ASCENDING),
                listOf(OTHER_EXPENSE_ID, EXPENSE_ID, OTHER_PAYMENT_ID, PAYMENT_ID),
            ),
            Pair(
                createFilterOptions(sortedBy = DATE, sortOrder = DESCENDING),
                listOf(PAYMENT_ID, OTHER_PAYMENT_ID, EXPENSE_ID, OTHER_EXPENSE_ID),
            ),
            Pair(
                createFilterOptions(sortedBy = TITLE, sortOrder = ASCENDING),
                listOf(PAYMENT_ID, OTHER_EXPENSE_ID, OTHER_PAYMENT_ID, EXPENSE_ID),
            ),
            Pair(
                createFilterOptions(sortedBy = TITLE, sortOrder = DESCENDING),
                listOf(EXPENSE_ID, OTHER_PAYMENT_ID, OTHER_EXPENSE_ID, PAYMENT_ID),
            ),
        ) { (filterOptions, expectedIds) ->
            // given
            val activities = listOf(
                createActivity(
                    activityId = EXPENSE_ID,
                    title = "JKL",
                    date = Instant.ofEpochMilli(2L),
                ),
                createActivity(
                    activityId = OTHER_EXPENSE_ID,
                    title = "def",
                    date = Instant.ofEpochMilli(1L),
                ),
                createActivity(
                    activityId = PAYMENT_ID,
                    title = "Abc",
                    date = Instant.ofEpochMilli(4L),
                ),
                createActivity(
                    activityId = OTHER_PAYMENT_ID,
                    title = "giH",
                    date = Instant.ofEpochMilli(3L),
                ),
            )
            // when
            val actualIds = activities.sort(filterOptions).map { it.activityId }

            // then
            actualIds shouldBe expectedIds
        }
    }
},)
