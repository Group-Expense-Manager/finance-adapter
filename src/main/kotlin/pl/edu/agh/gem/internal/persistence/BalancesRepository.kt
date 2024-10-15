package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.finance.balance.Balances

interface BalancesRepository {
    fun save(balances: Balances): Balances
    fun getBalances(groupId: String): List<Balances>
}
