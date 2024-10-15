package pl.edu.agh.gem.external.persistence.balance

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import pl.edu.agh.gem.internal.persistence.BalancesRepository

@Repository
class MongoBalancesRepository(
    private val mongoOperations: MongoOperations,
) : BalancesRepository {
    override fun save(balances: Balances): Balances {
        return mongoOperations.save(balances.toEntity()).toDomain()
    }

    override fun getBalances(groupId: String): List<Balances> {
        val query = Query.query(Criteria.where("${BalancesEntity::id.name}.${BalancesCompositeKey::groupId.name}").isEqualTo(groupId))
        return mongoOperations.find(query, BalancesEntity::class.java).map(BalancesEntity::toDomain)
    }
}
