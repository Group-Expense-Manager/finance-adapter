package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.SettlementsRepository
import pl.edu.agh.gem.util.createSettlements

class SettlementsRepositoryIT(
    private val settlementsRepository: SettlementsRepository,
) : BaseIntegrationSpec({

    should("save and retrieve settlements by groupId") {
        // given
        val settlements = createSettlements(groupId = GROUP_ID)

        // when
        val savedSettlements = settlementsRepository.save(settlements)

        // then
        savedSettlements.groupId shouldBe settlements.groupId
        savedSettlements.currency shouldBe settlements.currency
        savedSettlements.settlements.size shouldBe 3

        // when
        val retrievedSettlements = settlementsRepository.getSettlements(GROUP_ID)

        // then
        retrievedSettlements.shouldNotBeEmpty()
        retrievedSettlements.first().also {
            it.groupId shouldBe GROUP_ID
            it.currency shouldBe settlements.currency
            it.settlements.size shouldBe 3
            it.settlements.map { it.value } shouldContainExactlyInAnyOrder listOf("100", "200", "-300").map { it.toBigDecimal() }
        }
    }

    should("return empty list when no settlements found for groupId") {
        // given
        val nonExistingGroupId = "non-existing-group"

        // when
        val retrievedSettlements = settlementsRepository.getSettlements(nonExistingGroupId)

        // then
        retrievedSettlements.shouldBeEmpty()
    }
},)
