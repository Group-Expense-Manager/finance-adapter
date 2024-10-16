package pl.edu.agh.gem.external.dto.finance

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import pl.edu.agh.gem.external.dto.group.toDto
import pl.edu.agh.gem.helper.group.DummyGroup.GROUP_ID
import pl.edu.agh.gem.util.createCurrencySettlement
import pl.edu.agh.gem.util.createSettlement
import pl.edu.agh.gem.util.createSettlements

class SettlementsResponseTest : ShouldSpec({

    should("map Settlement to SettlementsDto") {
        // given
        val settlement = createSettlement()

        // when
        val settlementDto = settlement.toDto()

        // then
        settlementDto.also {
            it.payerId shouldBe settlement.payerId
            it.payeeId shouldBe settlement.payeeId
            it.value shouldBe settlement.value
        }
    }

    should("map CurrencySettlement  to CurrencyBalancesDto") {
        // given
        val currencySettlement = createCurrencySettlement()

        // when
        val currencySettlementDto = currencySettlement.toDto()

        // then
        currencySettlementDto.also {
            it.currency shouldBe currencySettlement.currency.toDto()
            it.settlements shouldBe currencySettlement.settlements.map { settlement -> settlement.toDto() }
        }
    }

    should("map Settlements  to SettlementsResponse") {
        // given
        val settlements = createSettlements()

        // when
        val settlementsResponse = settlements.toSettlementsResponse(GROUP_ID)

        // then
        settlementsResponse.also {
            it.groupId shouldBe GROUP_ID
            it.currencySettlements shouldBe settlements.map { currencySettlement -> currencySettlement.toDto() }
        }
    }
},)
