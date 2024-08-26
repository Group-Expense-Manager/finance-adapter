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
import pl.edu.agh.gem.util.Quadruple
import pl.edu.agh.gem.util.createActivity
import pl.edu.agh.gem.util.createFilterOptions
import java.time.Instant

class ActivityMergerTest : ShouldSpec({

    val expense1 = createActivity(
        activityId = EXPENSE_ID,
        title = "JKL",
        date = Instant.ofEpochMilli(2L),
    )

    val expense2 = createActivity(
        activityId = OTHER_EXPENSE_ID,
        title = "def",
        date = Instant.ofEpochMilli(1L),
    )

    val payment1 = createActivity(
        activityId = PAYMENT_ID,
        title = "Abc",
        date = Instant.ofEpochMilli(4L),
    )

    val payment2 = createActivity(
        activityId = OTHER_PAYMENT_ID,
        title = "giH",
        date = Instant.ofEpochMilli(3L),
    )

    context("sort correctly") {
        withData(
            Quadruple(
                ActivityMerger(createFilterOptions(sortedBy = DATE, sortOrder = ASCENDING)),
                listOf(expense2, expense1),
                listOf(payment2, payment1),
                listOf(OTHER_EXPENSE_ID, EXPENSE_ID, OTHER_PAYMENT_ID, PAYMENT_ID),
            ),
            Quadruple(
                ActivityMerger(createFilterOptions(sortedBy = DATE, sortOrder = DESCENDING)),
                listOf(expense1, expense2),
                listOf(payment1, payment2),
                listOf(PAYMENT_ID, OTHER_PAYMENT_ID, EXPENSE_ID, OTHER_EXPENSE_ID),
            ),
            Quadruple(
                ActivityMerger(createFilterOptions(sortedBy = TITLE, sortOrder = ASCENDING)),
                listOf(expense2, expense1),
                listOf(payment1, payment2),
                listOf(PAYMENT_ID, OTHER_EXPENSE_ID, OTHER_PAYMENT_ID, EXPENSE_ID),
            ),
            Quadruple(
                ActivityMerger(createFilterOptions(sortedBy = TITLE, sortOrder = DESCENDING)),
                listOf(expense1, expense2),
                listOf(payment2, payment1),
                listOf(EXPENSE_ID, OTHER_PAYMENT_ID, OTHER_EXPENSE_ID, PAYMENT_ID),
            ),
        ) { (activityMerger, listA, listB, expectedIds) ->
            // when
            val actualIds = activityMerger.merge(listA, listB).map { it.activityId }

            // then
            actualIds shouldBe expectedIds
        }
    }
},)
