package pl.edu.agh.gem.internal.client

import pl.edu.agh.gem.internal.model.finance.Activity
import pl.edu.agh.gem.internal.model.finance.filter.ClientFilterOptions

interface PaymentManagerClient {
    fun getActivities(groupId: String, clientFilterOptions: ClientFilterOptions): List<Activity>
}

class PaymentManagerClientException(override val message: String?) : RuntimeException()

class RetryablePaymentManagerClientException(override val message: String?) : RuntimeException()
