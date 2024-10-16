package pl.edu.agh.gem.internal.model.finance.balance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.user.DummyUser.OTHER_USER_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.DummyData.CURRENCY_2
import pl.edu.agh.gem.util.createBalance
import pl.edu.agh.gem.util.createBalancesMap

class BalancesTest : ShouldSpec({

    should("map BalancesMap  to Balances") {
        // given
        val map = createBalancesMap()

        // when
        val balancesResponse = map.toBalances()

        // then
        balancesResponse.also {
            it shouldHaveSize 2
            it.first().also { first ->
                first.currency.code shouldBe CURRENCY_1
                first.balances.shouldContainExactly(
                    createBalance(userId = USER_ID, value = "1".toBigDecimal()),
                    createBalance(userId = OTHER_USER_ID, value = "-1".toBigDecimal()),

                )
            }
            it.last().also { last ->
                last.currency.code shouldBe CURRENCY_2
                last.balances.shouldContainExactly(
                    createBalance(userId = USER_ID, value = "2".toBigDecimal()),
                    createBalance(userId = OTHER_USER_ID, value = "-2".toBigDecimal()),

                )
            }
        }
    }
},)
