package pl.edu.agh.gem.internal.model.finance.filter

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.ASCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortOrder.DESCENDING
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.DATE
import pl.edu.agh.gem.internal.model.finance.filter.SortedBy.TITLE
import pl.edu.agh.gem.util.DummyData.ACTIVITY_TITLE
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.createFilterOptions

class FilterOptionsTest : ShouldSpec({
    should("map to ClientFilterOptions") {
        // given
        val filterOptions = createFilterOptions(title = ACTIVITY_TITLE, status = PENDING)

        // when
        val clientFilterOptions = filterOptions.toClientFilterOptions()

        // then
        clientFilterOptions.also {
            it.title shouldBe filterOptions.title
            it.status shouldBe filterOptions.status
            it.creatorId shouldBe filterOptions.creatorId
            it.currency shouldBe filterOptions.currency
            it.sortedBy shouldBe filterOptions.sortedBy
            it.sortOrder shouldBe filterOptions.sortOrder
        }
    }

    should("create FilterOptions with all fields set") {
        // given & when
        val filterOptions = FilterOptions.create(
            userId = USER_ID,
            title = ACTIVITY_TITLE,
            status = PENDING,
            isCreator = true,
            type = EXPENSE,
            currency = CURRENCY_1,
            sortedBy = TITLE,
            sortOrder = DESCENDING,
        )

        // then
        filterOptions.also {
            it.title shouldBe ACTIVITY_TITLE
            it.status shouldBe PENDING
            it.creatorId shouldBe USER_ID
            it.type shouldBe EXPENSE
            it.currency shouldBe CURRENCY_1
            it.sortedBy shouldBe TITLE
            it.sortOrder shouldBe DESCENDING
        }
    }

    should("create FilterOptions when no fields set") {
        // given & when
        val filterOptions = FilterOptions.create(
            userId = USER_ID,
            title = null,
            status = null,
            isCreator = null,
            type = null,
            currency = null,
            sortedBy = null,
            sortOrder = null,
        )

        // then
        filterOptions.also {
            it.title shouldBe null
            it.status shouldBe null
            it.creatorId shouldBe null
            it.type shouldBe null
            it.currency shouldBe null
            it.sortedBy shouldBe DATE
            it.sortOrder shouldBe ASCENDING
        }
    }
},)
