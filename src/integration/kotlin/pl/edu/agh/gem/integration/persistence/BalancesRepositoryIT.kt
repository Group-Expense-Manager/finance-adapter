package pl.edu.agh.gem.integration.persistence

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.integration.BaseIntegrationSpec
import pl.edu.agh.gem.internal.persistence.BalancesRepository
import pl.edu.agh.gem.util.createBalances

class BalancesRepositoryIT(
    private val balancesRepository: BalancesRepository,
) : BaseIntegrationSpec({

    should("save and retrieve balances by groupId") {
        // given
        val balances = createBalances(groupId = "group1")

        // when
        val savedBalances = balancesRepository.save(balances)

        // then
        savedBalances.groupId shouldBe balances.groupId
        savedBalances.currency shouldBe balances.currency
        savedBalances.users.size shouldBe 3

        // when
        val retrievedBalances = balancesRepository.getBalances("group1")

        // then
        retrievedBalances.shouldNotBeEmpty()
        retrievedBalances.first().groupId shouldBe "group1"
        retrievedBalances.first().users.size shouldBe 3
        retrievedBalances.first().users[0].userId shouldBe USER_ID
        retrievedBalances.first().users.map { it.value } shouldContainExactlyInAnyOrder listOf("5", "-2", "-3").map { it.toBigDecimal() }
    }

    should("return empty list when no balances found for groupId") {
        // given
        val nonExistingGroupId = "non-existing-group"

        // when
        val retrievedBalances = balancesRepository.getBalances(nonExistingGroupId)

        // then
        retrievedBalances.shouldBeEmpty()
    }
},)
