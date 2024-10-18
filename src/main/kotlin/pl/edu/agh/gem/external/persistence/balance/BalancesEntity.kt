package pl.edu.agh.gem.external.persistence.balance

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pl.edu.agh.gem.internal.model.finance.balance.Balance
import pl.edu.agh.gem.internal.model.finance.balance.Balances
import java.math.BigDecimal

@Document("balances")
data class BalancesEntity(
    @Id
    val id: BalancesCompositeKey,
    val balances: List<BalanceEntity>,
)

data class BalancesCompositeKey(
    val groupId: String,
    val currency: String,
)

fun BalancesEntity.toDomain(): Balances {
    return Balances(
        groupId = id.groupId,
        currency = id.currency,
        users = balances.map { it.toDomain() },
    )
}

fun Balances.toEntity(): BalancesEntity {
    return BalancesEntity(
        id = BalancesCompositeKey(
            groupId = groupId,
            currency = currency,
        ),
        balances = users.map { it.toEntity() },
    )
}

data class BalanceEntity(
    val userId: String,
    val balance: BigDecimal,
)

fun BalanceEntity.toDomain(): Balance {
    return Balance(
        userId = userId,
        value = balance,
    )
}

fun Balance.toEntity(): BalanceEntity {
    return BalanceEntity(
        userId = userId,
        balance = value,
    )
}
