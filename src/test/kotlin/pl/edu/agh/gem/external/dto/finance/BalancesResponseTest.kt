package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.helper.user.DummyUser.USER_ID
import pl.edu.agh.gem.util.DummyData.CURRENCY_1
import pl.edu.agh.gem.util.createBalances
import java.math.BigDecimal

class BalancesResponseTest : ShouldSpec({

    should("map user-balance entry  to UserBalanceDto") {
        // given
        val entry = mapOf(USER_ID to BigDecimal.ONE).entries.first()

        // when
        val userBalanceDto = entry.toUserBalanceDto()

        // then
        userBalanceDto.also {
            it.userId shouldBe USER_ID
            it.balance shouldBe BigDecimal.ONE
        }
    }

    should("map currency-user-balances entry  to CurrencyBalancesDto") {
        // given
        val entry = createBalances().entries.first()

        // when
        val currencyBalancesDto = entry.toCurrencyBalancesDto()

        // then
        currencyBalancesDto.also {
            it.currency shouldBe CURRENCY_1
            it.userBalances shouldBe entry.value.entries.map { userBalance -> userBalance.toUserBalanceDto() }
        }
    }

    should("map Balances  to BalancesResponse") {
        // given
        val balances = createBalances()

        // when
        val balancesResponse = balances.toBalancesResponse(GROUP_ID)

        // then
        balancesResponse.also {
            it.groupId shouldBe GROUP_ID
            it.balances shouldBe balances.map { currencyBalances -> currencyBalances.toCurrencyBalancesDto() }
        }
    }
},)
