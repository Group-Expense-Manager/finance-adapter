package pl.edu.agh.gem.external.persistence.settlements

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.internal.model.finance.settelment.Settlement
import pl.edu.agh.gem.internal.model.finance.settelment.SettlementStatus
import pl.edu.agh.gem.internal.model.finance.settelment.Settlements
import java.math.BigDecimal

@Document("settlements")
data class SettlementsEntity(
    @Id
    val id: SettlementsCompositeKey,
    val status: SettlementStatus,
    val settlements: List<SettlementEntity>,
)

data class SettlementsCompositeKey(
    val groupId: String,
    val currency: String,
)

fun SettlementsEntity.toDomain(): Settlements {
    return Settlements(
        groupId = id.groupId,
        currency = id.currency,
        status = status,
        settlements = settlements.map { it.toDomain() },
    )
}

fun Settlements.toEntity(): SettlementsEntity {
    return SettlementsEntity(
        id = SettlementsCompositeKey(
            groupId = groupId,
            currency = currency,
        ),
        status = status,
        settlements = settlements.map { it.toEntity() },
    )
}

data class SettlementEntity(
    val fromUserId: String,
    val toUserId: String,
    val value: BigDecimal,
)

fun SettlementEntity.toDomain(): Settlement {
    return Settlement(
        fromUserId = fromUserId,
        toUserId = toUserId,
        value = value,
    )
}

fun Settlement.toEntity(): SettlementEntity {
    return SettlementEntity(
        fromUserId = fromUserId,
        toUserId = toUserId,
        value = value,
    )
}
