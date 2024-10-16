package pl.edu.agh.gem.internal.settlement

import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.settlements.Settlement
import java.math.BigDecimal

class SettlementGenerator {

    fun generate(values: List<Balance>): List<Settlement> {
        var (remainingBalances, settlements) = removeBalancesSummingToZero(values.filter { it.value != BigDecimal.ZERO })
        
        return listOf()
    }

    private fun removeBalancesSummingToZero(balances: List<Balance>): Pair<List<Balance>, List<Settlement>> {
        val filteredBalances = balances.toMutableList()
        val settlements = mutableListOf<Settlement>()
        
        for (balance in balances) {
            val oppositeBalanceValue = balance.value.negate()
            val oppositeBalance = filteredBalances.find { it.value == oppositeBalanceValue }
            
            if (oppositeBalance != null) {
                filteredBalances.remove(oppositeBalance)
                settlements.add(toSettlement(balance, oppositeBalance))
            } else {
                filteredBalances.add(balance)
            }
        }
        
        return Pair(filteredBalances, settlements)

    }
    
    private fun createSettlementsUsingGreedyAlgorithm(balances: List<Balance>): List<Settlement>> {
        
    }
    
    
}
private fun toSettlement(a: Balance, b: Balance) = Settlement(
        payerId = if (a.value > BigDecimal.ZERO)  a.userId else b.userId,
        payeeId = if (a.value > BigDecimal.ZERO)  b.userId else a.userId,
        value = a.value.abs(),
)
