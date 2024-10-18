package pl.edu.agh.gem.internal.persistence

import pl.edu.agh.gem.internal.model.finance.settelment.Settlements

interface SettlementsRepository {
    fun save(settlements: Settlements): Settlements
    fun getSettlements(groupId: String): List<Settlements>
    fun blockSettlements(groupId: String, currency: String)
}
