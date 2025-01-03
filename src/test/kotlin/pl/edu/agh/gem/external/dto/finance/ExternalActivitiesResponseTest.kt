package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.ACCEPTED
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.ActivityStatus.REJECTED
import pl.edu.agh.gem.internal.model.finance.ActivityType.EXPENSE
import pl.edu.agh.gem.util.createActivity
import java.math.BigDecimal
import java.time.Instant

class ExternalActivitiesResponseTest : ShouldSpec({
    should("map Activity to DTO correctly") {
        // given
        val activity = createActivity()

        // when
        val activityDTO = activity.toDTO()

        // then
        activityDTO.also {
            it.activityId shouldBe activity.activityId
            it.type shouldBe activity.type
            it.creatorId shouldBe activity.creatorId
            it.title shouldBe activity.title
            it.value shouldBe activity.value
            it.currency shouldBe activity.currency
            it.status shouldBe activity.status
            it.participantIds shouldBe activity.participantIds
            it.date shouldBe activity.date
        }
    }

    should("map Activities to ActivitiesResponse correctly") {
        // given
        val activityIds = listOf("expenseId1", "expenseId2", "expenseId3")
        val types = listOf(EXPENSE, EXPENSE, EXPENSE)
        val creatorIds = listOf("creatorId1", "creatorId2", "creatorId3")
        val titles = listOf("title1", "title2", "title3")
        val values = listOf(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN)
        val currencies = listOf("PLN", "EUR", "USD")
        val statuses = listOf(PENDING, ACCEPTED, REJECTED)
        val participantsIds = listOf(
            listOf("participant1", "participant2"),
            listOf("participant3", "participant4"),
            listOf("participant5", "participant6"),
        )
        val activityDates = listOf(
            Instant.ofEpochSecond(1000),
            Instant.ofEpochSecond(2000),
            Instant.ofEpochSecond(3000),
        )

        val activities = activityIds.mapIndexed { index, expenseId ->
            createActivity(
                activityId = expenseId,
                type = types[index],
                creatorId = creatorIds[index],
                title = titles[index],
                value = values[index],
                currency = currencies[index],
                status = statuses[index],
                participantIds = participantsIds[index],
                date = activityDates[index],
            )
        }

        // when
        val activitiesResponse = activities.toExternalActivitiesResponse(GROUP_ID)

        // then
        activitiesResponse.groupId shouldBe GROUP_ID
        activitiesResponse.activities.also {
            it shouldHaveSize 3
            it.map { dto -> dto.activityId } shouldContainExactly activityIds
            it.map { dto -> dto.type } shouldContainExactly types
            it.map { dto -> dto.creatorId } shouldContainExactly creatorIds
            it.map { dto -> dto.title } shouldContainExactly titles
            it.map { dto -> dto.value } shouldContainExactly values
            it.map { dto -> dto.currency } shouldContainExactly currencies
            it.map { dto -> dto.status } shouldContainExactly statuses
            it.map { dto -> dto.participantIds } shouldContainExactly participantsIds
            it.map { dto -> dto.date } shouldContainExactly activityDates
        }
    }

    should("return empty activities when there are no activities") {
        // given
        val activities = listOf<Activity>()

        // when
        val activitiesResponse = activities.toExternalActivitiesResponse(GROUP_ID)

        // then
        activitiesResponse.also {
            it.groupId shouldBe GROUP_ID
            it.activities shouldBe listOf()
        }
    }
},)
