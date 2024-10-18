package pl.edu.agh.gem.external.persistence.settlements

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import pl.edu.agh.gem.internal.model.finance.settelment.SettlementStatus.PENDING
import pl.edu.agh.gem.internal.model.finance.settelment.Settlements
import pl.edu.agh.gem.internal.persistence.SettlementsRepository

@Repository
class MongoSettlementsRepository(
    private val mongoOperations: MongoOperations,
) : SettlementsRepository {
    override fun save(settlements: Settlements): Settlements {
        return mongoOperations.save(settlements.toEntity()).toDomain()
    }

    override fun getSettlements(groupId: String): List<Settlements> {
        val query = Query.query(Criteria.where("${SettlementsEntity::id.name}.${SettlementsCompositeKey::groupId.name}").isEqualTo(groupId))
        return mongoOperations.find(query, SettlementsEntity::class.java).map(SettlementsEntity::toDomain)
    }

    override fun blockSettlements(groupId: String, currency: String) {
        val query = Query.query(
            Criteria.where("${SettlementsEntity::id.name}.${SettlementsCompositeKey::groupId.name}")
                .isEqualTo(groupId)
                .and("${SettlementsEntity::id.name}.${SettlementsCompositeKey::currency.name}")
                .isEqualTo(currency),
        )
        val update = Update.update(SettlementsEntity::status.name, PENDING)
        mongoOperations.updateFirst(query, update, SettlementsEntity::class.java)
    }
}
